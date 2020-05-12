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

import com.android.volley.VolleyError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ServerError
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.getJsonError
import org.json.JSONObject

class GetConfigurationRequest(private val eCSCallback: ECSCallback<ECSConfig, ECSError>) : ECSJsonRequest() {


    override fun getURL(): String {
        return getRawConfigUrl()
    }

    override fun getServiceID(): String {
        TODO("Not yet implemented")
    }


    override fun getReplaceURLMap(): MutableMap<String, String> {
        TODO("Not yet implemented")
    }

    override fun onErrorResponse(error: VolleyError?) {
        val jsonError = error?.getJsonError()
        val hybrisError = jsonError?.getData(ServerError::class.java)
        eCSCallback.onFailure(ECSError(hybrisError.toString(),null,null))
    }
    override fun onResponse(response: JSONObject?) {
        val config = response?.getData(ECSConfig::class.java)

        if(config?.rootCategory!= null && config.siteId!=null ) { //TODO to use kotlin if let
            config.isHybris = true
        }

        config?.locale = ECSDataHolder.locale
        config?.let { ECSDataHolder.config = config }
        config?.let { eCSCallback.onResponse(it) } ?: kotlin.run {   } // TODO send error
    }

    //TODO remove this method
    private fun getRawConfigUrl(): String {
        return ECSDataHolder.baseURL +"/"+ "pilcommercewebservices"+"/" + "v2" + "/" +
                "inAppConfig" + "/" +
                ECSDataHolder.locale + "/" +
                ECSDataHolder.getPropositionId()
    }

}