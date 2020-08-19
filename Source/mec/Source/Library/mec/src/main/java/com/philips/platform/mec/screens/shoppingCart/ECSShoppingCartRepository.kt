/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart

import com.bazaarvoice.bvandroidsdk.BVConversationsClient
import com.bazaarvoice.bvandroidsdk.BulkRatingOptions
import com.bazaarvoice.bvandroidsdk.BulkRatingsRequest
import com.bazaarvoice.bvandroidsdk.EqualityOperator
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.cart.ECSItem
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.ecs.model.oauth.ECSOAuthData
import com.philips.platform.ecs.util.ECSConfiguration
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility

class ECSShoppingCartRepository(var ecsShoppingCartViewModel: EcsShoppingCartViewModel, var ecsServices: ECSServices)
{
    var ecsShoppingCartCallback= ECSShoppingCartCallback(ecsShoppingCartViewModel)

    var authCallBack = object : com.philips.platform.ecs.integration.ECSCallback<ECSOAuthData, Exception> {

        override fun onResponse(result: ECSOAuthData?) {
            fetchShoppingCart()
        }

        override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError) {
            val mecError = MecError(error, ecsError,MECRequestType.MEC_HYBRIS_AUTH)
            ecsShoppingCartViewModel.mecError.value = mecError
        }
    }

     fun fetchShoppingCart() {
         ecsShoppingCartCallback.mECRequestType=MECRequestType.MEC_FETCH_SHOPPING_CART
         if(!MECutility.isExistingUser() || ECSConfiguration.INSTANCE.accessToken == null) {
             HybrisAuth.hybrisAuthentication(authCallBack)
         }else{
             ecsServices.microService.fetchShoppingCart(ecsShoppingCartCallback)
         }
    }

    fun updateShoppingCart(cartItem: ECSItem, quantity: Int) {
        ecsShoppingCartCallback.mECRequestType=MECRequestType.MEC_UPDATE_SHOPPING_CART
        this.ecsServices.microService.updateShoppingCart(cartItem,quantity,ecsShoppingCartCallback)
    }

    fun fetchProductReview(ecsItems: MutableList<ECSItem>, ecsShoppingCartViewModel: EcsShoppingCartViewModel ,bvClient: BVConversationsClient?){

        val mecConversationsDisplayCallback = MECBulkRatingCallback(ecsItems, ecsShoppingCartViewModel)
        val ctnList: MutableList<String> = mutableListOf()

        for(ecsItem in ecsItems){
            ecsItem.ctn?.replace("/","_")?.let { ctnList.add(it) }
        }
        val request = MECConstant.KEY_BAZAAR_LOCALE?.let { BulkRatingsRequest.Builder(ctnList, BulkRatingOptions.StatsType.All).addFilter(BulkRatingOptions.Filter.ContentLocale, EqualityOperator.EQ, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter(it, MECDataHolder.INSTANCE.locale).build() }
        val prepareCall = bvClient!!.prepareCall(request)
        prepareCall.loadAsync(mecConversationsDisplayCallback)

    }

    fun applyVoucher(voucherCode: String, ecsVoucherCallback: ECSVoucherCallback){
        ecsServices.applyVoucher(voucherCode,ecsVoucherCallback)
    }

    fun removeVoucher(voucherCode: String, ecsVoucherCallback: ECSVoucherCallback){
        ecsServices.removeVoucher(voucherCode,ecsVoucherCallback)
    }

    fun createCart(ctn:String, createShoppingCartCallback: ECSCallback<ECSShoppingCart, ECSError>){
        ecsServices.microService.createShoppingCart(ctn = ctn,ecsCallback = createShoppingCartCallback)
    }



}