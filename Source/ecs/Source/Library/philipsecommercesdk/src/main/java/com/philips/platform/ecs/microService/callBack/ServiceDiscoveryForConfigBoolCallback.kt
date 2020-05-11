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
import com.philips.platform.ecs.microService.util.ECSDataHolder

class ServiceDiscoveryForConfigBoolCallback(val ecsConfigManager: ECSConfigManager, val ecsCallback: ECSCallback<Boolean, ECSError>) : ServiceDiscoveryInterface.OnGetServiceUrlMapListener{

    override fun onSuccess(urlMap: MutableMap<String, ServiceDiscoveryService>?) {

        ECSDataHolder.urlMap = urlMap

        val serviceDiscoveryService =urlMap?.get(ECSConstants.SERVICEID_IAP_BASEURL)
        val locale = serviceDiscoveryService?.locale
        ECSDataHolder.locale = locale
        val configUrls = serviceDiscoveryService?.configUrls
        ECSDataHolder.baseURL = configUrls

        if(configUrls==null || ECSDataHolder.getPropositionId() == null){
            ecsCallback.onResponse(false)
        }else{
            ecsConfigManager.getConfigBoolean(ecsCallback)
        }
    }

    override fun onError(error: ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES?, message: String?) {
        var notNullMessage = message?:""
        ecsCallback.onFailure(ECSError(notNullMessage))
    }
}