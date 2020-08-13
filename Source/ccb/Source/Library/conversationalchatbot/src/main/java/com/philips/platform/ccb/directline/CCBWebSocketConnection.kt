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
    private lateinit var webSocket: WebSocket

    fun setBotResponseListener(botResponseListener: BotResponseListener) {
        this.botResponseListener = botResponseListener
    }


    fun createWebSocket() {
        val request = Request.Builder().url(CCBManager.streamUrl).build()
        webSocket = OkHttpClient().newWebSocket(request, this)
    }

    fun closeWebSocket() {
        webSocket.close(1000, "User is closing Websocket")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        botResponseListener?.onOpen()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        botResponseListener?.onFailure()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        try {
            if (!TextUtils.isEmpty(text)) botResponseListener?.onMessageReceived(text)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        botResponseListener?.onClosed()
    }
}