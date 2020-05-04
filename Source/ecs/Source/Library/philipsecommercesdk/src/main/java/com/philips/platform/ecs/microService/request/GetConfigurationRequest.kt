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
import com.android.volley.VolleyError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.util.ECSDataHolder
import org.json.JSONObject

class GetConfigurationRequest(private val eCSCallback: ECSCallback<ECSConfig, Exception>) : ECSJsonRequest() {

    override fun getMethod(): Int {
        return Request.Method.GET
    }

    override fun getURL(): String {
        //TODO check if url is null then throw exception ..dont crash with NullPointer
        return ECSDataHolder.getURLMapper(ECSConstants.CONFIG)!!
    }

    override fun onErrorResponse(error: VolleyError?) {}
    override fun onResponse(response: JSONObject?) {}

}