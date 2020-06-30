package com.philips.platform.ccb.rest

import com.android.volley.Response
import com.philips.platform.appinfra.rest.request.StringRequest

class CCBRequest(method: Int, url: String?, listener: Response.Listener<String>?, errorListener: Response.ErrorListener?, header: Map<String, String>) : StringRequest(method, url, listener, errorListener, header, null, null) {

}