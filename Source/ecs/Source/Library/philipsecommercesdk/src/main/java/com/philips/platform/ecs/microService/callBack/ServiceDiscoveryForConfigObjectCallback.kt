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
import com.philips.platform.ecs.microService.util.ECSDataHolder

class ServiceDiscoveryForConfigObjectCallback(val ecsConfigManager: ECSConfigManager, val ecsCallback: ECSCallback<ECSConfig, ECSError>) : ServiceDiscoveryInterface.OnGetServiceUrlMapListener{

    override fun onSuccess(urlMap: MutableMap<String, ServiceDiscoveryService>?) {

        ECSDataHolder.urlMap = urlMap

        val serviceDiscoveryService =urlMap?.get(ECSConstants.SERVICEID_IAP_BASEURL)
        val locale = serviceDiscoveryService?.locale
        ECSDataHolder.locale = locale
        val configUrls = serviceDiscoveryService?.configUrls
        ECSDataHolder.baseURL = configUrls

        if(configUrls==null || ECSDataHolder.getPropositionId() == null){
            var ecsConfig = ECSConfig(locale,null,null,null,null,null,null,null,false)
            ecsCallback.onResponse(ecsConfig)
        }else{
            ecsConfigManager.getConfigObject(ecsCallback)
        }
    }

    override fun onError(error: ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES?, message: String?) {
        ecsCallback.onFailure(ECSError(message?:"",null,null))
    }
}