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
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.request.*
import com.philips.platform.ecs.microService.util.ECSDataHolder

class ECSProductManager {

    var requestHandler = RequestHandler()

    fun getProducts(productCategory:String?, limit:Int, offset:Int, productFilter: ProductFilter?, ecsCallback :ECSCallback<ECSProducts, ECSError>){
        val ecsException = ECSApiValidator().getECSException(APIType.Locale)

        ecsException?.let { throw ecsException } ?: kotlin.run {

            val getProductsRequest= GetProductsRequest(productCategory,limit,offset,productFilter, ecsCallback)
            requestHandler.handleRequest(getProductsRequest)

        }
    }

    fun getProductFor(ctn: String, eCSCallback: ECSCallback<ECSProduct?, ECSError>) {
        val ecsException = ECSApiValidator().getECSException(APIType.Locale)

        ecsException?.let { throw ecsException } ?: kotlin.run {

            if(ECSDataHolder.config.isHybris) {
                val getProductForRequest = GetProductForRequest(ctn, eCSCallback)
                requestHandler.handleRequest(getProductForRequest)
            }else{
                val ecsProduct = ECSProduct(null,ctn,null)
                getSummaryForSingleProduct(ecsProduct, eCSCallback)
            }

        }
    }

    fun getSummaryForSingleProduct(ecsProduct: ECSProduct, eCSCallback: ECSCallback<ECSProduct?, ECSError>) {
        val getSummariesForProductsRequest = GetSummariesForProductsRequest(listOf(ecsProduct), object : ECSCallback<List<ECSProduct>, ECSError> {
            override fun onResponse(result: List<ECSProduct>) {

                if (!result.isNullOrEmpty()) {
                    eCSCallback.onResponse(result[0])
                } else {
                    eCSCallback.onResponse(ecsProduct)
                }
            }

            override fun onFailure(ecsError: ECSError) {

                when (ECSDataHolder.config.isHybris) {
                    true -> eCSCallback.onResponse(ecsProduct)
                    false -> eCSCallback.onFailure(ecsError) //note for non hybris flow ..we only fetch summary ...no pint of sending success , if it is not found
                }

            }
        })
        requestHandler.handleRequest(getSummariesForProductsRequest)
    }

    fun fetchProductSummaries(ctns: List<String>, ecsCallback: ECSCallback<List<ECSProduct>, ECSError>) {
        val ecsException = ECSApiValidator().getECSException(APIType.Locale)

        ecsException?.let { throw ecsException } ?: kotlin.run {

            var ecsProductList = mutableListOf<ECSProduct>()
            for (ctn in ctns) {
                var ecsProduct = ECSProduct(null, ctn, null)
                ecsProductList.add(ecsProduct)
            }
            val getSummariesForProductsRequest = GetSummariesForProductsRequest(ecsProductList, ecsCallback)
            requestHandler.handleRequest(getSummariesForProductsRequest)
        }
    }

    fun fetchProductDetails(product: ECSProduct, ecsCallback: ECSCallback<ECSProduct, ECSError>) {

        val ecsException = ECSApiValidator().getECSException(APIType.Locale)

        ecsException?.let { throw ecsException } ?: kotlin.run {

               //TODO remove the bad coding to wait 2 methoods to retutn their callbacks
                val callBacks = mutableListOf< ECSCallback<ECSProduct, ECSError>?>()
                fetchProductDisclaimer(product,ecsCallback,callBacks)
                fetchProductAsset(product, ecsCallback,callBacks)
        }
    }

    private fun fetchProductAsset(product: ECSProduct, ecsCallback: ECSCallback<ECSProduct, ECSError>, callBacks: MutableList<ECSCallback<ECSProduct, ECSError>?>){
        val getProductAssetRequest = GetProductAssetRequest(product, object : ECSCallback<ECSProduct, ECSError> {
            override fun onResponse(result: ECSProduct) {
                Log.d("ECSProductManager", result.toString())

                callBacks.add(this)

                if (callBacks.size > 1) ecsCallback.onResponse(result)

            }

            override fun onFailure(ecsError: ECSError) {
                //do nothing : error is already logged
                callBacks.add(this)
                if (callBacks.size > 1) ecsCallback.onResponse(product)
            }
        })
        requestHandler.handleRequest(getProductAssetRequest)
    }

    private fun fetchProductDisclaimer(product: ECSProduct, ecsCallback: ECSCallback<ECSProduct, ECSError>, callBacks: MutableList<ECSCallback<ECSProduct, ECSError>?>){
        val getProductDisclaimerRequest = GetProductDisclaimerRequest(product, object : ECSCallback<ECSProduct, ECSError> {
            override fun onResponse(result: ECSProduct) {
                Log.d("ECSProductManager", result.toString())
                callBacks.add(this)
                if (callBacks.size > 1) ecsCallback.onResponse(result)
            }

            override fun onFailure(ecsError: ECSError) {
                //do nothing : error is already logged
                callBacks.add(this)
                if (callBacks.size > 1) ecsCallback.onResponse(product)
            }
        })

        requestHandler.handleRequest(getProductDisclaimerRequest)
    }

}