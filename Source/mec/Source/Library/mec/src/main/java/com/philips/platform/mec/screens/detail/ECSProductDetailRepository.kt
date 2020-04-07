/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import com.bazaarvoice.bvandroidsdk.*
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder

class ECSProductDetailRepository(private val ecsProductDetailViewModel: EcsProductDetailViewModel, val ecsServices: ECSServices) {

    var ecsProductDetailCallBack= ECSProductDetailCallback(ecsProductDetailViewModel)
    var mECAddToProductCallback = MECAddToProductCallback(ecsProductDetailViewModel,"AddToCart")


    var bvClient = MECDataHolder.INSTANCE.bvClient
    var reviewsCb = MECReviewConversationsDisplayCallback(ecsProductDetailViewModel)
    var ratingCb = MECDetailBulkRatingConversationsDisplayCallback(ecsProductDetailViewModel)

    fun getProductDetail(ecsProduct: ECSProduct){
        ecsProductDetailCallBack.mECRequestType=MECRequestType.MEC_FETCH_PRODUCT_DETAILS
        ecsServices.fetchProductDetails(ecsProduct,ecsProductDetailCallBack)
    }

    fun fetchProductReview(ctn: String, pageNumber: Int, pageSize: Int){
        val request = ReviewsRequest.Builder(ctn.replace("/","_"), pageSize, pageNumber).addSort(ReviewOptions.Sort.SubmissionTime, SortOrder.DESC).addFilter(ReviewOptions.Filter.ContentLocale, EqualityOperator.EQ, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter(MECConstant.KEY_BAZAAR_LOCALE!!, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter("FilteredStats", "Reviews").build()
        val prepareCall = bvClient!!.prepareCall(request)
        reviewsCb.mECRequestType=MECRequestType.MEC_FETCH_REVIEW
        prepareCall.loadAsync(reviewsCb)
    }

    fun getRatings(ctn: String) {
        var ctns = mutableListOf(ctn)
        val request = BulkRatingsRequest.Builder(ctns, BulkRatingOptions.StatsType.All).addFilter(BulkRatingOptions.Filter.ContentLocale, EqualityOperator.EQ, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter(MECConstant.KEY_BAZAAR_LOCALE!!, MECDataHolder.INSTANCE.locale).build()
        val prepareCall = bvClient!!.prepareCall(request)
        prepareCall.loadAsync(ratingCb)
    }

    fun addTocart(ecsProduct: ECSProduct){
        mECAddToProductCallback.mECRequestType= MECRequestType.MEC_ADD_PRODUCT_TO_SHOPPING_CART
        ecsServices.addProductToShoppingCart(ecsProduct,mECAddToProductCallback)
    }

    fun createCart(createShoppingCartCallback: ECSCallback<ECSShoppingCart, Exception>){
       ecsServices.createShoppingCart(createShoppingCartCallback)
    }





}