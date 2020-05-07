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
import com.philips.platform.ecs.model.products.ECSProducts

class MicroECSServices(val appInfra: AppInfra) {

    init {
        ECSDataHolder.appInfra = appInfra
    }

    var ecsManager = ECSManager()


    fun configureECS(ecsCallback: ECSCallback<Boolean, Exception>) {
        ecsManager.configureECS(ecsCallback)
    }

    fun configureECSToGetConfiguration(ecsCallback: ECSCallback<ECSConfig, Exception>) {
        ecsManager.configureECSToGetConfiguration(ecsCallback)
    }


    fun fetchProducts(currentPage: Int, pageSize: Int, eCSCallback: com.philips.platform.ecs.integration.ECSCallback<ECSProducts?, java.lang.Exception?>) {
        ecsManager.getProductList(currentPage, pageSize, eCSCallback)
    }

    fun fetchProduct(ctn: String, eCSCallback: com.philips.platform.ecs.integration.ECSCallback<ECSProduct?, java.lang.Exception?>) {
        ecsManager.getProductFor(ctn, eCSCallback)
    }

    fun fetchProductSummaries(ctns: List<String?>, ecsCallback: com.philips.platform.ecs.integration.ECSCallback<List<ECSProduct?>?, java.lang.Exception?>) {
        ecsManager.getProductSummary(ctns, ecsCallback)
    }


    fun fetchProductDetails(product: ECSProduct, ecsCallback: com.philips.platform.ecs.integration.ECSCallback<ECSProduct?, java.lang.Exception?>) {
        ecsManager.getProductDetail(product, ecsCallback)
    }


}