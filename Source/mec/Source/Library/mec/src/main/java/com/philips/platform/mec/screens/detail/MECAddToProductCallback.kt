/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class MECAddToProductCallback(private val ecsProductDetailViewModel: EcsProductDetailViewModel) : ECSCallback<ECSShoppingCart, ECSError> {

    lateinit var mECRequestType: MECRequestType

    override fun onResponse(result: ECSShoppingCart) {
        ecsProductDetailViewModel.addToProductCallBack.onResponse(result)
    }

    override fun onFailure(ecsError :ECSError) {

        when {
            MECutility.isAuthError(ecsError) -> {
                ecsProductDetailViewModel.retryAPI(mECRequestType)
            }
            ecsError.errorCode == ECSErrorEnum.ECSCartError.errorCode -> {
                ecsProductDetailViewModel.createShoppingCart()
            }
            else -> {

                val occECSError = com.philips.platform.ecs.error.ECSError(ecsError.errorCode?:-100,ecsError.errorType?.name)
                val mecError = MecError(Exception(ecsError.errorMessage), occECSError, mECRequestType)
                ecsProductDetailViewModel.mecError.value = mecError
            }
        }
    }
}