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
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.model.config.ECSConfig
import com.philips.platform.ecs.model.products.ECSProduct

class ECSServices(val appInfra: AppInfra) : ECSServiceProvider {

    init {
        ECSDataHolder.appInfra = appInfra
    }


    override fun configureECS(ecsCallback: ECSCallback<Boolean?, Exception?>?) {

    }

    override fun configureECSToGetConfiguration(ecsCallback: ECSCallback<ECSConfig?, Exception?>?) {

    }

    override fun fetchProductDetails(product: ECSProduct?, ecsCallback: ECSCallback<ECSProduct?, Exception?>?) {

    }
}