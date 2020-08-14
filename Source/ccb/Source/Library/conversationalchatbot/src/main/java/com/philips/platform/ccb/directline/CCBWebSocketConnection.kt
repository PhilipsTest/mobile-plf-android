/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.directline

import android.text.TextUtils
import android.util.Log
import com.philips.platform.ccb.listeners.BotResponseListener
import com.philips.platform.ccb.manager.CCBManager
import okhttp3.*
import okio.ByteString
import java.lang.Exception

class CCBWebSocketConnection : WebSocketListener() {

    private val TAG: String? = CCBWebSocketConnection::class.java.simpleName
    private var botResponseListener: BotResponseListener? = null
    private var webSocket: WebSocket? = null

    fun setBotResponseListener(botResponseListener: BotResponseListener) {
        this.botResponseListener = botResponseListener
    }

    fun createWebSocket() {
        val request = Request.Builder().url(CCBManager.streamUrl).build()
        webSocket = OkHttpClient().newWebSocket(request, this)
    }

    fun closeWebSocket() {
        webSocket?.close(1000, "User is closing Websocket")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        botResponseListener?.onOpen()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        botResponseListener?.onFailure()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.i(TAG, "CCBConversational onMessage:->$text")
        try {
            if (!TextUtils.isEmpty(text)) botResponseListener?.onMessageReceived(text)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        botResponseListener?.onClosed()
    }
}