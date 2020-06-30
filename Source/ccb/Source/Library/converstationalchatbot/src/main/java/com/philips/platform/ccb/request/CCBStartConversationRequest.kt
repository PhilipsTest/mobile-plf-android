package com.philips.platform.ccb.request

import java.util.HashMap

class CCBStartConversationRequest : CCBChatBotRequestInterface {
    override fun getUrl(): String {
        return CCBUrlBuilder.BASE_URL + CCBUrlBuilder.START_CONVERSATION
    }

    override fun getHeader(): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = "Bearer "+ "access_Token"
        return headers
    }

    override fun getBody(): String? {
        return null
    }

    override fun getMethodType(): String {
        return "POST"
    }
}