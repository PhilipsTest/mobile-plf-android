package com.philips.platform.ccb.request

import com.android.volley.Request
import com.philips.platform.ccb.constant.CCBUrlBuilder
import com.philips.platform.ccb.rest.CCBRequestInterface
import org.json.JSONObject
import java.util.*

class CCBAuthenticationRequest(key: String, val name: String) : CCBRequestInterface{
    private var mKey :String = ""

    init {
        mKey = key
    }

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
        val jsonObject = JSONObject()
        val userJson =  JSONObject()
        userJson.put("id","AUserOne")
        userJson.put("name",name)
        jsonObject.put("user",userJson)
        return jsonObject.toString()
    }

    override fun getMethodType(): Int {
        return Request.Method.POST
    }

}