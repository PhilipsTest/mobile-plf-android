package com.philips.platform.ccb.request

import com.android.volley.Request
import com.philips.platform.ccb.constant.CCBUrlBuilder
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.rest.CCBRequestInterface
import java.util.HashMap

class CCBStartConversationRequest : CCBRequestInterface {
    override fun getUrl(): String {
        return CCBUrlBuilder.BASE_URL + CCBUrlBuilder.START_CONVERSATION
    }

    override fun getHeader(): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = "Bearer "+ CCBManager.token
        return headers
    }

    override fun getBody(): String? {
        return null
    }

    override fun getMethodType(): Int {
        return Request.Method.POST
    }

}