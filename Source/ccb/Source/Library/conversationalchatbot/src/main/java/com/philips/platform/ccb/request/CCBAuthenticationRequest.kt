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
import com.philips.platform.ccb.rest.CCBRequestInterface
import java.util.*

class CCBAuthenticationRequest(key: String?) : CCBRequestInterface{
    private var mKey :String? = ""

    init {
        mKey = key
    }

    override fun getUrl(): String {
        return CCBUrlBuilder.BASE_URL + CCBUrlBuilder.AUTHENTICATION
    }

    override fun getHeader(): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = "Bearer "+ CCBUrlBuilder.HIDDEN_KNOCK
        return headers
    }

    override fun getBody(): String? {
        return null
    }

    override fun getMethodType(): Int {
        return Request.Method.POST
    }

}