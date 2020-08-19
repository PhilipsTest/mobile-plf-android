/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.request

import com.android.volley.Request
import com.philips.platform.ccb.constant.CCBUrlBuilder
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.rest.CCBRequestInterface
import org.json.JSONObject
import java.util.*

class CCBEndConversationRequest() : CCBRequestInterface {


    override fun getUrl(): String {
        return CCBUrlBuilder.BASE_URL + CCBUrlBuilder.SUFIX_CONVERSATION + CCBManager.conversationId + CCBUrlBuilder.SUFIX_ACTIVITIES
    }

    override fun getHeader(): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = "Bearer "+ CCBManager.token
        headers["Accept-Encoding"] = "gzip"
        return headers
    }

    override fun getBody(): String? {
        val jsonObject = JSONObject()
        jsonObject.put("type","endOfConversation")
        val  fromJson = JSONObject()
        fromJson.put("id","AUserOne")
        jsonObject.put("from",fromJson)
        return jsonObject.toString()
    }

    override fun getMethodType(): Int {
        return Request.Method.POST
    }

}