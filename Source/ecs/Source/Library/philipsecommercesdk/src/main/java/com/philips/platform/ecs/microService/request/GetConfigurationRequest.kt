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

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import org.json.JSONObject
import java.util.HashMap

class GetConfigurationRequest(private val eCSCallback: ECSCallback<ECSConfig, ECSError>) : ECSJsonRequest(eCSCallback) {


    override fun getURL(): String {
        return getRawConfigUrl()
    }

    override fun getServiceID(): String {
        TODO("Not yet implemented")
    }


    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceUrl: MutableMap<String, String> = HashMap()
        return replaceUrl
    }

    override fun getHeader(): MutableMap<String, String>? {
        return null
    }

    override fun onResponse(response: JSONObject?) {
        val config = response?.getData(ECSConfig::class.java)
        if(config?.rootCategory!= null && config.siteId!=null ) config.isHybris = true

        config?.locale = ECSDataHolder.locale
        config?.let { ECSDataHolder.config = config }
        config?.let { eCSCallback.onResponse(it) }
    }

    //TODO remove this method
    private fun getRawConfigUrl(): String {
        return ECSDataHolder.baseURL +"/"+ "pilcommercewebservices"+"/" + "v2" + "/" +
                "inAppConfig" + "/" +
                ECSDataHolder.locale + "/" +
                ECSDataHolder.getPropositionId()
    }

}