/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.rest

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.philips.platform.appinfra.rest.request.StringRequest

class CCBRequest(method: Int, url: String?, val body: String?, listener: Response.Listener<String>?, errorListener: Response.ErrorListener?, header: Map<String, String>) : StringRequest(method, url, listener, errorListener, header, null, null) {

    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray? {
        return body?.toByteArray(Charsets.UTF_8)
    }
}