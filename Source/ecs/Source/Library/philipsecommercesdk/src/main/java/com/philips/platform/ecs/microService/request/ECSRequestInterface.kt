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
import org.json.JSONObject

interface ECSRequestInterface : Response.ErrorListener,TokenProviderInterface {

    fun  getMethod(): Int{
        return Request.Method.GET
    }
    fun  getURL(): String
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

    fun  getHeader(): Map<String, String>?{
        return null
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
        Thread(Runnable {  NetworkController(this).executeRequest() }).start()
    }

}