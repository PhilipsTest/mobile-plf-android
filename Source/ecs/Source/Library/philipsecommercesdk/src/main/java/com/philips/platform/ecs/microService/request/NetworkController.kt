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

import com.philips.platform.appinfra.rest.request.JsonObjectRequest
import com.philips.platform.appinfra.rest.request.StringRequest
import com.philips.platform.ecs.microService.util.ECSDataHolder

class NetworkController(appInfraJSONRequest: AppInfraRequest) {

    var jsonObjectRequest: JsonObjectRequest? = null
    var stringRequest: StringRequest? = null

    init {
        val jsonSuccessResponseListener = appInfraJSONRequest.getJSONSuccessResponseListener()
        val stringSuccessResponseListener = appInfraJSONRequest.getStringSuccessResponseListener()
        jsonObjectRequest = jsonSuccessResponseListener?.let { getAppInfraJSONObject(appInfraJSONRequest) }
        stringRequest = stringSuccessResponseListener?.let {getStringRequest(appInfraJSONRequest)  }

    }

    private fun getAppInfraJSONObject(appInfraJSONRequest: APPInfraRequestInterface): JsonObjectRequest {
        return JsonObjectRequest(appInfraJSONRequest.getMethod(), appInfraJSONRequest.getURL(), appInfraJSONRequest.getJSONRequest()
                , appInfraJSONRequest.getJSONSuccessResponseListener(), appInfraJSONRequest.getJSONFailureResponseListener(),
                appInfraJSONRequest.getHeader(), appInfraJSONRequest.getParams(), appInfraJSONRequest.getTokenProviderInterface())
    }

    fun executeRequest() {
            jsonObjectRequest?.let { ECSDataHolder.appInfra.restClient.requestQueue.add(jsonObjectRequest) } ?: kotlin.run {

            stringRequest?.let { ECSDataHolder.appInfra.restClient.requestQueue.add(stringRequest) }
            }
    }

    private fun getStringRequest(appInfraJSONRequest: APPInfraRequestInterface): StringRequest {
        return StringRequest(appInfraJSONRequest.getMethod(), appInfraJSONRequest.getURL()
                , appInfraJSONRequest.getStringSuccessResponseListener(), appInfraJSONRequest.getJSONFailureResponseListener(),
                appInfraJSONRequest.getHeader(), appInfraJSONRequest.getParams(), appInfraJSONRequest.getTokenProviderInterface())
    }


}