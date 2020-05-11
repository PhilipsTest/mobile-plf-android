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
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.request.GetProductForRequest
import com.philips.platform.ecs.microService.request.GetProductSummaryRequest
import com.philips.platform.ecs.microService.util.ECSDataHolder

class ECSProductManager {

    fun getProductFor(ctn: String, eCSCallback: ECSCallback<ECSProduct?, ECSError>) {
        val ecsException = ECSApiValidator().getECSException(APIType.Locale)
        ecsException?.let { throw ecsException } ?: kotlin.run {

            if(ECSDataHolder.config.isHybris) {
                GetProductForRequest(ctn, eCSCallback).executeRequest()
            }else{

                var ecsProduct = ECSProduct(null,ctn,null,null)
                getSummaryForSingleProduct(ecsProduct, eCSCallback)
            }

        }
    }

    fun getSummaryForSingleProduct(ecsProduct: ECSProduct, eCSCallback: ECSCallback<ECSProduct?, ECSError>) {
        GetProductSummaryRequest(listOf(ecsProduct), object : ECSCallback<List<ECSProduct>, ECSError> {
            override fun onResponse(result: List<ECSProduct>) {

                if (!result.isNullOrEmpty()) {
                    eCSCallback.onResponse(result[0])
                } else {
                    eCSCallback.onResponse(ecsProduct)
                }
            }

            override fun onFailure(ecsError: ECSError) {
                eCSCallback.onResponse(ecsProduct)
            }
        }).executeRequest()
    }

    fun fetchProductSummaries(ctns: List<String>, ecsCallback: ECSCallback<List<ECSProduct>, ECSError>) {
        var ecsProductList = mutableListOf<ECSProduct>()
        for (ctn in ctns){
            var ecsProduct = ECSProduct(null,ctn,null,null)
            ecsProductList.add(ecsProduct)
        }
        GetProductSummaryRequest(ecsProductList,ecsCallback).executeRequest()
    }

    fun fetchProductDetails(product: ECSProduct, ecsCallback: ECSCallback<ECSProduct, ECSError>) {

    }

}