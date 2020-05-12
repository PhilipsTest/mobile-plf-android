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
import com.philips.platform.appinfra.rest.request.StringRequest

abstract class ECSStringRequest : ECSRequestInterface , Response.Listener<String>{

    override fun getRequestType(): RequestType {
        return RequestType.STRING
    }

    override fun  getStringSuccessResponseListener(): Response.Listener<String>{
        return this
    }

    override fun getAppInfraStringRequest(): StringRequest? {
        return StringRequest(getMethod(), getURL()
                ,getStringSuccessResponseListener(), getJSONFailureResponseListener(),
                getHeader(), getParams(),getTokenProviderInterface())
    }
}