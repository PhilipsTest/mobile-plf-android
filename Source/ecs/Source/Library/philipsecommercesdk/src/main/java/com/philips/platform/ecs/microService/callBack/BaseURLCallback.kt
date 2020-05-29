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

package com.philips.platform.ecs.microService.callBack

import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.manager.ECSConfigManager
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.request.GetConfigurationRequest
import com.philips.platform.ecs.microService.util.ECSDataHolder

class BaseURLCallback(val getConfigurationRequest: GetConfigurationRequest) : ServiceDiscoveryInterface.OnGetServiceUrlMapListener{

    override fun onSuccess(urlMap: MutableMap<String, ServiceDiscoveryService>?) {

        ECSDataHolder.urlMap = urlMap

        val serviceDiscoveryService =urlMap?.get(ECSConstants.SERVICEID_IAP_BASEURL)
        val locale = serviceDiscoveryService?.locale ?:""
        ECSDataHolder.locale = locale
        val configUrls = serviceDiscoveryService?.configUrls
        ECSDataHolder.baseURL = configUrls

        if(configUrls==null || ECSDataHolder.getPropositionId() == null){
            val ecsConfig = ECSConfig(locale)
            getConfigurationRequest.eCSCallback.onResponse(ecsConfig)
        }else{
            getConfigurationRequest.url = configUrls
            getConfigurationRequest.locale = locale
            getConfigurationRequest.executeRequest()
        }
    }

    override fun onError(error: ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES?, message: String?) {
        getConfigurationRequest.eCSCallback.onFailure(ECSError(message?:"",null,null))
    }
}