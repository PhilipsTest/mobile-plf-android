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

package com.philips.platform.ecs.microService.manager

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.callBack.ServiceDiscoveryForConfigBoolCallback
import com.philips.platform.ecs.microService.callBack.ServiceDiscoveryForConfigObjectCallback
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.request.GetConfigurationRequest
import com.philips.platform.ecs.microService.request.GetProductForRequest
import com.philips.platform.ecs.microService.util.ECSDataHolder


class ECSConfigManager {

    fun configureECS(ecsCallback: ECSCallback<Boolean, ECSError>) {
        ECSDataHolder.appInfra?.serviceDiscovery?.getServicesWithCountryPreference(ECSConstants().getListOfServiceID(), ServiceDiscoveryForConfigBoolCallback(this,ecsCallback), null)
    }

    fun configureECSToGetConfiguration(ecsCallback: ECSCallback<ECSConfig, ECSError>) {
        ECSDataHolder.appInfra?.serviceDiscovery?.getServicesWithCountryPreference(ECSConstants().getListOfServiceID(), ServiceDiscoveryForConfigObjectCallback(this,ecsCallback), null)
    }

    fun getConfigObject(ecsCallback: ECSCallback<ECSConfig, ECSError>){
        GetConfigurationRequest(ecsCallback).executeRequest()
    }

    fun getConfigBoolean(ecsCallback: ECSCallback<Boolean, ECSError>){

        GetConfigurationRequest(object : ECSCallback<ECSConfig, ECSError>{
            override fun onResponse(result: ECSConfig) {
                ecsCallback.onResponse(result.isHybris)
            }

            override fun onFailure(ecsError: ECSError) {
                ecsCallback.onFailure(ecsError)
            }


        }).executeRequest()
    }

}