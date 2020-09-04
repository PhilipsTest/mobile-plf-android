/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.fragment


import android.graphics.drawable.Drawable
import android.os.Bundle
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
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.ccb.R.layout
import com.philips.platform.ccb.constant.CCBUrlBuilder
import com.philips.platform.ccb.directline.CCBAzureConversationHandler
import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.directline.CCBWebSocketConnection
import com.philips.platform.ccb.integration.CCBDeviceUtility
import com.philips.platform.ccb.listeners.BotResponseListener
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.manager.CCBSettingsManager
import com.philips.platform.ccb.model.CCBActions
import com.philips.platform.ccb.model.CCBActivities
import com.philips.platform.ccb.model.CCBMessage
import com.philips.platform.ccb.model.CCBUser
import com.philips.platform.ccb.util.CCBLog
import io.noties.markwon.Markwon
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.ccb_waiting_layout.view.*
import kotlinx.android.synthetic.main.ccb_bot_response_layout.view.*
import kotlinx.android.synthetic.main.ccb_conversation_fragment.view.*
import kotlinx.android.synthetic.main.ccb_dynamic_button.view.*
import kotlinx.android.synthetic.main.ccb_fragment.view.*
import kotlinx.android.synthetic.main.ccb_user_response_layout.view.*
import net.frakbot.jumpingbeans.JumpingBeans

class CCBConversationalFragment : Fragment(), BotResponseListener {

    private lateinit var rootView: View
    private val TAG: String = CCBConversationalFragment::class.java.simpleName
    private lateinit var ccbAzureConversationHandler: CCBAzureConversationHandler
    private lateinit var ccbAzureSessionHandler: CCBAzureSessionHandler
    private val ccbWebSocketConnection: CCBWebSocketConnection = CCBWebSocketConnection()
    private lateinit var watingResponseView: View
    private lateinit var jumpingBeans: JumpingBeans
    private var mLoggingInterface: LoggingInterface? = null
    private var ccbDeviceUtility: CCBDeviceUtility? = null
    private var postInProgress : Boolean = false
    private var onOpen : Boolean = true
    private var response :String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(layout.ccb_conversation_fragment, container, false)

        ccbDeviceUtility = context?.let { CCBDeviceUtility(it) }

        ccbAzureConversationHandler = CCBAzureConversationHandler()

        ccbAzureSessionHandler = CCBAzureSessionHandler()

        ccbWebSocketConnection.setBotResponseListener(this)

        mLoggingInterface = CCBSettingsManager.mLoggingInterface

        initViewListener()

        connectChatBot()

        return rootView;
    }

    private fun initViewListener() {
        rootView.fbclosebutton.setOnClickListener {
            closeConversation()
            activity?.finish()
        }

        rootView.fbrestartbutton.setOnClickListener {
            rootView.ccb_actionbutton_view.removeAllViews()
            onOpen = true
            closeConversation()
            connectChatBot()
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
        val ccbUser = CCBUser(CCBUrlBuilder.HIDDEN_KNOCK, "ems", "")
        CCBManager.getCCBSessionHandlerInterface().authenticateUser(ccbUser) { success, ccbError ->
            if (success) {
                startConverssation()
            }

            if (ccbError != null) {
                rootView.ccb_progressBar?.visibility = View.GONE
                showToastOnError()
                closeConversation()
            }
        }
    }

    private fun startConverssation() {
        val ccbUser = CCBUser(CCBUrlBuilder.HIDDEN_KNOCK, "", "")
        CCBManager.getCCBSessionHandlerInterface().startConversation(ccbUser) { ccbConversation, ccbError ->
            openWebSocket()
            if (ccbError != null) {
                rootView.ccb_progressBar.visibility = View.GONE
                showToastOnError()
                closeConversation()
            }
        }
    }

    private fun openWebSocket() {
        ccbWebSocketConnection.createWebSocket()
    }

    private fun postMessage(message: String?) {
        onOpen = false
        if(postInProgress.equals(false)) {
            message?.let { displayUserResponse(it) }
            waitForBotResponse()
            CCBLog.d(TAG, "before postMessage : $message")
            postInProgress = true
            ccbAzureConversationHandler.postMessage(message) { conversation, _ ->
                if (conversation != null) {
                    postInProgress = false
                    displayMessage(response)
                    CCBLog.d(TAG, "postMessage success : $message")
                } else {
                    CCBLog.d(TAG, "postMessage failed : $message")
                }
            }
        }
    }

    override fun onOpen() {
        disableProgressBar()
        waitForBotResponse()
        ccbAzureSessionHandler.updateConversation { success, ccbError ->
            if (success) {
                onOpen = true
                CCBLog.d(TAG, "updateConversation success")
            }
            if (ccbError != null) {
                CCBLog.d(TAG, "updateConversation failed")
            }
        }
    }

    override fun onFailure() {
    }

    override fun onMessageReceived(jsonResponse: String) {
        if(onOpen) {
            showMessage(jsonResponse)
        } else if(postInProgress && !onOpen) {
            response = jsonResponse
        } else {
            showMessage(jsonResponse)
        }
    }

    private fun showMessage(jsonResponse: String) {
        try {
            val botResponseData = Gson().fromJson(jsonResponse, CCBMessage::class.java)
            CCBLog.d(TAG, "CCBConversational :->$botResponseData.toString()")

            val activity: CCBActivities = botResponseData?.activities?.get(0) ?: return

            if (botResponseData.watermark == null && activity.type.equals("message")) {
                CCBLog.d(TAG, "watermark null")
            } else if (activity.text != null) {
                CCBLog.d(TAG, "watermark not null")
                removeWaitingView {
                    handleBotResponse(activity)
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun displayMessage(jsonResponse: String) {
        try {
            val botResponseData = Gson().fromJson(jsonResponse, CCBMessage::class.java)
            CCBLog.d(TAG, "CCBConversational :->$botResponseData.toString()")

            val activity: CCBActivities = botResponseData?.activities?.get(0) ?: return

            if (botResponseData.watermark == null && activity.type.equals("message")) {
                CCBLog.d(TAG, "watermark null")
            } else if (activity.text != null) {
                CCBLog.d(TAG, "watermark not null")
                removeWaitingView {
                    handleBotResponse(activity)
                }
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

        displayBotRespon(activity.text)
        if (activity.suggestedActions != null && activity.suggestedActions.actions.isNotEmpty()) {
            updateActionUI(activity.text, buttons = activity.suggestedActions.actions)
        }
    }

    private fun displayUserResponse(text: String) {
        this.activity?.runOnUiThread {
            val view = layoutInflater.inflate(layout.ccb_user_response_layout, rootView.ccb_recentchat_view, false)
            view.user_response_text.text = text
            rootView.ccb_recentchat_view.addView(view)
            rootView.avatarIV.visibility = View.INVISIBLE
        }
    }

    private fun displayBotRespon(msg: String) {
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
                        return request
                    }
                }
                )).build()

                markwon.setMarkdown(view.bot_response_text, msg)
            }
            rootView.ccb_recentchat_view.addView(view)
            rootView.avatarIV.visibility = View.VISIBLE
        }
    }

    private fun waitForBotResponse() {
        activity?.runOnUiThread {
            watingResponseView = layoutInflater.inflate(layout.ccb_waiting_layout, rootView.ccb_recentchat_view, false)
            rootView.avatarIV.visibility = View.VISIBLE
            watingResponseView.botresponse_waiting_text.visibility = View.VISIBLE
            jumpingBeans = JumpingBeans.with(watingResponseView.botresponse_waiting_text).appendJumpingDots().build()
            rootView.ccb_recentchat_view.addView(watingResponseView)
        }
    }

    private fun removeWaitingView(compltionHalder: () -> Unit) {
        activity?.runOnUiThread {
            jumpingBeans.stopJumping()
            rootView.ccb_recentchat_view.removeView(watingResponseView)
            compltionHalder.invoke()
        }
    }

    private fun updateActionUI(ques: String, buttons: List<CCBActions>) {
        this.activity?.runOnUiThread {
            rootView.ccb_actionbutton_view.removeAllViews()
            for (button: CCBActions in buttons) {
                if (isDataExchangeCommand(button.title)) {
                    ccbDeviceUtility?.performCommand(button.title) {
                        postMessage(it)
                    }
                } else {
                    val view = layoutInflater.inflate(layout.ccb_dynamic_button, rootView.ccb_actionbutton_view, false)
                    val button_view = view.ccb_dynamic_button
                    button_view.setText(button.title)
                    button_view.setOnClickListener {
                        rootView.ccb_actionbutton_view.removeAllViews()
                        CCBLog.d(TAG, "ButtonClick : ${button_view.text}")
                        postMessage(button.title)
                    }
                    rootView.ccb_actionbutton_view.addView(view)
                }
            }
        }
    }

    private fun isDataExchangeCommand(actionTitle: String): Boolean {
        return actionTitle.split(":").size == 3
    }


    fun scrollToBottom() {
        rootView.scrollview.post {
            rootView.scrollview.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun closeConversation() {
        ccbWebSocketConnection.closeWebSocket()
        CCBManager.getCCBSessionHandlerInterface().endConversation { b, ccbError ->
        }
    }

    private fun disableProgressBar() {
        this.activity?.runOnUiThread {
            if (rootView.conversation_progressbar.visibility == View.VISIBLE)
                rootView.conversation_progressbar.visibility = View.GONE
        }
    }

    private fun showToastOnError() {
        activity?.runOnUiThread {
            Toast.makeText(context, "Failed to connect to the bot.", Toast.LENGTH_SHORT).show()
            disableProgressBar()
        }
    }
}


