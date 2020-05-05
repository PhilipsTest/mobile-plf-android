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
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.request.GetConfigurationRequest
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.model.products.ECSProduct

class ECSManager {



    // config ==================
    fun configureECS(ecsCallback: ECSCallback<Boolean, Exception>) {
        ECSDataHolder.appInfra?.serviceDiscovery?.getServicesWithCountryPreference(ECSConstants().getListOfServiceID(), ServiceDiscoveryForConfigBoolCallback(this,ecsCallback), null)
    }

    fun configureECSToGetConfiguration(ecsCallback: ECSCallback<ECSConfig, Exception>) {
        ECSDataHolder.appInfra?.serviceDiscovery?.getServicesWithCountryPreference(ECSConstants().getListOfServiceID(), ServiceDiscoveryForConfigObjectCallback(this,ecsCallback), null)
    }

    fun getConfigObject(ecsCallback: ECSCallback<ECSConfig, Exception>){
        GetConfigurationRequest(ecsCallback).executeRequest()
    }

    fun getConfigBoolean(ecsCallback: ECSCallback<Boolean, Exception>){

        GetConfigurationRequest(object : ECSCallback<ECSConfig, Exception>{
            override fun onResponse(result: ECSConfig) {
                ecsCallback.onResponse(result.isHybris)
            }

            override fun onFailure(ecsError: Exception?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


        }).executeRequest()
    }

    // ============ config ends


    fun fetchProductDetails(product: ECSProduct, ecsCallback: ECSCallback<ECSProduct, Exception>) {
        val ecsException = ECSApiValidator().getECSException(APIType.Locale)
       // if (ecsException!=null) throw ecsException else
    }


}