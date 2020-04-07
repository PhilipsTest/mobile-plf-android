/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart

import com.bazaarvoice.bvandroidsdk.BulkRatingOptions
import com.bazaarvoice.bvandroidsdk.BulkRatingsRequest
import com.bazaarvoice.bvandroidsdk.EqualityOperator
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.cart.ECSEntries
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility

class ECSShoppingCartRepository(ecsShoppingCartViewModel: EcsShoppingCartViewModel, val ecsServices: ECSServices)
{
    private var ecsShoppingCartCallback= ECSShoppingCartCallback(ecsShoppingCartViewModel)

    var authCallBack = object : ECSCallback<ECSOAuthData, Exception> {

        override fun onResponse(result: ECSOAuthData?) {
            fetchShoppingCart()
        }

        override fun onFailure(error: Exception, ecsError: ECSError) {
            val mecError = MecError(error, ecsError,MECRequestType.MEC_HYBRIS_AUTH)
            ecsShoppingCartViewModel.mecError.value = mecError
        }
    }

     fun fetchShoppingCart() {

         if(!MECutility.isExistingUser() || ECSConfiguration.INSTANCE.accessToken == null) {
             HybrisAuth.hybrisAuthentication(authCallBack)
         }else{
             this.ecsServices.fetchShoppingCart(ecsShoppingCartCallback)
         }

    }

    fun updateShoppingCart(entries: ECSEntries, quantity: Int) {
        ecsShoppingCartCallback.mECRequestType=MECRequestType.MEC_UPDATE_SHOPPING_CART
        this.ecsServices.updateShoppingCart(quantity,entries,ecsShoppingCartCallback)
    }

    fun fetchProductReview(ecsEntries: MutableList<ECSEntries>, ecsShoppingCartViewModel: EcsShoppingCartViewModel){

        val mecConversationsDisplayCallback = MECBulkRatingCallback(ecsEntries, ecsShoppingCartViewModel)
        val ctnList: MutableList<String> = mutableListOf()

        for(ecsEntry in ecsEntries){
            ctnList.add(ecsEntry.product.code.replace("/","_"))
        }
        val bvClient = MECDataHolder.INSTANCE.bvClient
        val request = MECConstant.KEY_BAZAAR_LOCALE?.let { BulkRatingsRequest.Builder(ctnList, BulkRatingOptions.StatsType.All).addFilter(BulkRatingOptions.Filter.ContentLocale, EqualityOperator.EQ, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter(it, MECDataHolder.INSTANCE.locale).build() }
        bvClient!!.prepareCall(request).loadAsync(mecConversationsDisplayCallback)

    }

    fun applyVoucher(voucherCode: String, ecsVoucherCallback: ECSVoucherCallback){
        ecsServices.applyVoucher(voucherCode,ecsVoucherCallback)
    }

    fun removeVoucher(voucherCode: String, ecsVoucherCallback: ECSVoucherCallback){
        ecsServices.removeVoucher(voucherCode,ecsVoucherCallback)
    }

    fun createCart(createShoppingCartCallback: ECSCallback<ECSShoppingCart, Exception>){
        ecsServices.createShoppingCart(createShoppingCartCallback)
    }

}