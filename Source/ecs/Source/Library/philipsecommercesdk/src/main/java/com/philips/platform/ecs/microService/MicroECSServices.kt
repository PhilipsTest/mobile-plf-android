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
import com.philips.platform.ecs.microService.error.ECSException
import com.philips.platform.ecs.microService.manager.ECSManager
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.util.ECSDataHolder


class MicroECSServices(appInfra: AppInfra) {

    private var ecsManager : ECSManager
    init {
        ECSDataHolder.appInfra = appInfra
        ecsManager = ECSManager()
    }

    fun configureECS(ecsCallback: ECSCallback<Boolean, Exception>) {
        ecsManager.configureECS(ecsCallback)
    }

    fun configureECSToGetConfiguration(ecsCallback: ECSCallback<ECSConfig, Exception>) {
        ecsManager.configureECSToGetConfiguration(ecsCallback)
    }
    @Throws(ECSException::class)
    fun fetchProduct(ctn: String, eCSCallback:ECSCallback<ECSProduct, Exception>) {
        ecsManager.getProductFor(ctn, eCSCallback)
    }

}