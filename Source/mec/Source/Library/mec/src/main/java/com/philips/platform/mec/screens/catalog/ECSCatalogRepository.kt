/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import com.bazaarvoice.bvandroidsdk.BulkRatingOptions
import com.bazaarvoice.bvandroidsdk.BulkRatingsRequest
import com.bazaarvoice.bvandroidsdk.EqualityOperator
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.model.products.ECSProducts
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder

class ECSCatalogRepository {

    fun getProducts(pageNumber: Int, pageSize: Int, ecsCallback: ECSPILProductsCallback, eCSServices: ECSServices) {
        eCSServices.microService.fetchProducts(productCategory = MECDataHolder.INSTANCE.rootCategory, offset = pageNumber, limit = pageSize, ecsCallback = ecsCallback)
    }

    fun getCategorizedProductsForRetailer(ctnS: MutableList<String>, ecsCallback: ECSPILProductsCallback, eCSServices: ECSServices) {
        eCSServices.microService.fetchProductSummaries(ctnS, ecsCallback)
    }

    fun fetchProductReview(ecsProducts: List<com.philips.platform.ecs.microService.model.product.ECSProduct>, ecsProductViewModel: EcsProductViewModel){

        val mecConversationsDisplayCallback = MECBulkRatingConversationsDisplayCallback(ecsProducts, ecsProductViewModel)
        val ctnList: MutableList<String> = mutableListOf()

        for(ecsProduct in ecsProducts){
            ctnList.add(ecsProduct.ctn.replace("/","_"))
        }
        val bvClient = MECDataHolder.INSTANCE.bvClient
        val request = BulkRatingsRequest.Builder(ctnList, BulkRatingOptions.StatsType.All).addFilter(BulkRatingOptions.Filter.ContentLocale, EqualityOperator.EQ, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter(MECConstant.KEY_BAZAAR_LOCALE, MECDataHolder.INSTANCE.locale).build()
        bvClient?.prepareCall(request)?.loadAsync(mecConversationsDisplayCallback)
    }

}

