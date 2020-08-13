package com.philips.platform.ccb.directline

import android.view.View
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.errors.CCBErrorCdes
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.model.CCBUser
import com.philips.platform.ccb.request.CCBAuthenticationRequest
import com.philips.platform.ccb.request.CCBEndConversationRequest
import com.philips.platform.ccb.request.CCBRefreshTokenRequest
import com.philips.platform.ccb.request.CCBStartConversationRequest
import com.philips.platform.ccb.rest.CCBRestClient
import org.json.JSONObject

class CCBAzureSessionHandler : CCBSessionHandlerInterface {

    var ccbWebSocketConnection: CCBWebSocketConnection? = null

    private val ccbRestClient by lazy { CCBRestClient() }

    override fun authenticateUser(ccbUser: CCBUser, completionHandler: (Boolean, CCBError?) -> Unit) {
        val ccbAuthenticationRequest = CCBAuthenticationRequest(ccbUser.secretKey)

        ccbRestClient.invokeRequest(ccbAuthenticationRequest, Response.Listener { response: String ->
            val tokenObject = JSONObject(response)
            val accessToken = tokenObject.getString("token")
            val conversationId = tokenObject.getString("conversationId")
            CCBManager.token = accessToken
            CCBManager.conversationId = conversationId
            completionHandler.invoke(true, null)
        }, Response.ErrorListener { error: VolleyError ->
            completionHandler.invoke(false, CCBError(CCBErrorCdes.NETWORK_ERROR, "Chatbot Error"))
        })
    }

    override fun startConversation(ccbUser: CCBUser, completionHandler: (Boolean, CCBError?) -> Unit) {

        authenticateUser(ccbUser){ success: Boolean, ccbError: CCBError? ->
            if(success){
                val ccbStartConversationRequest = CCBStartConversationRequest()
                ccbRestClient.invokeRequest(ccbStartConversationRequest, Response.Listener { response: String ->
                    val conversation = Gson().fromJson(response, CCBConversation::class.java)
                    if(conversation!=null) {
                        CCBManager.streamUrl = conversation.streamUrl
                    }
                    /*ccbWebSocketConnection = CCBWebSocketConnection()
                    ccbWebSocketConnection?.createWebSocket()*/
                    completionHandler.invoke(true, null)
                }, Response.ErrorListener { error: VolleyError ->
                    completionHandler.invoke(false, CCBError(CCBErrorCdes.NETWORK_ERROR, "Chatbot Error"))
                })
            }
        }
    }

    override fun refreshSession(completionHandler: (Boolean, CCBError?) -> Unit) {
        val ccbRefreshTokenRequest = CCBRefreshTokenRequest()
        ccbRestClient.invokeRequest(ccbRefreshTokenRequest,Response.Listener { response: String ->
            completionHandler.invoke(true, null)
        }, Response.ErrorListener { error: VolleyError ->
            completionHandler.invoke(false, CCBError(CCBErrorCdes.NETWORK_ERROR, "Chatbot Error"))
        })
    }

    override fun endConversation(completionHandler: (Boolean, CCBError?) -> Unit) {
        val ccbEndConversationRequest = CCBEndConversationRequest()
        ccbRestClient.invokeRequest(ccbEndConversationRequest,Response.Listener { response: String ->
            ccbWebSocketConnection?.closeWebSocket()
            CCBManager.conversationId = null
            CCBManager.token = null
            completionHandler.invoke(true, null)
        }, Response.ErrorListener { error: VolleyError ->
            completionHandler.invoke(false, CCBError(CCBErrorCdes.NETWORK_ERROR, "Chatbot Error"))
        })
    }

    override fun updateConversation(completionHandler: (Boolean, CCBError?) -> Unit) {
        val ccbEndConversationRequest = CCBEndConversationRequest()
        ccbRestClient.invokeRequest(ccbEndConversationRequest,Response.Listener { response: String ->
            completionHandler.invoke(true, null)
        }, Response.ErrorListener { error: VolleyError ->
            completionHandler.invoke(false, CCBError(CCBErrorCdes.NETWORK_ERROR, "Chatbot Error"))
        })
    }
}