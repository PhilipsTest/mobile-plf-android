package com.philips.platform.ccb.directline

import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.model.*
import com.philips.platform.ccb.request.CCBGetAllMessagesRequest
import com.philips.platform.ccb.request.CCBPostMessageRequest
import com.philips.platform.ccb.rest.CCBRestClient
import com.philips.platform.ccb.util.CCBLog
import org.json.JSONArray
import org.json.JSONObject


class CCBAzureConversationHandler: CCBConversationHandlerInterface {

    private val ccbRestClient by lazy { CCBRestClient() }

    override fun postMessage(text: String?,completionHandler: (Boolean, CCBError?) -> Unit) {
        val ccbPostMessageRequest = CCBPostMessageRequest(text)
        ccbRestClient.invokeRequest(ccbPostMessageRequest, Response.Listener { response: String ->

            completionHandler.invoke(true, null)
        }, Response.ErrorListener { error: VolleyError ->
            completionHandler.invoke(false, CCBError(error.networkResponse.statusCode, "Chatbot Error"))
        })

    }

    override fun getAllMessages(completionHandler: (CCBMessage?, CCBError?) -> Unit) {
        val ccbGetAllMessagesRequest = CCBGetAllMessagesRequest()
        ccbRestClient.invokeRequest(ccbGetAllMessagesRequest, Response.Listener { response: String ->
            val ccbMessage = Gson().fromJson(response, CCBMessage::class.java)
            completionHandler.invoke(ccbMessage, null)
        }, Response.ErrorListener { error: VolleyError ->
            completionHandler.invoke(null, CCBError(error.networkResponse.statusCode, "Chatbot Error"))
        })
    }
}