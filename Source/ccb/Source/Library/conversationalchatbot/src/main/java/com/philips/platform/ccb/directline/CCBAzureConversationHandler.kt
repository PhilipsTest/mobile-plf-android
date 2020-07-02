package com.philips.platform.ccb.directline

import com.android.volley.Response
import com.android.volley.VolleyError
import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.manager.CCBSettingManager
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.request.CCBPostMessageRequest
import com.philips.platform.ccb.rest.CCBRestClient

class CCBAzureConversationHandler: CCBConversationHandlerInterface {

    private val ccbRestClient by lazy { CCBRestClient(CCBSettingManager.mRestInterface) }

    override fun postMessage(completionHandler: (Boolean, CCBError?) -> Unit) {
        val ccbPostMessageRequest = CCBPostMessageRequest()
        ccbRestClient.invokeRequest(ccbPostMessageRequest,Response.Listener { response: String ->
            completionHandler.invoke(true, null)
        }, Response.ErrorListener { error: VolleyError ->
            completionHandler.invoke(false, CCBError(error.networkResponse.statusCode, "Chatbot Error"))
        })

    }

    override fun getAllMessages(ccbConversation: CCBConversation, completionHandler: (Boolean, CCBError?) -> Unit) {
        TODO("Not yet implemented")
    }
}