package com.philips.platform.ccb.fragment


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spanned
import android.text.style.CharacterStyle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.philips.platform.ccb.R.layout
import com.philips.platform.ccb.constant.CCBUrlBuilder
import com.philips.platform.ccb.directline.CCBAzureConversationHandler
import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.directline.CCBWebSocketConnection
import com.philips.platform.ccb.listeners.BotResponseListener
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.model.CCBActions
import com.philips.platform.ccb.model.CCBActivities
import com.philips.platform.ccb.model.CCBMessage
import com.philips.platform.ccb.model.CCBUser
import com.philips.platform.ccb.util.CCBLog
import io.noties.markwon.Markwon
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.botreponse_waiting_layout.view.*
import kotlinx.android.synthetic.main.ccb_bot_response_layout.view.*
import kotlinx.android.synthetic.main.ccb_conversation_fragment.view.*
import kotlinx.android.synthetic.main.ccb_dynamic_button.view.*
import kotlinx.android.synthetic.main.ccb_fragment.view.*
import kotlinx.android.synthetic.main.ccb_user_response_layout.view.*
import net.frakbot.jumpingbeans.JumpingBeans


/**
 * A simple [Fragment] subclass.
 */
class CCBConversationalFragment : Fragment(), BotResponseListener {

    private lateinit var rootView: View
    private val TAG: String = CCBConversationalFragment::class.java.simpleName
    private val INIT_START = "Wake Up Light"
    private lateinit var ccbAzureConversationHandler: CCBAzureConversationHandler
    private lateinit var ccbAzureSessionHandler: CCBAzureSessionHandler
    private val ccbWebSocketConnection: CCBWebSocketConnection = CCBWebSocketConnection()
    private var prevBotResponse: String? = null
    private lateinit var watingResponseView: View
    private lateinit var jumpingBeans: JumpingBeans


    private lateinit var markwon: Markwon


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        rootView = inflater.inflate(layout.ccb_conversation_fragment, container, false)

        ccbAzureConversationHandler = CCBAzureConversationHandler()

        ccbAzureSessionHandler = CCBAzureSessionHandler()

        markwon = Markwon.create(context!!)

        initViewListener()

        connectChatBot()

        return rootView;
    }

    private fun initViewListener() {
        rootView.fbclosebutton.setOnClickListener {
            closeConversation()
        }

        rootView.fbrestartbutton.setOnClickListener {
            rootView.ccb_actionbutton_view.removeAllViews()
            postMessage("Restart Conversation")
        }

        rootView.ccb_recentchat_view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                scrollToBottom()
                handleFabVisibility()
            }
        })
    }

    private fun handleFabVisibility() {
        if (rootView.ccb_recentchat_view.childCount > 0) {
            rootView.fbrestartbutton.visibility = View.VISIBLE
            rootView.fbclosebutton.visibility = View.VISIBLE
        } else {
            rootView.fbrestartbutton.visibility = View.INVISIBLE
            rootView.fbclosebutton.visibility = View.INVISIBLE
        }
    }

    private fun connectChatBot() {
        val ccbUser = CCBUser(CCBUrlBuilder.HIDDEN_KNOCK, "EMS", "")
        CCBManager.getCCBSessionHandlerInterface().authenticateUser(ccbUser) { success, ccbError ->
            if (success) {
                Log.i(TAG, "Authenticated success!!")
                startConverssation()
            }

            if (ccbError != null) {
                rootView.ccb_progressBar.visibility = View.GONE
                Log.i(TAG, "Authentication failed!!")
                showToastOnError()
                closeConversation()
            }
        }
    }

    private fun startConverssation() {
        val ccbUser = CCBUser(CCBUrlBuilder.HIDDEN_KNOCK, "", "")
        CCBManager.getCCBSessionHandlerInterface().startConversation(ccbUser) { ccbConversation, ccbError ->
            if (ccbConversation != null) {
                Log.i(TAG, "Conversation started!!")
                openWebSocket()
            }
            if (ccbError != null) {
                Log.i(TAG, "Conversation Failed!!")
                rootView.ccb_progressBar.visibility = View.GONE
                showToastOnError()
                closeConversation()
            }
        }
    }

    fun openWebSocket() {
        ccbWebSocketConnection.setBotResponseListener(this)
        ccbWebSocketConnection.createWebSocket()
    }

    fun postMessage(message: String?) {
        message?.let { displayUserResponse(it) }
        if (!message?.contains("FOLLOW_UP-")!!)
            waitForBotResponse()
        ccbAzureConversationHandler.postMessage(message) { conversation, _ ->
            if (conversation != null) {
                CCBLog.d(TAG, "postMessage success : $message")
            } else {
                CCBLog.d(TAG, "postMessage failed : $message")
            }
        }
    }

    override fun onOpen() {
        disableProgressBar()
        waitForBotResponse()
        ccbAzureSessionHandler.updateConversation { success, ccbError ->
            if (success) {
                //postMessage()
            }
            if (ccbError != null) {
                CCBLog.d(TAG, "Update failed")
            }
        }
    }

    override fun onFailure() {
    }

    /*fun onMessageReceived2(jsonResponse: String) {
        try {
            val botResponseData = Gson().fromJson(jsonResponse, CCBMessage::class.java)
            CCBLog.d(TAG, "CCBConversational :->$botResponseData.toString()")
            val activity: CCBActivities = botResponseData?.activities?.get(0) ?: return

            if (botResponseData.watermark == null && activity.type.equals("message")) {
                CCBLog.d(TAG, "watermark null")
                displayUserResponse(activity.text)
            } else if(activity.text!=null){
                CCBLog.d(TAG, "watermark not null")
                handleBotResponse(activity)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }*/

    override fun onMessageReceived(jsonResponse: String) {
        try {
            val botResponseData = Gson().fromJson(jsonResponse, CCBMessage::class.java)
            CCBLog.d(TAG, "CCBConversational :->$botResponseData.toString()")

            val activity: CCBActivities = botResponseData?.activities?.get(0) ?: return

            if (botResponseData.watermark == null && activity.type.equals("message")) {
                CCBLog.d(TAG, "watermark null")
                //displayUserResponse(activity.text)
            } else if (activity.text != null) {
                CCBLog.d(TAG, "watermark not null")
                handleBotResponse(activity)
            }

        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }


    override fun onClosed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*private fun handleBotResponse(activity: CCBActivities) {
        CCBLog.d(TAG, "handleBotResponse")

        if (activity.suggestedActions != null && activity.suggestedActions.actions.isNotEmpty()) {
            updateActionUI(activity.text, buttons = activity.suggestedActions.actions)
        }

//        if (!CCBManager.conversationId?.let { activity.replyToId?.contains(it) }!!) {
//            return
//        }

        displayBotRespon(activity.text)
    }*/

    private fun handleBotResponse(activity: CCBActivities) {
        CCBLog.d(TAG, "handleBotResponse")

        if (activity.suggestedActions != null && activity.suggestedActions.actions.isNotEmpty()) {
            updateActionUI(activity.text, buttons = activity.suggestedActions.actions)
        }
        displayBotRespon(activity.text)
    }


    /*private fun displayUserResponse2(text: String?) {

        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(R.layout.ccb_user_response_layout, chatLayout, false)
            view.ccb_user_response_text.text = text
            chatLayout.addView(view)
            scrollToBottom()
        }
    }*/

    private fun displayUserResponse(text: String) {
        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(layout.ccb_user_response_layout, rootView.ccb_recentchat_view, false)
            view.user_response_text.text = text
            rootView.ccb_recentchat_view.addView(view)
            rootView.avatarIV.visibility = View.INVISIBLE
        }
    }

/*    private fun displayBotRespon2(msg: String?) {
        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(R.layout.ccb_bot_response_layout, chatLayout, false)
            view.bot_response_text.text = msg
            //chatLayout.addView(view)
            scrollToBottom()
        }
    }*/

    private fun displayBotRespon(msg: String) {
        Log.i(TAG, "msg : $msg")
        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(layout.ccb_bot_response_layout, rootView.ccb_recentchat_view, false)

            context?.let {
                val markwon = Markwon.builder(it).usePlugin(GlideImagesPlugin.create(object : GlideImagesPlugin.GlideStore {
                    override fun cancel(target: Target<*>) {
                        Glide.with(it).clear(target);
                    }

                    override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
                        view.bot_response_imageview.visibility = View.VISIBLE
                        val request = Glide.with(it).load(drawable.getDestination())
                        request.into(view.bot_response_imageview)
                        return request
                    }


                }
                )).build()

                        /*usePlugin(object : AbstractMarkwonPlugin() {

                    override fun beforeRender(node: Node) {
                        CCBLog.d(TAG, "beforeRender : ${node}")
                        //super.beforeRender(node)
                        //AsyncDrawableScheduler.unschedule(node)
                    }

                    override fun beforeSetText(textView: TextView, markdown: Spanned) {
                        CCBLog.d(TAG, "beforeSetText : ${textView.text}")
                        super.beforeSetText(textView, markdown)
                        markdown
                    }

                    override fun afterSetText(textView: TextView) {
                        *//* val text = textView.text
                         CCBLog.d(TAG,"afterSetText : ${textView.text}")
                         if (text.contains("Wake Up light")) {
                             textView.text = text.toString().replace("Wake Up light", "")
                         } *//*
                        AsyncDrawableScheduler.unschedule(textView)
                        super.afterSetText(textView)

                    }
                }).build()*/

                val toMarkdown = markwon.toMarkdown(msg)

                isContainsDrawableSpan(toMarkdown,view.bot_response_text,markwon,msg)

                /*if (!isContainsDrawableSpan(toMarkdown,view.bot_response_text,markwon))
                    markwon.setMarkdown(view.bot_response_text, msg)*/
            }
            rootView.ccb_recentchat_view.addView(view)
            rootView.avatarIV.visibility = View.VISIBLE
        }
    }

    private fun isContainsDrawableSpan(spanned: Spanned,view: View,markwon: Markwon,msg: String): Boolean{
        val spans = spanned.getSpans(0, spanned.length, CharacterStyle::class.java)
        if(spans.size == 0)
            return false

       /* for(i : Int in spans.indices){
            val characterStyle = spans[i]
            markwon.setParsedMarkdown(view.bot_response_text,characterStyle)
        }*/

       /* val a = intArrayOf(1, 2, 3, 4)
        for (i in a.indices) {
            val q = a[i]
        }*/

        for(charStyleSpan:CharacterStyle in spans){
            //if (charStyleSpan !is AsyncDrawableSpan){
                view.bot_response_text.append(charStyleSpan.toString())
            //}
        }
       // markwon.setMarkdown(view.bot_response_text, msg)
        return false
    }

    private fun waitForBotResponse() {
        activity?.runOnUiThread {
            watingResponseView = layoutInflater.inflate(layout.botreponse_waiting_layout, rootView.ccb_recentchat_view, false)
            rootView.avatarIV.visibility = View.VISIBLE
            watingResponseView.botresponse_waiting_text.visibility = View.VISIBLE
            jumpingBeans = JumpingBeans.with(watingResponseView.botresponse_waiting_text).appendJumpingDots().build()
            rootView.ccb_recentchat_view.addView(watingResponseView)
        }
    }

    private fun removeWaitingView(compltionHalder: () -> Unit) {
        activity?.runOnUiThread {
            jumpingBeans?.stopJumping()
            rootView.ccb_recentchat_view.removeView(watingResponseView)
            compltionHalder.invoke()
        }
    }

    private fun updateActionUI(ques: String, buttons: List<CCBActions>) {
        displayBotRespon(ques)
        this.activity?.runOnUiThread {
            rootView.ccb_actionbutton_view.removeAllViews()
            for (button: CCBActions in buttons) {
                val view = layoutInflater.inflate(layout.ccb_dynamic_button, rootView.ccb_actionbutton_view, false)
                val button_view = view.ccb_dynamic_button
                button_view.setText(button.title)
                button_view.setOnClickListener {
                    rootView.ccb_actionbutton_view.removeAllViews()
                    // if (performAction == null) {
                    postMessage(button.title)
                    // } else {
                    //   changeDeviceSettings(performAction)
                    //}
                }
                rootView.ccb_actionbutton_view.addView(view)
            }
        }
    }

    fun scrollToBottom() {
        rootView.scrollview.post {
            rootView.scrollview.fullScroll(View.FOCUS_DOWN)
        }
    }

    fun closeConversation() {
        ccbWebSocketConnection.closeWebSocket()
        CCBManager.getCCBSessionHandlerInterface().endConversation { b, ccbError ->
        }
        activity?.finish()
    }

    fun disableProgressBar() {
        this.activity?.runOnUiThread {
            if (rootView.conversation_progressbar.visibility == View.VISIBLE)
                rootView.conversation_progressbar.visibility = View.GONE
        }
    }

    private fun showToastOnError() {
        activity?.runOnUiThread {
            Toast.makeText(context, "Failed to connect to the bot.", Toast.LENGTH_SHORT).show()
        }
    }
}


