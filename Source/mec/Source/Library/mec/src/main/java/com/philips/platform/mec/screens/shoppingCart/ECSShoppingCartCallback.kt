/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.error.ECSErrorEnum
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSShoppingCartCallback(private val ecsShoppingCartViewModel: EcsShoppingCartViewModel) : ECSCallback<ECSShoppingCart, Exception> {
    var mECRequestType = MECRequestType.MEC_FETCH_SHOPPING_CART
    override fun onResponse(ecsShoppingCart: ECSShoppingCart?) {
        ecsShoppingCartViewModel.ecsShoppingCart.value = ecsShoppingCart
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)

        if (MECutility.isAuthError(ecsError)) {
            ecsShoppingCartViewModel.retryAPI(mECRequestType)
        } else if (ecsError!!.errorcode == ECSErrorEnum.ECSCartError.errorCode) {
            ecsShoppingCartViewModel.createShoppingCart("")
        } else {
            ecsShoppingCartViewModel.mecError.value = mecError
        }

    }
}