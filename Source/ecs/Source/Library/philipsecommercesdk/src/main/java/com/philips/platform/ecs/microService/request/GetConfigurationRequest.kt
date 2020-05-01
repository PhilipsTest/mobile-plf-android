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
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.model.config.ECSConfig
import org.json.JSONObject

class GetConfigurationRequest(private val eCSCallback: ECSCallback<ECSConfig, Exception>) : AppInfraRequest(), Response.Listener<JSONObject?> {
    override fun getMethod(): Int {
        return Request.Method.GET
    }

    override fun getURL(): String {

        //TODO check if url is null then throw exception ..dont crash with NullPointer
        return ECSDataHolder.getURLMapper(ECSConstants.CONFIG)!!
    }

    override fun onErrorResponse(error: VolleyError?) {}
    override fun onResponse(response: JSONObject?) {}

    override fun getJSONSuccessResponseListener(): Response.Listener<JSONObject?>? {
        return this
    }

}