/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.cart.ECSShoppingCart
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSShoppingCartCallback(private val ecsShoppingCartViewModel: EcsShoppingCartViewModel) : com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.cart.ECSShoppingCart, Exception> {
    var mECRequestType = MECRequestType.MEC_FETCH_SHOPPING_CART
    override fun onResponse(ecsShoppingCart: com.philips.platform.ecs.model.cart.ECSShoppingCart?) {
        if(mECRequestType==MECRequestType.MEC_UPDATE_SHOPPING_CART){ // if any product quantity of cart is changed
            ecsShoppingCartViewModel.tagProductIfDeleted()
        }
        ecsShoppingCartViewModel.ecsShoppingCart.value = ecsShoppingCart
    }

    override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)

        if (MECutility.isAuthError(ecsError)) {
            ecsShoppingCartViewModel.retryAPI(mECRequestType)
        } else if (ecsError!!.errorcode == com.philips.platform.ecs.error.ECSErrorEnum.ECSCartError.errorCode) {
            ecsShoppingCartViewModel.createShoppingCart("")
        } else {
            ecsShoppingCartViewModel.mecError.value = mecError
        }

    }
}