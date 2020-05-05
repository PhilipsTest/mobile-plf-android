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

class NetworkController(private val ecsRequest: ECSRequestInterface) {

    internal fun getAppInfraJSONObject(): JsonObjectRequest {
        return JsonObjectRequest(ecsRequest.getMethod(), ecsRequest.getURL(), ecsRequest.getJSONRequest()
                , ecsRequest.getJSONSuccessResponseListener(), ecsRequest.getJSONFailureResponseListener(),
                ecsRequest.getHeader(), ecsRequest.getParams(), ecsRequest.getTokenProviderInterface())
    }

    fun executeRequest() {

        when (ecsRequest.getRequestType()) {

            RequestType.JSON -> ECSDataHolder.appInfra?.restClient?.requestQueue?.add(getAppInfraJSONObject())
            RequestType.STRING -> ECSDataHolder.appInfra?.restClient?.requestQueue?.add(getStringRequest())
        }
    }

    internal fun getStringRequest(): StringRequest {
        return StringRequest(ecsRequest.getMethod(), ecsRequest.getURL()
                , ecsRequest.getStringSuccessResponseListener(), ecsRequest.getJSONFailureResponseListener(),
                ecsRequest.getHeader(), ecsRequest.getParams(), ecsRequest.getTokenProviderInterface())
    }

}