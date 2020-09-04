/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart

import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSShoppingCartCallback(private val ecsShoppingCartViewModel: EcsShoppingCartViewModel) : ECSCallback<ECSShoppingCart, ECSError> {
    var mECRequestType = MECRequestType.MEC_FETCH_SHOPPING_CART
    override fun onResponse(result: ECSShoppingCart) {
        if(mECRequestType==MECRequestType.MEC_UPDATE_SHOPPING_CART){ // if any product quantity of cart is changed
            ecsShoppingCartViewModel.tagProductAddedOrDeleted()
        }
        ecsShoppingCartViewModel.ecsShoppingCart.value = result
    }

    override fun onFailure(ecsError: ECSError) {
        val occECSError = com.philips.platform.ecs.error.ECSError(ecsError.errorCode?:-100,ecsError.errorType?.name)
        val mecError = MecError(Exception(ecsError.errorMessage), occECSError, mECRequestType)

        if (MECutility.isAuthError(ecsError)) {
            ecsShoppingCartViewModel.retryAPI(mECRequestType)
        }  else {
            ecsShoppingCartViewModel.mecError.value = mecError
        }

    }
}