package com.philips.platform.ccb.fragment


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.philips.platform.ccb.integration.FetchAppDataHandler.DeviceSettingsListener
import com.philips.platform.ccb.listeners.BotResponseListener
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.manager.CCBSettingManager
import com.philips.platform.ccb.model.Activity
import com.philips.platform.ccb.model.BotResponseData
import com.philips.platform.ccb.model.Button
import com.philips.platform.ccb.model.CCBUser
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.bot_response_layout.view.*
import kotlinx.android.synthetic.main.botreponse_waiting_layout.view.*
import kotlinx.android.synthetic.main.customer_survey_layout.view.*
import kotlinx.android.synthetic.main.dynamic_button.view.*
import kotlinx.android.synthetic.main.fragment_ccb.*
import kotlinx.android.synthetic.main.fragment_ccbconversational.view.*
import kotlinx.android.synthetic.main.user_response_layout.view.*
import net.frakbot.jumpingbeans.JumpingBeans


/**
 * A simple [Fragment] subclass.
 */
class CCBConversationalFragment() : Fragment(), BotResponseListener {

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


        rootView = inflater.inflate(layout.fragment_ccbconversational, container, false)

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
        val ccbUser = CCBUser(CCBUrlBuilder.SECRET_KEY, "EMS", "")
        CCBManager.getCCBSessionHandlerInterface().authenticateUser(ccbUser) { success, ccbError ->
            if (success) {
                Log.i(TAG, "Authenticated success!!")
                startConverssation()
            }

            if (ccbError != null) {
                progressBar.visibility = View.GONE
                Log.i(TAG, "Authentication failed!!")
                showToastOnError()
                closeConversation()
            }
        }
    }

    private fun startConverssation() {
        CCBManager.getCCBSessionHandlerInterface().startConversation { ccbConversation, ccbError ->
            if (ccbConversation != null) {
                Log.i(TAG, "Conversation started!!")
                openWebSocket()
            }
            if (ccbError != null) {
                Log.i(TAG, "Conversation Failed!!")
                progressBar.visibility = View.GONE
                showToastOnError()
                closeConversation()
            }
        }
    }

    fun openWebSocket() {
        ccbWebSocketConnection.setBotResponseListener(this)
        ccbWebSocketConnection!!.createWebSocket()
    }

    fun postMessage(message: String?) {
        message?.let { displayUserResponse(it) }
        if (!message?.contains("FOLLOW_UP-")!!)
            waitForBotResponse()
        ccbAzureConversationHandler.postMessage(message) { conversation, _ ->
            if (conversation != null) {
                Log.i(TAG, "postMessage : $message :: ${conversation?.id}")
            } else {
                Log.i(TAG, "postMessage failed")
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
                Log.d(TAG, "Update failed")
            }
        }
    }

    override fun onFailure() {
        //showToastOnError()
        //closeConversation()
    }

    override fun onMessageReceived(jsonResponse: String) {
        try {
            val botResponseData = Gson().fromJson(jsonResponse, BotResponseData::class.java)
            Log.d(TAG, "CCBConversational :->${botResponseData}")

            val activity: Activity = botResponseData?.activities?.get(0) ?: return

            if (activity.text.contains("Philips Privacy Notice")) {
                postMessage(INIT_START)
            }

            if (botResponseData?.watermark == null) return

            Log.i(TAG, "prevBotResponse : $prevBotResponse")
            if (prevBotResponse != null && prevBotResponse.equals(activity.text, false)) return
            prevBotResponse = activity.text

            removeWaitingView {
                if (activity.text.contains("APP_SYNC") || activity.text.contains("FOLLOW_UP")) {
                    handleAppSync(activity)
                } else if (activity.text.equals("Customer_Survey", false))
                    userReviewView()
                else
                    handleBotResponse(activity)

            }

        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onClosed() {

    }

    private fun handleBotResponse(activity: Activity, performAction: String? = null) {
        val msg = activity.text
        if (activity.attachments == null || activity.attachments.isEmpty())
            displayBotRespon(msg, false)
        else
            updateActionUI(activity.text, buttons = activity.attachments[0].content.buttons, performAction = performAction)
    }

    private fun displayUserResponse(text: String) {
        if (text.contains("FOLLOW_UP") || text.equals(INIT_START) || text.contains("APP_SYNC"))
            return

        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(layout.user_response_layout, rootView.ccb_recentchat_view, false)
            view.user_response_text.text = text
            rootView.ccb_recentchat_view.addView(view)
            rootView.avatarIV.visibility = View.INVISIBLE
        }
    }

    private fun displayBotRespon(msg: String, isActions: Boolean) {
        Log.i(TAG, "msg : $msg")
        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(layout.bot_response_layout, rootView.ccb_recentchat_view, false)

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
        }
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

    private fun handleAppSync(activity: Activity) {
        var msg = activity.text
        var msgToHandle: String? = null
        for (text: String in msg.split(" ")) {
            if (text.contains("FOLLOW_UP")) {
                val key = text?.split('-')?.get(1)
                msg = msg.replace(text, "", true)
                activity.text = msg
                postMessage(text)
                if (!msg.contains("APP_SYNC")) {
                    handleBotResponse(activity)
                    return
                }
            }

            if (text.contains("APP_SYNC")) {
                msgToHandle = text
            }
        }


        when (val key = msgToHandle?.split('-')?.get(1)) {
            "GET_WiFi" -> {
                if (msgToHandle != null)
                    msg = msg.replace(msgToHandle, "...", true)
                activity.text = msg
                handleBotResponse(activity)
                val dataFromApp = CCBSettingManager.fetchAppDataHandler?.getDataFromApp(key)
                Log.i(TAG, "handleAppSync : $dataFromApp")
                if (dataFromApp != null)
                    postMessage("APP_SYNC-GET_WiFi-$dataFromApp")
            }

            "PUT_SSID" -> {
                val dataFromApp = CCBSettingManager.fetchAppDataHandler?.getDataFromApp(key)
                Log.i(TAG, "handleAppSync : $dataFromApp")
                if (dataFromApp != null) {
                    if (msgToHandle != null)
                        msg = msg.replace(msgToHandle, dataFromApp, true)
                    activity.text = msg
                    handleBotResponse(activity)
                }
            }

            "ACTION_WiFi" -> {
                if (msgToHandle != null)
                    msg = msg.replace(msgToHandle, "", true)
                activity.text = msg
                handleBotResponse(activity, key)
            }
        }
    }


    private fun updateActionUI(ques: String, buttons: List<Button>, performAction: String? = null) {
        displayBotRespon(ques, true)
        this.activity?.runOnUiThread {
            rootView.ccb_actionbutton_view.removeAllViews()
            for (button: Button in buttons) {
                val view = layoutInflater.inflate(layout.dynamic_button, rootView.ccb_actionbutton_view, false)
                val button_view = view.ccb_dynamic_button
                button_view.setText(button.title)
                button_view.setOnClickListener {
                    rootView.ccb_actionbutton_view.removeAllViews()
                    if (performAction == null) {
                        postMessage(button.title)
                    } else {
                        changeDeviceSettings(performAction)
                    }
                }
                rootView.ccb_actionbutton_view.addView(view)
            }
        }
    }

    private fun userReviewView() {
        displayBotRespon("Please rate your experience", false)
        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(layout.customer_survey_layout, rootView.ccb_actionbutton_view, false)
            rootView.ccb_actionbutton_view.addView(view)

            view.btn_review_happy.setOnClickListener { updateUserReview("Good") }
            view.btn_review_average.setOnClickListener { updateUserReview("Average") }
            view.btn_review_sad.setOnClickListener { updateUserReview("Poor") }
        }
    }

    private fun updateUserReview(review: String) {
        rootView.ccb_actionbutton_view.removeAllViews()
        displayUserResponse(review)
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

    private fun changeDeviceSettings(performAction: String?) {
        performAction?.let {
            CCBSettingManager.fetchAppDataHandler?.changeDeviceSettings(it, object : DeviceSettingsListener {
                override fun onSuccess(successAction: String) {
                    postMessage(successAction)
                }

                override fun onFailure() {
                    Log.i(TAG, "onFailure")
                }
            })
        }
    }

    private fun showToastOnError() {
        activity?.runOnUiThread {
            Toast.makeText(context, "Failed to connect to the bot.", Toast.LENGTH_SHORT).show()
        }
    }
}


