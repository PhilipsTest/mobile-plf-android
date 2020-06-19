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
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.util.ECSDataHolder
import org.json.JSONObject

abstract class ECSJsonRequest(ecsErrorCallback: ECSCallback<*, ECSError>) : ECSAbstractRequest(ecsErrorCallback) , Response.Listener<JSONObject>{


    override fun executeRequest() {
        ECSDataHolder.appInfra?.restClient?.requestQueue?.add(getJsonObjectRequest())
    }

    private fun getJsonObjectRequest():JsonObjectRequest{
        return JsonObjectRequest(requestMethod , getURL(), jsonObjectForRequest
                , this, this,
                getHeader(), getParams(),tokenProviderInterface)
    }

}