/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.error.ECSErrorEnum
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class MECAddToProductCallback(private val ecsProductDetailViewModel: EcsProductDetailViewModel, private val request :String) : ECSCallback<ECSShoppingCart, Exception> {

    lateinit var mECRequestType : MECRequestType
    /**
     * On response.
     *
     * @param result the result
     */
    override fun onResponse(result: ECSShoppingCart?) {
        ecsProductDetailViewModel.addToProductCallBack.onResponse(result)
    }

    /**
     * On failure.
     * @param error     the error object
     * @param ecsError the error code
     */
    override fun onFailure(error: Exception?, ecsError: ECSError?) {
       

        if (  MECutility.isAuthError(ecsError)) {
            ecsProductDetailViewModel.retryAPI(mECRequestType)
        }else if (ecsError!!.errorcode == ECSErrorEnum.ECSCartError.errorCode){
            ecsProductDetailViewModel.createShoppingCart(request)
        } else{

            val mecError = MecError(error, ecsError,mECRequestType)
            ecsProductDetailViewModel.mecError.value = mecError
        }
    }
}