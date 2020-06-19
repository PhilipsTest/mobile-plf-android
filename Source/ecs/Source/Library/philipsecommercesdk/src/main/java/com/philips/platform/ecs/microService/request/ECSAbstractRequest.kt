/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.ecs.microService.request

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.philips.platform.appinfra.rest.TokenProviderInterface
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ErrorHandler
import com.philips.platform.ecs.microService.util.ECSDataHolder
import org.json.JSONObject

abstract class ECSAbstractRequest(val ecsErrorCallback: ECSCallback<*, ECSError>) :  Response.ErrorListener {

    lateinit var url: String
    lateinit var locale: String
    var requestMethod = Request.Method.GET

    var tokenProviderInterface : TokenProviderInterface? = null
    var jsonObjectForRequest : JSONObject? = null


    abstract fun getServiceID():String
    abstract fun executeRequest()

    open fun getURL():String{
        return url
    }

    override fun onErrorResponse(error: VolleyError?) {
        ecsErrorCallback.onFailure(ErrorHandler().getECSError(error))
    }

    open fun getReplaceURLMap(): MutableMap<String, String> {
        val map = HashMap<String, String>()
        ECSDataHolder.config.siteId?.let { map.put("siteId", it) }
        ECSDataHolder.locale?.let {
            val langCountryArray = it.split("_")
            if (langCountryArray.size >= 2) {
                map["language"] = langCountryArray[0]
                map["country"]  =  langCountryArray[1]
            }
        }
        return map
    }

    open fun  getHeader(): MutableMap<String, String>?{
        val headerMap = HashMap<String,String>()
        headerMap["Accept"] = "application/json"
        ECSDataHolder.getAPIKey() ?.let { headerMap["Api-Key"] = it }
        headerMap["Api-Version"] = "1"
        return headerMap
    }

    open fun  getParams(): Map<String, String>?{
        return null
    }

}