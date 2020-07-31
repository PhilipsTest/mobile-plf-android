/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import com.bazaarvoice.bvandroidsdk.BulkRatingOptions
import com.bazaarvoice.bvandroidsdk.BulkRatingsRequest
import com.bazaarvoice.bvandroidsdk.EqualityOperator
import com.philips.platform.ecs.microService.ECSServices
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder

class ECSCatalogRepository {

    fun getProducts(offset: Int, limit: Int, ecsCallback: ECSProductsCallback, microService: ECSServices) {
        microService.fetchProducts(productCategory = MECDataHolder.INSTANCE.rootCategory, offset = offset, limit = limit, ecsCallback = ecsCallback)
    }

    fun fetchProductSummaries(ctnS: MutableList<String>, ecsCallback: ECSProductsCallback, microService: ECSServices) {
        microService.fetchProductSummaries(ctnS, ecsCallback)
    }

    fun fetchProductReview(ecsProducts: List<ECSProduct>, ecsProductViewModel: EcsProductViewModel){

        val mecConversationsDisplayCallback = MECBulkRatingConversationsDisplayCallback(ecsProducts, ecsProductViewModel)
        val ctnList: MutableList<String> = getCtnList(ecsProducts)
        val bvClient = MECDataHolder.INSTANCE.bvClient
        val request = BulkRatingsRequest.Builder(ctnList, BulkRatingOptions.StatsType.All).addFilter(BulkRatingOptions.Filter.ContentLocale, EqualityOperator.EQ, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter(MECConstant.KEY_BAZAAR_LOCALE, MECDataHolder.INSTANCE.locale).build()
        val prepareCall = bvClient?.prepareCall(request)
        prepareCall?.loadAsync(mecConversationsDisplayCallback)
    }

    internal fun getCtnList(ecsProducts: List<ECSProduct>): MutableList<String> {
        val ctnList: MutableList<String> = mutableListOf()
        for (ecsProduct in ecsProducts) {
            ctnList.add(ecsProduct.ctn.replace("/", "_"))
        }
        return ctnList
    }

}

