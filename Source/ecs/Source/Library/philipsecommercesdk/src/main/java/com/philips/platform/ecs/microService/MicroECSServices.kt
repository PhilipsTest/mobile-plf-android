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
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSException
import com.philips.platform.ecs.microService.manager.ECSConfigManager
import com.philips.platform.ecs.microService.manager.ECSProductManager
import com.philips.platform.ecs.microService.manager.ECSRetailerManager
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.ecs.microService.util.ECSDataHolder


class MicroECSServices(appInfra: AppInfra) {

    internal var ecsConfigManager = ECSConfigManager()
    internal var ecsProductManager = ECSProductManager()
    internal var ecsRetailerManager = ECSRetailerManager()

    init {
        ECSDataHolder.appInfra = appInfra
    }

    fun configureECS(ecsCallback: ECSCallback<Boolean, ECSError>) {
        ecsConfigManager.configureECS(ecsCallback)
    }

    fun configureECSToGetConfiguration(ecsCallback: ECSCallback<ECSConfig, ECSError>) {
        ecsConfigManager.configureECSToGetConfiguration(ecsCallback)
    }


    fun fetchProducts(currentPage: Int, pageSize: Int) {
      //TODO
    }

    @Throws(ECSException::class)
    fun fetchProduct(ctn: String, eCSCallback:ECSCallback<ECSProduct?, ECSError>) {
        ecsProductManager.getProductFor(ctn, eCSCallback)
    }

    @Throws(ECSException::class)
    fun fetchProductSummaries(ctns: List<String>, ecsCallback: ECSCallback<List<ECSProduct>, ECSError>) {
        ecsProductManager.fetchProductSummaries(ctns,ecsCallback)
    }

    @Throws(ECSException::class)
    fun fetchProductDetails(product:ECSProduct, ecsCallback:ECSCallback<ECSProduct, ECSError>) {
        ecsProductManager.fetchProductDetails(product,ecsCallback)
    }

    @Throws(ECSException::class)
    fun fetchRetailers(ctn: String, ecsCallback: ECSCallback<ECSRetailerList?, ECSError>) {
        ecsRetailerManager.fetchRetailers(ctn,ecsCallback)
    }

    @Throws(ECSException::class)
    fun fetchRetailers(product: ECSProduct, ecsCallback: ECSCallback<ECSRetailerList?, ECSError>) {
        ecsRetailerManager.fetchRetailers(product.id,ecsCallback)
    }

}