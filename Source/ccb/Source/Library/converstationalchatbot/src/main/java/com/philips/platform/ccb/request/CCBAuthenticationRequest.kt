package com.philips.platform.ccb.request

import java.util.*

class CCBAuthenticationRequest : CCBChatBotRequestInterface{
    override fun getUrl(): String {
        return CCBUrlBuilder.BASE_URL + CCBUrlBuilder.AUTHENTICATION
    }

    override fun getHeader(): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = "Bearer "+ CCBUrlBuilder.SECRET_KEY
        return headers
    }

    override fun getBody(): String? {
        return null
    }

    override fun getMethodType(): String {
        return "POST"
    }
}