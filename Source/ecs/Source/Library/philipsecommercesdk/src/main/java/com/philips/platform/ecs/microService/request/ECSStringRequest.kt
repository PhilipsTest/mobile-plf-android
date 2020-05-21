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
import com.philips.platform.appinfra.rest.request.StringRequest
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError

abstract class ECSStringRequest(ecsErrorCallback: ECSCallback<*, ECSError>) : ECSAbstractRequest(ecsErrorCallback) , Response.Listener<String>{

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