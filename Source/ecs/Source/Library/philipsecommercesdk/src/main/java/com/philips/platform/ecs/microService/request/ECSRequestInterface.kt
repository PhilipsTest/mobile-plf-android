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
import com.philips.platform.appinfra.rest.TokenProviderInterface
import com.philips.platform.appinfra.rest.request.JsonObjectRequest
import com.philips.platform.appinfra.rest.request.StringRequest
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject

interface ECSRequestInterface : Response.ErrorListener,TokenProviderInterface {

    //TODO retry and time out

    fun  getMethod(): Int{
        return Request.Method.GET
    }
    fun  getURL(): String{
        var url = ECSDataHolder.urlMap?.get(getServiceID())?.configUrls ?: ""
        return url.replaceParam(getReplaceURLMap())
    }
    fun  getServiceID():String


    fun  getReplaceURLMap():MutableMap<String,String>{

        val map = HashMap<String,String>()
        ECSDataHolder.config.siteId?.let { map.put("siteId", it) }
        ECSDataHolder.locale?.split("_")?.get(0)?.let { map.put("language", it) }
        ECSDataHolder.locale?.split("_")?.get(1)?.let { map.put("country", it) }
        return map
    }
    fun  getRequestType():RequestType

    fun  getJSONRequest(): JSONObject?{
     return null
    }

    fun  getJSONSuccessResponseListener(): Response.Listener<JSONObject>?{
        return null
    }
    fun  getStringSuccessResponseListener(): Response.Listener<String>?{
        return null
    }
    fun  getJSONFailureResponseListener(): Response.ErrorListener{
        return this
    }

    fun  getHeader(): MutableMap<String, String>?{
        val headerMap = HashMap<String,String>()
        headerMap["Accept"] = "application/json"
        headerMap["Api-Key"] = "yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP"
        headerMap["Api-Version"] = "1"
        return headerMap
    }
    fun  getParams(): Map<String, String>?{
        return null
    }
    fun  getTokenProviderInterface(): TokenProviderInterface?{
        return null
    }

    override fun getToken(): TokenProviderInterface.Token? {
        return null
    }

    fun executeRequest(){
         NetworkController().executeRequest(this)
    }

    fun getAppInfraJSONObject(): JsonObjectRequest?{
        return null
    }

    fun getAppInfraStringRequest(): StringRequest?{
        return null
    }

}