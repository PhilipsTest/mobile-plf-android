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
    private var botResponseListener:BotResponseListener? = null

    fun setBotResponseListener(botResponseListener: BotResponseListener){
        this.botResponseListener = botResponseListener
    }

    fun createWebSocket(){
        val request = Request.Builder().url(CCBManager.streamUrl).build()
        OkHttpClient().newWebSocket(request,this)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.i(TAG, "onOpen $response")
        botResponseListener?.onOpen()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.i(TAG, "onFailure $response")
        botResponseListener?.onFailure()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.i(TAG, "onClosing $reason")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.i(TAG, "onMessage $text")
        try {
            if (!TextUtils.isEmpty(text)) botResponseListener?.onMessageReceived(text)
        }catch (ex:Exception){
            ex.printStackTrace()
        }

    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Log.i(TAG, "onMessage $bytes")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.i(TAG, "onClosed $reason")
        botResponseListener?.onClosed()
    }
}