package com.philips.platform.ccb.directline

import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.integration.ccbCallback
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.manager.CCBSettingManager
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.model.CCBUser
import com.philips.platform.ccb.request.CCBAuthenticationRequest
import com.philips.platform.ccb.request.CCBStartConversationRequest
import com.philips.platform.ccb.rest.CCBRestClient
import org.json.JSONObject

class CCBAzureSessionHandler : CCBSessionHandlerInterface {

    private val ccbRestClient by lazy { CCBRestClient(CCBSettingManager.mRestInterface) }

    override fun authenticateUser(ccbUser: CCBUser, completionHandler: (Boolean, CCBError?) -> Unit) {
        val ccbAuthenticationRequest = CCBAuthenticationRequest(ccbUser.secretKey)

        ccbRestClient.invokeRequest(ccbAuthenticationRequest, Response.Listener { response: String ->
            val tokenObject = JSONObject(response)
            val accessToken = tokenObject.getString("token")
            CCBManager.token = accessToken
            completionHandler.invoke(true, null)
        }, Response.ErrorListener { error: VolleyError ->
            completionHandler.invoke(false, CCBError(error.networkResponse.statusCode, "Chatbot Error"))
        })
    }

    override fun startConversation(completionHandler: (CCBConversation?, CCBError?) -> Unit) {
        val ccbStartConversationRequest = CCBStartConversationRequest()
        ccbRestClient.invokeRequest(ccbStartConversationRequest, Response.Listener { response: String ->
            val conversation = Gson().fromJson(response, CCBConversation::class.java)
            completionHandler.invoke(conversation, null)
        }, Response.ErrorListener { error: VolleyError ->
            completionHandler.invoke(null, CCBError(error.networkResponse.statusCode, "Chatbot Error"))
        })
    }

    override fun refreshSession(completionHandler: (Boolean, CCBError?) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun endConversation(completionHandler: (Boolean, CCBError?) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}