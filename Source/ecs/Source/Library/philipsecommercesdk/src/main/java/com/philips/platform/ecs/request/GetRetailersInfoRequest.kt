/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.request


import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.gson.Gson

import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.retailers.ECSRetailerList
import com.philips.platform.ecs.util.ECSConfiguration
import com.philips.platform.ecs.error.ECSNetworkError
import org.json.JSONObject
import java.util.HashMap

open class GetRetailersInfoRequest (ecsCallback: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.retailers.ECSRetailerList, Exception>, ctn :String) : com.philips.platform.ecs.request.OAuthAppInfraAbstractRequest() , Response.Listener<JSONObject> {

    val PREFIX_RETAILERS = "www.philips.com/api/wtb/v1"
    val RETAILERS_ALTER = "online-retailers?product=%s&lang=en"
    val PRX_SECTOR_CODE = "B2C"

    val callBack = ecsCallback
    val ctn = ctn
    override fun getURL(): String {
        return createURL()
    }

    fun createURL():String{
            val builder = StringBuilder("https://")
            builder.append(PREFIX_RETAILERS).append("/")
            builder.append(PRX_SECTOR_CODE).append("/")
            builder.append(com.philips.platform.ecs.util.ECSConfiguration.INSTANCE.locale).append("/")
            builder.append(RETAILERS_ALTER)
            return String.format(builder.toString(), ctn)
    }

    override fun onErrorResponse(error: VolleyError?) {
        val ecsErrorWrapper = com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage(error,this)
        callBack.onFailure(ecsErrorWrapper.exception, ecsErrorWrapper.ecsError)
    }

    override fun getMethod(): Int {
        return Request.Method.GET
    }

    override fun getHeader(): Map<String, String>? {
        val authMap = HashMap<String, String>()
        authMap["Authorization"] = "Bearer " + com.philips.platform.ecs.util.ECSConfiguration.INSTANCE.accessToken
        return authMap
    }

    override fun onResponse(response: JSONObject?) {

        try{
           val webResults = Gson().fromJson(response.toString(),
                    com.philips.platform.ecs.model.retailers.ECSRetailerList::class.java)
            callBack.onResponse(webResults)
        }catch (exception:Exception){
            val ecsError = com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage(com.philips.platform.ecs.error.ECSErrorEnum.ECSsomethingWentWrong, exception, response.toString())
            callBack.onFailure(ecsError.getException(), ecsError.ecsError)
        }

    }

    override fun getJSONSuccessResponseListener(): Response.Listener<JSONObject> {
        return this
    }


}