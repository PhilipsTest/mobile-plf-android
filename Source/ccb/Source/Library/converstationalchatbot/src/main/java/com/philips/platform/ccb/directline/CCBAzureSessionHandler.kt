package com.philips.platform.ccb.directline

import com.android.volley.Response
import com.android.volley.VolleyError
import com.philips.platform.ccb.integration.ccbCallback
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.manager.CCBSettingManager
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.model.CCBUser
import com.philips.platform.ccb.request.CCBAuthenticationRequest
import com.philips.platform.ccb.rest.CCBRestClient
import org.json.JSONObject
import java.lang.Exception

internal class CCBAzureSessionHandler: CCBSessionHandlerInterface {
    private var ccbRestClient: CCBRestClient? = null

    override fun authenticateUser(ccbUser: CCBUser, ccbCallback: ccbCallback<Boolean, Exception>?) {
        val ccbAuthenticationRequest = CCBAuthenticationRequest(ccbUser.secretKey)
        ccbRestClient = CCBRestClient(CCBSettingManager.mRestInterface)
        ccbRestClient?.invokeRequest(ccbAuthenticationRequest, Response.Listener { response: String ->
            val tokenObject = JSONObject(response)
            val accessToken = tokenObject.getString("token")
            CCBManager.INSTANCE.ccbConversation?.accessToken = accessToken
            ccbCallback?.onResponse(true)
        }, Response.ErrorListener { error: VolleyError ->
            ccbCallback?.onFailure(error)
        })
    }

    override fun startConversation(ccbCallback: ccbCallback<Boolean, Exception>?) {
        TODO("Not yet implemented")
    }

    override fun refreshSession(ccbCallback: ccbCallback<Boolean, Exception>?) {
        TODO("Not yet implemented")
    }

    override fun endConversation(ccbCallback: ccbCallback<Boolean, Exception>?) {
        TODO("Not yet implemented")
    }
}