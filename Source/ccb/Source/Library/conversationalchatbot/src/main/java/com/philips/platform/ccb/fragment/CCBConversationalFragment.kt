package com.philips.platform.ccb.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.philips.platform.ccb.R
import com.philips.platform.ccb.directline.CCBAzureConversationHandler
import com.philips.platform.ccb.directline.CCBWebSocketConnection
import com.philips.platform.ccb.listeners.BotResponseListener
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.manager.CCBSettingManager
import com.philips.platform.ccb.model.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin

import kotlinx.android.synthetic.main.user_response_layout.view.*
import kotlinx.android.synthetic.main.fragment_ccbconversational.view.*
import kotlinx.android.synthetic.main.bot_response_layout.view.*
import kotlinx.android.synthetic.main.dynamic_button.view.*
import kotlinx.android.synthetic.main.fragment_ccbconversational.*


/**
 * A simple [Fragment] subclass.
 */
class CCBConversationalFragment() : Fragment(), BotResponseListener {

    lateinit var moshi: Moshi

    private lateinit var rootView: View
    private lateinit var linearLayout1: LinearLayout
    private lateinit var linearLayout: LinearLayout
    private lateinit var scrollView: ScrollView
    private val TAG: String = CCBConversationalFragment::class.java.simpleName
    private val INIT_WELCOME = "Welcome"
    private val INIT_PRIVACY = "Privacy"
    private val INIT_START = "START_BOT"
    private lateinit var ccbAzureConversationHandler: CCBAzureConversationHandler
    private val ccbWebSocketConnection: CCBWebSocketConnection = CCBWebSocketConnection()

    private lateinit var markwon: Markwon


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        rootView = inflater.inflate(R.layout.fragment_ccbconversational, container, false)

        linearLayout1 = rootView.recentChat
        linearLayout = rootView.selectionViewContainer
        scrollView = rootView.scrollview
        ccbAzureConversationHandler = CCBAzureConversationHandler()

        markwon = Markwon.create(context!!)


        moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        Log.i(TAG, "onCreateView")

        rootView.fbclosebutton.setOnClickListener {
            closeConversation()
        }

        rootView.fbrestartbutton.setOnClickListener {
            postMessage("Restart Conversation")
        }

        openWebSocket()

        return rootView;
    }

    fun openWebSocket() {
        ccbWebSocketConnection.setBotResponseListener(this)
        ccbWebSocketConnection!!.createWebSocket()
    }

    fun postMessage(message: String) {
        Log.i("SHASHI", "posting message : $message")
        ccbAzureConversationHandler.postMessage(message) { conversation, _ ->
            if (conversation != null) {
                Log.i("SHASHI", "postMessage success : $message")
                if (message.equals("Ping"))
                    postMessage(INIT_START)
            } else {
                Log.i("SHASHI", "postMessage failed")
            }
        }
    }


    override fun onOpen() {
        postMessage("Ping")
    }

    override fun onFailure() {
    }

    override fun onMessageReceived(jsonResponse: String) {
        try {
            val jsonAdapter: JsonAdapter<BotResponseData> = moshi.adapter(BotResponseData::class.java)
            val botResponseData: BotResponseData? = jsonAdapter.fromJson(jsonResponse)
            Log.i("SHASHI", "CCBConversational :->$botResponseData.toString()")

            val activity: Activity = botResponseData?.activities?.get(0) ?: return
            if (activity.text.equals("Ping")) return

            if (botResponseData?.watermark == null) {
                Log.i("SHASHI", "watermark null")
                displayUserResponse(activity.text)
            } else {
                Log.i("SHASHI", "watermark not null")
                if (activity.text.contains("APP_SYNC")) {
                    handleAppSync(activity)
                    return
                }
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

        val msg = activity.text

       // displayBotRespon(msg)

        if (activity.attachments == null || activity.attachments.isEmpty())
            displayBotRespon(msg,false)
        else
            updateActionUI(activity.text, buttons = activity.attachments[0].content.buttons)


    }

    private fun displayUserResponse(text: String) {
        if (text.equals(INIT_WELCOME) || text.equals(INIT_PRIVACY) || text.equals(INIT_START) || text.contains("APP_SYNC"))
            return

        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(R.layout.user_response_layout, linearLayout1, false)
            view.user_response_text.text = text
            linearLayout1.addView(view)
            rootView.avatarIV.visibility = View.INVISIBLE
            scrollToBottom()
        }
    }

    private fun displayBotRespon(msg: String,isActions:Boolean) {

        this.activity?.runOnUiThread {
            if (!isActions)
                linearLayout.removeAllViews()

            val view = layoutInflater.inflate(R.layout.bot_response_layout, linearLayout1, false)

            context?.let {
                val markwon = Markwon.builder(it).usePlugin(GlideImagesPlugin.create(it)).build()
                markwon.setMarkdown(view.bot_response_text, msg)
            }
            linearLayout1.addView(view)
            rootView.avatarIV.visibility = View.VISIBLE

            scrollToBottom()
        }
    }

    private fun handleAppSync(activity: Activity) {
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
                        msg = msg.replace(msgToHandle, dataFromApp,true)
                    activity.text = msg
                    handleBotResponse(activity)
                }
            }
        }
    }


    private fun updateActionUI(ques: String, buttons: List<Button>) {
        displayBotRespon(ques,true)
        this.activity?.runOnUiThread {
            linearLayout.removeAllViews()

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

    fun closeConversation() {
        ccbWebSocketConnection.closeWebSocket()
        CCBManager.getCCBSessionHandlerInterface().endConversation { b, ccbError ->
            if (b) activity?.finish()
        }
    }
}

