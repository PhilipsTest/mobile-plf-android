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
import com.philips.platform.ccb.model.Activity
import com.philips.platform.ccb.model.BotResponseData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.ans_message_layout.view.*
import kotlinx.android.synthetic.main.fragment_ccbconversational.view.*
import kotlinx.android.synthetic.main.ques_meesage_layout.view.*
import kotlinx.android.synthetic.main.selection_views.view.*

/**
 * A simple [Fragment] subclass.
 */
class CCBConversationalFragment() : Fragment(), BotResponseListener {

    lateinit var moshi: Moshi

    lateinit var linearLayout1: LinearLayout
    lateinit var linearLayout: LinearLayout
    lateinit var scrollView: ScrollView
    private val TAG: String = CCBConversationalFragment::class.java.simpleName


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val rootview = inflater.inflate(R.layout.fragment_ccbconversational, container, false)

        linearLayout1 = rootview.recentChat
        linearLayout = rootview.selectionViewContainer
        scrollView = rootview.scrollview

        moshi = Moshi.Builder()
                // ... add your own JsonAdapters and factories ...
                .add(KotlinJsonAdapterFactory())
                .build()

        Log.i(TAG, "onCreateView")
        openWebSocket()
        postMessage("Welcome")

        return rootview;
    }

    fun openWebSocket() {
        val ccbWebSocketConnection = CCBWebSocketConnection()
        ccbWebSocketConnection.setBotResponseListener(this)
        ccbWebSocketConnection!!.createWebSocket()
    }

    @Synchronized fun postMessage(message: String?) {
        val ccbAzureConversationHandler = CCBAzureConversationHandler()
        ccbAzureConversationHandler.postMessage(message) { b, _ ->
            if (b) {
                Log.i(TAG, "postMessage success : " + message)
            } else {
                Log.i(TAG, "postMessage failed")
            }
        }
    }

    override fun onMessageReceived(jsonResponse: String) {
        try {
            Log.i(TAG, "CCBConversational :->$jsonResponse")
            val jsonAdapter: JsonAdapter<BotResponseData> = moshi.adapter(BotResponseData::class.java)
            val botResponseData: BotResponseData? = jsonAdapter.fromJson(jsonResponse)
            Log.i(TAG, "CCBConversational :->$botResponseData.toString()")
            /*if(!botResponseData?.activities?.get(0)?.conversation?.id?.equals(CCBManager.conversationId)!! ){
                Log.i(TAG,"Conversation id is not valid")
               // return
            }*/
            val responseActivity = botResponseData?.activities?.get(0)

            if (responseActivity?.text.equals("Welcome") || responseActivity?.text.equals("Privacy") || responseActivity?.text.equals("Start Activities"))
                return
            else if (responseActivity?.text.equals("Hello I am your advisor bot")) {
                postMessage("Privacy")
            } else if (responseActivity?.text?.contains("Philips Privacy Notice")!!) {
                postMessage("Start Activities")
            }

            parseResponse(botResponseData)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun parseResponse(botResponseData: BotResponseData?) {
        val activities = botResponseData?.activities;
        activities?.forEach {
            val id = it.from.id
            if (id == "AUserOne") updateBotResponsetoUi(it.text, false)

            if (id == "karanservice-bot" && !botResponseData?.activities?.get(0)?.replyToId?.contains(CCBManager.conversationId,true)!!) return

            if (id == "karanservice-bot" && it.attachments?.size != 0) updateActionUI(it)

            if (id == "karanservice-bot" && (it.attachments?.size == 0 ||
                            it.attachments?.get(0)?.content == null)) updateBotResponsetoUi(it.text, true)
        }
    }

    private fun updateBotResponsetoUi(msg: String, isResponseFromBot: Boolean) {
        /* if(msg.equals("Privacy") || msg.equals("Start Activities"))
             return*/
        this.activity?.runOnUiThread {
            if (isResponseFromBot) {
                val view = layoutInflater.inflate(R.layout.ques_meesage_layout, linearLayout1, false)
                if (msg.contains("Privacy", true)) {
                    val spannable = SpannableHelper.getSpannable(msg, "**", "**", LinkSpanClickListener { })
                    view.ques_chat_text.text = spannable
                } else {
                    view.ques_chat_text.text = msg
                }
                linearLayout1.addView(view)
            } else {
                val view = layoutInflater.inflate(R.layout.ans_message_layout, linearLayout1, false)
                view.ans_chat_text.text = msg
                linearLayout1.addView(view)
            }
            scrollToBottom()
        }
    }

    private fun updateActionUI(activity: Activity) {
        if (activity?.attachments?.size == 0) return

        if (activity?.attachments?.get(0)?.content == null) return

        this.activity?.runOnUiThread {
            linearLayout.removeAllViews()
            val view = layoutInflater.inflate(R.layout.selection_views, linearLayout)
            view.selectionTitle.text = activity?.text
            val resButton1 = activity.attachments?.get(0)?.content?.buttons?.get(0)
            val resButton2 = activity.attachments?.get(0)?.content?.buttons?.get(1)
            view.button1.text = resButton1?.title
            view.button2.text = resButton2?.title

            view.button1.setOnClickListener {
                postMessage(resButton1?.value)
                updateBotResponsetoUi(activity.text, true)
            }

            view.button2.setOnClickListener {
                postMessage(resButton2?.value)
                updateBotResponsetoUi(activity.text, true)
            }
        }
    }

    fun scrollToBottom(){
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

}
