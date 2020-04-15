/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError

class ECSProductListForCTNsCallback (private val ecsProductDetailViewModel: EcsProductDetailViewModel): ECSCallback<List<ECSProduct>,Exception>{
    lateinit var mECRequestType : MECRequestType
    override fun onResponse(result: List<ECSProduct>) {
        ecsProductDetailViewModel.ecsProduct.value = result.get(0)
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)
        ecsProductDetailViewModel.mecError.value = mecError
    }
}