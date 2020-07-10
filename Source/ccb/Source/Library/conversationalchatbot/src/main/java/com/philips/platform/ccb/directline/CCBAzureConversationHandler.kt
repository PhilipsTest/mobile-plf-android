package com.philips.platform.ccb.directline

import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.model.Conversation
import com.philips.platform.ccb.request.CCBPostMessageRequest
import com.philips.platform.ccb.rest.CCBRestClient

class CCBAzureConversationHandler: CCBConversationHandlerInterface {

    private val ccbRestClient by lazy { CCBRestClient() }

    override fun postMessage(message: String?,completionHandler: (conversation:Conversation?, CCBError?) -> Unit) {
        val ccbPostMessageRequest = CCBPostMessageRequest(message)
        ccbRestClient.invokeRequest(ccbPostMessageRequest,Response.Listener {
            Log.i("conversation"," => $it")
            val conversation = Gson().fromJson(it, Conversation::class.java)
            completionHandler.invoke(conversation, null)
        }, Response.ErrorListener { error: VolleyError ->
            completionHandler.invoke(null, error?.networkResponse?.statusCode?.let { CCBError(it, "Chatbot Error") })
        })
    }

    override fun getAllMessages(ccbConversation: CCBConversation, completionHandler: (Boolean, CCBError?) -> Unit) {
        TODO("Not yet implemented")
    }
}