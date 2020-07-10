package com.philips.platform.ccb.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.philips.platform.ccb.R
import com.philips.platform.ccb.constant.LinkSpanClickListener
import com.philips.platform.ccb.constant.SpannableHelper
import com.philips.platform.ccb.directline.CCBAzureConversationHandler
import com.philips.platform.ccb.directline.CCBWebSocketConnection
import com.philips.platform.ccb.listeners.BotResponseListener
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.model.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.user_response_layout.view.*
import kotlinx.android.synthetic.main.fragment_ccbconversational.view.*
import kotlinx.android.synthetic.main.bot_response_layout.view.*
import kotlinx.android.synthetic.main.buttons_layout.view.*
import kotlinx.android.synthetic.main.dynamic_button.*
import kotlinx.android.synthetic.main.dynamic_button.view.*

/**
 * A simple [Fragment] subclass.
 */
class CCBConversationalFragment() : Fragment(), BotResponseListener {

    lateinit var moshi: Moshi

    private lateinit var linearLayout1: LinearLayout
    private lateinit var linearLayout: LinearLayout
    private lateinit var scrollView: ScrollView
    private val TAG: String = CCBConversationalFragment::class.java.simpleName
    private val INIT_WELCOME = "Welcome"
    private val INIT_PRIVACY = "Privacy"
    private val INIT_START = "START_ACTIVITY"
    //private var conversationidPair: Pair<String, String>? = null
    private var recentSentMessageID: String? = null;
    private lateinit var ccbAzureConversationHandler: CCBAzureConversationHandler


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val rootview = inflater.inflate(R.layout.fragment_ccbconversational, container, false)

        linearLayout1 = rootview.recentChat
        linearLayout = rootview.selectionViewContainer
        scrollView = rootview.scrollview
        ccbAzureConversationHandler = CCBAzureConversationHandler()

        moshi = Moshi.Builder()
                // ... add your own JsonAdapters and factories ...
                .add(KotlinJsonAdapterFactory())
                .build()

        Log.i(TAG, "onCreateView")
        openWebSocket()

        return rootview;
    }

    fun openWebSocket() {
        val ccbWebSocketConnection = CCBWebSocketConnection()
        ccbWebSocketConnection.setBotResponseListener(this)
        ccbWebSocketConnection!!.createWebSocket()
    }

    fun postMessage(message: String) {
        Log.i("SHASHI", "posting message : $message")
        ccbAzureConversationHandler.postMessage(message) { conversation, _ ->
            if (conversation != null) {
                Log.i("SHASHI", "postMessage success : $message")
                if (isInitMessage(message))
                    handleResponseToInitMsg(message)
            } else {
                Log.i("SHASHI", "postMessage failed")
            }
        }
    }

    override fun onOpen() {
        postMessage(INIT_WELCOME)
    }

    override fun onFailure() {
    }

    override fun onMessageReceived(jsonResponse: String) {
        try {
            val jsonAdapter: JsonAdapter<BotResponseData> = moshi.adapter(BotResponseData::class.java)
            val botResponseData: BotResponseData? = jsonAdapter.fromJson(jsonResponse)
            Log.i("SHASHI", "CCBConversational :->$botResponseData.toString()")

            val activity: Activity = botResponseData?.activities?.get(0) ?: return

            if (botResponseData?.watermark == null) {
                Log.i("SHASHI", "watermark null")
                //conversationidPair = Pair(activity.id, activity.text)
                displayUserResponse(activity.text)
            } else {
                Log.i("SHASHI", "watermark not null")
                handleBotResponse(activity)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun isInitMessage(text: String): Boolean {
        if (text.equals(INIT_WELCOME) || text.equals(INIT_PRIVACY) || text.equals(INIT_START))
            return true
        return false
    }

    private fun handleResponseToInitMsg(message: String) {
        Log.i("SHASHI", "handleResponseToInitMsg")

        if (message.equals(INIT_WELCOME)) {
            Log.i("SHASHI", "INIT_PRIVACY")
            postMessage(INIT_PRIVACY)
        } else if (message.equals(INIT_PRIVACY)) {
            Log.i("SHASHI", "INIT_START")
            postMessage(INIT_START)
        }
    }

    override fun onClosed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handleBotResponse(activity: Activity) {
        Log.i("SHASHI", "handleBotResponse")

        if (activity.attachments != null && activity.attachments[0].content.buttons.isNotEmpty()) {
            updateActionUI(activity.text, buttons = activity.attachments[0].content.buttons)
        }

        if (!activity.replyToId?.contains(CCBManager.conversationId)!!) {
            Log.i("SHASHI", "replyToId return")
            return
        }

        displayBotRespon(activity.text)
    }

    private fun displayUserResponse(text: String) {
        if (text.equals(INIT_WELCOME) || text.equals(INIT_PRIVACY) || text.equals(INIT_START))
            return

        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(R.layout.user_response_layout, linearLayout1, false)
            view.user_response_text.text = text
            linearLayout1.addView(view)
            scrollToBottom()
        }
    }

    private fun displayBotRespon(msg: String) {
        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(R.layout.bot_response_layout, linearLayout1, false)
            if (msg.contains("Privacy", true)) {
                val spannable = SpannableHelper.getSpannable(msg, "**", "**", LinkSpanClickListener { })
                view.bot_response_text.text = spannable
            } else {
                view.bot_response_text.text = msg
            }
            linearLayout1.addView(view)
            scrollToBottom()
        }
    }


    private fun updateActionUI(ques: String, buttons: List<Button>) {
        this.activity?.runOnUiThread {
            linearLayout.removeAllViews()
            // view.selectionTitle.text = ques

            for (button: Button in buttons) {
                val view = layoutInflater.inflate(R.layout.dynamic_button, linearLayout, false)
                val button_view = view.ccb_dynamic_button
                button_view.setText(button.title)
                button_view.setOnClickListener {
                    postMessage(button.title)
                }
                linearLayout.addView(view)
            }
        }
    }

    fun scrollToBottom() {
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
}

