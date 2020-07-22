package com.philips.platform.ccb.fragment


import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.gson.Gson
import com.philips.platform.ccb.R
import com.philips.platform.ccb.R.layout
import com.philips.platform.ccb.directline.CCBAzureConversationHandler
import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.directline.CCBWebSocketConnection
import com.philips.platform.ccb.listeners.BotResponseListener
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.model.CCBActions
import com.philips.platform.ccb.model.CCBActivities
import com.philips.platform.ccb.model.CCBMessage
import com.philips.platform.ccb.util.CCBLog
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.ccb_bot_response_layout.view.*
import kotlinx.android.synthetic.main.ccb_conversation_fragment.view.*
import kotlinx.android.synthetic.main.ccb_dynamic_button.view.*
import kotlinx.android.synthetic.main.ccb_user_response_layout.view.*


/**
 * A simple [Fragment] subclass.
 */
class CCBConversationalFragment() : Fragment(), BotResponseListener {


    private lateinit var rootView: View
    private val TAG: String = CCBConversationalFragment::class.java.simpleName
    private val INIT_WELCOME = "Welcome"
    private val INIT_PRIVACY = "Privacy"
    private val INIT_START = "START_BOT"
    private lateinit var ccbAzureConversationHandler: CCBAzureConversationHandler
    private lateinit var ccbAzureSessionHandler: CCBAzureSessionHandler
    private val ccbWebSocketConnection: CCBWebSocketConnection = CCBWebSocketConnection()

    private lateinit var markwon: Markwon


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        rootView = inflater.inflate(R.layout.ccb_conversation_fragment, container, false)

        ccbAzureConversationHandler = CCBAzureConversationHandler()

        markwon = Markwon.create(context!!)

        Log.i(TAG, "onCreateView")

        initViewListener()

        openWebSocket()

        return rootView;
    }

    private fun initViewListener() {
        rootView.fbclosebutton.setOnClickListener {
            closeConversation()
        }

        rootView.fbrestartbutton.setOnClickListener {
            postMessage("Restart Conversation")
        }

        rootView.ccb_recentchat_view.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                handleFabVisibility()
            }

            override fun onChildViewRemoved(parent: View, child: View) {
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

    fun openWebSocket() {
        ccbWebSocketConnection.setBotResponseListener(this)
        ccbWebSocketConnection!!.createWebSocket()
    }

    fun postMessage(message: String?) {
        CCBLog.d(TAG, "posting message : $message")
        ccbAzureConversationHandler.postMessage(message) { conversation, _ ->
            if (conversation != null) {
                CCBLog.d(TAG, "postMessage success : $message")
            } else {
                CCBLog.d(TAG, "postMessage failed : $message")
            }
        }
    }

    override fun onOpen() {
        ccbAzureSessionHandler.updateConversation{ success, ccbError ->
            if(success){

            }
            if(ccbError != null){
                CCBLog.d(TAG, "Update failed")
            }
        }
    }

    override fun onFailure() {
    }

    override fun onMessageReceived(jsonResponse: String) {
        disableProgressBar()
        try {
            val botResponseData = Gson().fromJson(jsonResponse, CCBMessage::class.java)

            val activity: CCBActivities = botResponseData?.activities?.get(0) ?: return
            if (activity.text.equals("Ping")) return

            if (botResponseData?.watermark == null) {
                displayUserResponse(activity.text)
            } else {
                if (activity.text.contains("APP_SYNC")) {
                    //handleAppSync(activity)
                    return
                }
                handleBotResponse(activity)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }


    override fun onClosed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handleBotResponse(activity: CCBActivities) {
        val msg = activity.text

        if (activity.suggestedActions == null || activity.suggestedActions.actions.isEmpty())
            displayBotRespon(msg, false)
        else
            updateActionUI(activity.text, buttons = activity.suggestedActions.actions)


    }

    private fun displayUserResponse(text: String) {
        if (text.equals(INIT_WELCOME) || text.equals(INIT_PRIVACY) || text.equals(INIT_START) || text.contains("APP_SYNC"))
            return

        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(layout.ccb_user_response_layout, rootView.ccb_recentchat_view, false)
            view.user_response_text.text = text
            rootView.ccb_recentchat_view.addView(view)
            rootView.avatarIV.visibility = View.INVISIBLE
            scrollToBottom()
        }
    }

    private fun displayBotRespon(msg: String, isActions: Boolean) {
        this.activity?.runOnUiThread {
            if (!isActions)
                rootView.ccb_actionbutton_view.removeAllViews()

            val view = layoutInflater.inflate(layout.ccb_bot_response_layout, rootView.ccb_recentchat_view, false)

            context?.let {
                val markwon = Markwon.builder(it).usePlugin(GlideImagesPlugin.create(object : GlideImagesPlugin.GlideStore {

                    override fun cancel(target: com.bumptech.glide.request.target.Target<*>) {
                        Glide.with(it).clear(target);
                    }

                    override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
                        view.bot_response_imageview.visibility = View.VISIBLE
                        view.bot_response_imageview.requestFocus()
                        val request = Glide.with(it).load(drawable.getDestination())
                        request.into(view.bot_response_imageview)
                        return request
                    }
                }
                )).usePlugin(object : AbstractMarkwonPlugin() {
                    override fun afterSetText(textView: TextView) {
                        val text = textView.text
                        if (text.contains("\n\nImage")) {
                            textView.text = text.toString().replace("\n\nImage", "")
                        }
                        super.afterSetText(textView)
                    }
                }).build()
                markwon.setMarkdown(view.bot_response_text, msg)
            }
            rootView.ccb_recentchat_view.addView(view)
            rootView.avatarIV.visibility = View.VISIBLE
            scrollToBottom()
        }
    }

    /*private fun handleAppSync(activity: CCBActivities) {
        var msg = activity.text
        var msgToHandle: String? = null
        for (text: String in msg.split(" ")) {
            if (text.contains("APP_SYNC")) {
                msgToHandle = text
            }
        }

        when (val key = msgToHandle?.split('+')?.get(1)) {
            "GET_WiFi" -> {
                val dataFromApp = CCBSettingManager.fetchAppDataHandler?.getDataFromApp(key)
                Log.i(TAG, "handleAppSync : $dataFromApp")
                if (dataFromApp != null)
                    postMessage("APP_SYNC+GET_WiFi $dataFromApp")
            }

            "PUT_SSD" -> {
                val dataFromApp = CCBSettingManager.fetchAppDataHandler?.getDataFromApp(key)
                Log.i(TAG, "handleAppSync : $dataFromApp")
                if (dataFromApp != null) {
                    if (msgToHandle != null)
                        msg = msg.replace(msgToHandle, dataFromApp, true)
                    activity.text = msg
                    handleBotResponse(activity)
                }
            }
        }
    }*/


    private fun updateActionUI(ques: String, buttons: List<CCBActions>) {
        displayBotRespon(ques, true)
        this.activity?.runOnUiThread {
            rootView.ccb_actionbutton_view.removeAllViews()

            for (button: CCBActions in buttons) {
                val view = layoutInflater.inflate(layout.ccb_dynamic_button, rootView.ccb_actionbutton_view, false)
                val button_view = view.ccb_dynamic_button
                button_view.setText(button.title)
                button_view.setOnClickListener {
                    postMessage(button.title)
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
            if (b) activity?.finish()
        }
    }

    fun disableProgressBar() {
        this.activity?.runOnUiThread {
            if (rootView.conversation_progressbar.visibility == View.VISIBLE)
                rootView.conversation_progressbar.visibility = View.GONE
        }
    }
}


