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

package com.philips.platform.ecs.microService

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.manager.ECSManager
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.model.products.ECSProduct

class ECSServices(val appInfra: AppInfra) : ECSServiceProvider {

    init {
        ECSDataHolder.appInfra = appInfra
    }

    var ecsManager = ECSManager()


    override fun configureECS(ecsCallback: ECSCallback<Boolean, Exception>) {
        ecsManager.configureECS(ecsCallback)
    }

    override fun configureECSToGetConfiguration(ecsCallback: ECSCallback<ECSConfig, Exception>) {
        ecsManager.configureECSToGetConfiguration(ecsCallback)
    }

    @Throws(Exception::class)
    override fun fetchProductDetails(product: ECSProduct, ecsCallback: ECSCallback<ECSProduct, Exception>) {
        ecsManager.fetchProductDetails(product,ecsCallback)
    }
}