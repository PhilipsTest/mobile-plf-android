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

import com.android.volley.Response
import com.philips.platform.appinfra.rest.request.JsonObjectRequest
import org.json.JSONObject

abstract class ECSJsonRequest : ECSRequestInterface , Response.Listener<JSONObject>{

    override fun getRequestType(): RequestType {
       return RequestType.JSON
    }

    override fun getJSONSuccessResponseListener(): Response.Listener<JSONObject> {
        return this
    }

    override fun getAppInfraJSONObject(): JsonObjectRequest? {

        return JsonObjectRequest(getMethod(), getURL(), getJSONRequest()
                , getJSONSuccessResponseListener(), getJSONFailureResponseListener(),
                getHeader(), getParams(), getTokenProviderInterface())
    }
}