package com.philips.platform.ccb.directline

import android.os.Build
import android.util.Log
import com.philips.platform.ccb.manager.CCBManager
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.net.URISyntaxException

class CCBWebSocketConnection {

    var mWebSocketClient: WebSocketClient? = null

    fun connectWebSocket() {
        val uri: URI
        uri = try {
            URI(CCBManager.streamUrl)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }
        mWebSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(serverHandshake: ServerHandshake) {
                Log.i("Websocket", "Opened")
                mWebSocketClient!!.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL)
            }

            override fun onMessage(s: String) {
            }

            override fun onClose(i: Int, s: String, b: Boolean) {
                Log.i("Websocket", "Closed $s")
            }

            override fun onError(e: Exception) {
                Log.i("Websocket", "Error " + e.message)
            }
        }
        (mWebSocketClient as WebSocketClient).connect()
    }


}