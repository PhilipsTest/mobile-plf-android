/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail


import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError

class ECSProductDetailCallback(private val ecsProductDetailViewModel: EcsProductDetailViewModel)  : ECSCallback<ECSProduct, ECSError> {
    lateinit var mECRequestType : MECRequestType
    override fun onResponse(ecsProduct: ECSProduct) {
        ecsProductDetailViewModel.ecsProduct.value = ecsProduct
    }

    override fun onFailure(error: ECSError) {
        val mecError = MecError(Exception(error), error, mECRequestType)
        ecsProductDetailViewModel.mecError.value = mecError
    }
}