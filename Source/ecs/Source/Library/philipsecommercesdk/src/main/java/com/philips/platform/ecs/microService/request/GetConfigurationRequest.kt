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
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.store.ECSURLBuilder
import com.philips.platform.ecs.util.ECSConfiguration
import org.json.JSONObject

class GetConfigurationRequest(private val eCSCallback: ECSCallback<ECSConfig, Exception>) : ECSJsonRequest() {


    override fun getURL(): String {
        return getRawConfigUrl()
    }

    override fun getServiceID(): String {
        TODO("Not yet implemented")
    }

    override fun getReplaceURLMap(): Map<String, String> {
        TODO("Not yet implemented")
    }

    override fun onErrorResponse(error: VolleyError?) {
        eCSCallback.onFailure(Exception(error?.message))
    }
    override fun onResponse(response: JSONObject?) {
        val config = response?.getData(ECSConfig::class.java)

        if(config?.rootCategory!= null && config.siteId!=null ) { //TODO to use kotlin if let
            config.isHybris = true
        }

        config?.locale = ECSDataHolder.locale
        config?.let { eCSCallback.onResponse(it) } ?: kotlin.run {   } // TODO send error
    }

    //TODO remove this method
    private fun getRawConfigUrl(): String {
        return ECSConfiguration.INSTANCE.baseURL + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR +
                ECSURLBuilder.SUFFIX_CONFIGURATION + ECSURLBuilder.SEPERATOR +
                ECSDataHolder.locale + ECSURLBuilder.SEPERATOR +
                ECSDataHolder.getPropositionId()
    }

}