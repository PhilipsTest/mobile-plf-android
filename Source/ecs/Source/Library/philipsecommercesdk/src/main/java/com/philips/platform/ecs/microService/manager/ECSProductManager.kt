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

import android.util.Log
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.request.GetProductAssetRequest
import com.philips.platform.ecs.microService.request.GetProductDisclaimerRequest
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

                var ecsProduct = ECSProduct(null,ctn,null)
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

                when(ECSDataHolder.config.isHybris){
                    true -> eCSCallback.onResponse(ecsProduct)
                    false ->  eCSCallback.onFailure(ecsError) //note for non hybris flow ..we only fetch summary ...no pint of sending success , if it is not found
                }

            }
        }).executeRequest()
    }

    fun fetchProductSummaries(ctns: List<String>, ecsCallback: ECSCallback<List<ECSProduct>, ECSError>) {
        val ecsException = ECSApiValidator().getECSException(APIType.Locale)

        ecsException?.let { throw ecsException } ?: kotlin.run {

            var ecsProductList = mutableListOf<ECSProduct>()
            for (ctn in ctns) {
                var ecsProduct = ECSProduct(null, ctn, null)
                ecsProductList.add(ecsProduct)
            }
            GetProductSummaryRequest(ecsProductList, ecsCallback).executeRequest()
        }
    }

    fun fetchProductDetails(product: ECSProduct, ecsCallback: ECSCallback<ECSProduct, ECSError>) {

        val ecsException = ECSApiValidator().getECSException(APIType.Locale)

        ecsException?.let { throw ecsException } ?: kotlin.run {
            // TODO give the call back once both threads finish their work
            fetchProductDisclaimer(product)
            fetchProductAsset(product, ecsCallback)
        }
    }

    private fun fetchProductAsset(product: ECSProduct,ecsCallback: ECSCallback<ECSProduct, ECSError>){
        GetProductAssetRequest(product,object : ECSCallback<ECSProduct, ECSError>{
            override fun onResponse(result: ECSProduct) {
                Log.d("ECSProductManager",result.toString())
                ecsCallback.onResponse(result)
            }

            override fun onFailure(ecsError: ECSError) {
                //do nothing : error is already logged
                ecsCallback.onResponse(product)
            }
        }).executeRequest()
    }

    private fun fetchProductDisclaimer(product: ECSProduct){
        GetProductDisclaimerRequest(product,object : ECSCallback<ECSProduct, ECSError>{
            override fun onResponse(result: ECSProduct) {
                Log.d("ECSProductManager",result.toString())
            }

            override fun onFailure(ecsError: ECSError) {
                //do nothing : error is already logged
            }
        }).executeRequest()
    }

}