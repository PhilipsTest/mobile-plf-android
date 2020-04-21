/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError

class ECSProductDetailCallback(private val ecsProductDetailViewModel: EcsProductDetailViewModel)  : com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.products.ECSProduct, Exception> {
    lateinit var mECRequestType : MECRequestType
    override fun onResponse(ecsProduct: com.philips.platform.ecs.model.products.ECSProduct?) {
        ecsProductDetailViewModel.ecsProduct
        ecsProductDetailViewModel.ecsProduct.value = ecsProduct
    }

    override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)
        ecsProductDetailViewModel.mecError.value = mecError
    }
}