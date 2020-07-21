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
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.util.ECSConfiguration
import org.json.JSONObject
import java.util.*

class GetConfigurationRequest(val eCSCallback: ECSCallback<ECSConfig, ECSError>) : ECSJsonRequest(eCSCallback) {


    override fun getURL(): String {
        return getRawConfigUrl(url)
    }

    override fun getServiceID(): String {
       return ECSConstants.SERVICEID_IAP_BASEURL
    }


    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceUrl: MutableMap<String, String> = HashMap()
        return replaceUrl
    }

    override fun getHeader(): MutableMap<String, String>? {
        return null
    }

    override fun onResponse(response: JSONObject?) {
        val config = response?.getData(ECSConfig::class.java) ?:ECSConfig(locale)
        config.locale = locale
        if(config.rootCategory!= null && config.siteId!=null ) config.isHybris = true
        ECSDataHolder.config = config
        setOCCConfigData(config)
        eCSCallback.onResponse(config)
    }

    override fun onErrorResponse(error: VolleyError?) {
        val ecsConfig = ECSConfig(locale)
        eCSCallback.onResponse(ecsConfig)
    }

    private fun getRawConfigUrl(url: String): String {
        return  url +"/"+ "pilcommercewebservices"+"/" + "v2" + "/" +
                "inAppConfig" + "/" +
                 locale + "/" +
                 ECSDataHolder.getPropositionId()
    }

    //TODO to be removed
    private fun setOCCConfigData(config : ECSConfig){
        ECSConfiguration.INSTANCE.siteId = config.siteId
        ECSConfiguration.INSTANCE.locale = locale
        ECSConfiguration.INSTANCE.rootCategory = config.rootCategory
    }

}