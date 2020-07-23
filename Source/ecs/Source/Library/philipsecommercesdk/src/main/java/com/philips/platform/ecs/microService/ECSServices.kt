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
import com.philips.platform.appinfra.BuildConfig
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSException
import com.philips.platform.ecs.microService.manager.ECSCartManager
import com.philips.platform.ecs.microService.manager.ECSConfigManager
import com.philips.platform.ecs.microService.manager.ECSProductManager
import com.philips.platform.ecs.microService.manager.ECSRetailerManager
import com.philips.platform.ecs.microService.model.cart.ECSItem
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.ecs.microService.util.ECSDataHolder



class ECSServices(appInfra: AppInfra) {

    val PIL_ECS_NOTATION = "ECS"

    internal var ecsConfigManager = ECSConfigManager()
    internal var ecsProductManager = ECSProductManager()
    internal var ecsRetailerManager = ECSRetailerManager()
    internal var ecsCartManager = ECSCartManager()

    init {
        ECSDataHolder.appInfra = appInfra
        ECSDataHolder.loggingInterface= appInfra.logging?.createInstanceForComponent(PIL_ECS_NOTATION, BuildConfig.VERSION_NAME)
    }

    fun configureECS(ecsCallback: ECSCallback<ECSConfig, ECSError>) {
        ecsConfigManager.getConfigObject(ecsCallback)
    }


    @Throws(ECSException::class)
    @JvmOverloads
    fun fetchProducts(productCategory:String?=null, limit: Int=20, offset:Int=0, productFilter: ProductFilter?=null,ecsCallback :ECSCallback<ECSProducts, ECSError> ) {
        ecsProductManager.getProducts(productCategory,limit,offset,productFilter,ecsCallback)
    }

    @Throws(ECSException::class)
    fun fetchProduct(ctn: String, eCSCallback:ECSCallback<ECSProduct?, ECSError>) {
        ecsProductManager.getProductFor(ctn.trim(), eCSCallback)
    }

    @Throws(ECSException::class)
    fun fetchProductSummaries(ctns: List<String>, ecsCallback: ECSCallback<ECSProducts, ECSError>) {
        ecsProductManager.fetchProductSummaries(ctns,ecsCallback)
    }

    @Throws(ECSException::class)
    fun fetchProductDetails(product:ECSProduct, ecsCallback:ECSCallback<ECSProduct, ECSError>) {
        ecsProductManager.fetchProductDetails(product,ecsCallback)
    }

    @Throws(ECSException::class)
    fun fetchRetailers(ctn: String, ecsCallback: ECSCallback<ECSRetailerList?, ECSError>) {
        ecsRetailerManager.fetchRetailers(ctn.trim(),ecsCallback)
    }

    @Throws(ECSException::class)
    fun createShoppingCart(ctn: String, quantity : Int = 1, ecsCallback: ECSCallback<ECSShoppingCart, ECSError>){
        ecsCartManager.createECSShoppingCart(ctn.trim(),quantity,ecsCallback)

    }

    @Throws(ECSException::class)
    fun addProductToShoppingCart(ctn: String, quantity : Int = 1, ecsCallback: ECSCallback<ECSShoppingCart, ECSError>){
        ecsCartManager.addProductToShoppingCart(ctn.trim(),quantity,ecsCallback)

    }

    @Throws(ECSException::class)
    fun fetchShoppingCart(ecsCallback: ECSCallback<ECSShoppingCart, ECSError>){
       ecsCartManager.fetchShoppingCart(ecsCallback)
    }

    @Throws(ECSException::class)
    fun updateShoppingCart(cartItem: ECSItem, quantity : Int, ecsCallback: ECSCallback<ECSShoppingCart, ECSError>){
        ecsCartManager.updateShoppingCart(cartItem.entryNumber,quantity,ecsCallback)
    }

}