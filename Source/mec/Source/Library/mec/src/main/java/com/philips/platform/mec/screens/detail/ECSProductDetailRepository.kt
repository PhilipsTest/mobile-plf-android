/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import com.bazaarvoice.bvandroidsdk.*
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSException
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart


import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder

class ECSProductDetailRepository(private val ecsProductDetailViewModel: EcsProductDetailViewModel, val ecsServices: ECSServices) {

    var ecsProductDetailCallBack= ECSProductDetailCallback(ecsProductDetailViewModel)
    var mECAddToProductCallback = MECAddToProductCallback(ecsProductDetailViewModel)


    var bvClient = MECDataHolder.INSTANCE.bvClient
    var reviewsCb = MECReviewConversationsDisplayCallback(ecsProductDetailViewModel)
    var ratingCb = MECDetailBulkRatingConversationsDisplayCallback(ecsProductDetailViewModel)


    fun getProductDetail(ecsProduct: ECSProduct){
        ecsProductDetailCallBack.mECRequestType=MECRequestType.MEC_FETCH_PRODUCT_DETAILS
        try{
        ecsServices.microService.fetchProductDetails(ecsProduct,ecsProductDetailCallBack)
        }catch (e : ECSException){
            val ecsError = ECSError(e.message ?:"",e.errorCode,null)
            ecsProductDetailCallBack.onFailure(ecsError)
        }
    }

    fun fetchProductReview(ctn: String, pageNumber: Int, pageSize: Int){
        val request = ReviewsRequest.Builder(ctn.replace("/","_"), pageSize, pageNumber).addSort(ReviewOptions.Sort.SubmissionTime, SortOrder.DESC).addFilter(ReviewOptions.Filter.ContentLocale, EqualityOperator.EQ, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter(MECConstant.KEY_BAZAAR_LOCALE!!, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter("FilteredStats", "Reviews").build()
        val prepareCall = bvClient!!.prepareCall(request)
        prepareCall.loadAsync(reviewsCb)
    }

    fun getRatings(ctn: String) {
        val ctns = mutableListOf(ctn)
        val request = BulkRatingsRequest.Builder(ctns, BulkRatingOptions.StatsType.All).addFilter(BulkRatingOptions.Filter.ContentLocale, EqualityOperator.EQ, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter(MECConstant.KEY_BAZAAR_LOCALE!!, MECDataHolder.INSTANCE.locale).build()
        val prepareCall = bvClient!!.prepareCall(request)
        prepareCall.loadAsync(ratingCb)
    }

    fun addTocart(ecsProduct: ECSProduct){
        mECAddToProductCallback.mECRequestType= MECRequestType.MEC_ADD_PRODUCT_TO_SHOPPING_CART
        ecsServices.microService.addProductToShoppingCart(ctn = ecsProduct.ctn,ecsCallback = mECAddToProductCallback)
    }

    fun createCart(ctn: String,createShoppingCartCallback: ECSCallback<ECSShoppingCart, ECSError>){
       ecsServices.microService.createShoppingCart(ctn = ctn,ecsCallback = createShoppingCartCallback)
    }

}