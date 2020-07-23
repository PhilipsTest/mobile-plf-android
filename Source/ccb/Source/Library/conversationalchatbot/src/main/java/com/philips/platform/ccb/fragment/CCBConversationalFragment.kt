package com.philips.platform.ccb.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.philips.platform.ccb.R
import com.philips.platform.ccb.directline.CCBAzureConversationHandler
import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.directline.CCBWebSocketConnection
import com.philips.platform.ccb.listeners.BotResponseListener
import com.philips.platform.ccb.model.CCBActions
import com.philips.platform.ccb.model.CCBActivities
import com.philips.platform.ccb.model.CCBMessage
import com.philips.platform.ccb.util.CCBLog
import kotlinx.android.synthetic.main.ccb_bot_response_layout.view.*
import kotlinx.android.synthetic.main.ccb_conversation_fragment.view.*
import kotlinx.android.synthetic.main.ccb_dynamic_button.view.*
import kotlinx.android.synthetic.main.ccb_user_response_layout.view.*
import net.frakbot.jumpingbeans.JumpingBeans
import pl.droidsonroids.gif.GifImageView


class CCBConversationalFragment : Fragment(), BotResponseListener {

    private lateinit var chatLayout: LinearLayout
    private lateinit var selectionViewLayout: LinearLayout
    private lateinit var scrollView: ScrollView
    private val TAG: String = CCBConversationalFragment::class.java.simpleName
    private lateinit var ccbAzureConversationHandler: CCBAzureConversationHandler
    private lateinit var ccbAzureSessionHandler: CCBAzureSessionHandler
    lateinit var typing : GifImageView
    private lateinit var botView: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val rootview = inflater.inflate(R.layout.ccb_conversation_fragment, container, false)

        chatLayout = rootview.ccb_chat_layout
        selectionViewLayout = rootview.ccb_selectionView_layout
        scrollView = rootview.ccb_scrollview
        ccbAzureConversationHandler = CCBAzureConversationHandler()
        ccbAzureSessionHandler = CCBAzureSessionHandler()
        openWebSocket()

        return rootview;
    }

    fun openWebSocket() {
        val ccbWebSocketConnection = CCBWebSocketConnection()
        ccbWebSocketConnection.setBotResponseListener(this)
        ccbWebSocketConnection.createWebSocket()
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
        try {
            val botResponseData = Gson().fromJson(jsonResponse, CCBMessage::class.java)
            CCBLog.d(TAG, "CCBConversational :->$botResponseData.toString()")
            val activity: CCBActivities = botResponseData?.activities?.get(0) ?: return

            if (botResponseData.watermark == null && activity.type.equals("message")) {
                CCBLog.d(TAG, "watermark null")
                displayUserResponse(activity.text)
            } else if(activity.text!=null){
                CCBLog.d(TAG, "watermark not null")
                this.activity?.runOnUiThread {
                    botView = layoutInflater.inflate(R.layout.ccb_bot_response_layout, chatLayout, false)
                    chatLayout.addView(botView)
                    JumpingBeans.with(botView.text1)
                          .appendJumpingDots()
                          .build()
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
        CCBLog.d(TAG, "handleBotResponse")

        if (activity.suggestedActions != null && activity.suggestedActions.actions.isNotEmpty()) {
            updateActionUI(activity.text, buttons = activity.suggestedActions.actions)
        }

//        if (!CCBManager.conversationId?.let { activity.replyToId?.contains(it) }!!) {
//            return
//        }

        displayBotRespon(activity.text)
    }


    private fun displayUserResponse(text: String?) {

        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(R.layout.ccb_user_response_layout, chatLayout, false)
            view.ccb_user_response_text.text = text
            chatLayout.addView(view)
            scrollToBottom()
        }
    }

    private fun displayBotRespon(msg: String?) {
        Thread.sleep(1000)
        this.activity?.runOnUiThread {
            //val view = layoutInflater.inflate(R.layout.ccb_bot_response_layout, chatLayout, false)
            botView.ccb_bot_response_text?.text = msg
            val animFadein: Animation = AnimationUtils.loadAnimation(context,
                    R.anim.bounce)

              /*JumpingBeans.with(view!!.text1)
                    .appendJumpingDots()
                    .build()*/
            //view.text1.startAnimation(animFadein)
            botView.bot_response?.visibility= View.VISIBLE
           if((msg.toString().contains("Privacy"))|| (msg.toString().contains("SleepMapper"))) {
               postMessage("Tuscany")
           }
            //chatLayout.addView(view)
            scrollToBottom()
        }
    }


    private fun updateActionUI(ques: String?, buttons: List<CCBActions>) {
        this.activity?.runOnUiThread {
            selectionViewLayout.removeAllViews()
            for (button: CCBActions in buttons) {
                val view = layoutInflater.inflate(R.layout.ccb_dynamic_button, selectionViewLayout, false)
                val ccbButtons = view.ccb_dynamic_button
                ccbButtons.setText(button.title)
                ccbButtons.setOnClickListener {
                    postMessage(button.title)
                }
                selectionViewLayout.addView(view)
            }
            scrollToBottom()
        }
    }

    fun scrollToBottom() {
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

}


