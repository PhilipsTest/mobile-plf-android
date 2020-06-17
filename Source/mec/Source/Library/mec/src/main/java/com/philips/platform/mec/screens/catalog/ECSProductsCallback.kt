/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.products.ECSProducts
import com.philips.platform.mec.common.MecError

class ECSProductsCallback(private var ecsProductViewModel:EcsProductViewModel) : ECSCallback<ECSProducts, Exception> {

    override fun onResponse(ecsProducts:ECSProducts?) {

        val mutableLiveData = ecsProductViewModel.ecsProductsList
        var value = mutableLiveData.value
        if (value.isNullOrEmpty()) value = mutableListOf<ECSProducts>()
        value?.add(ecsProducts!!)
        mutableLiveData.value = value
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val mecError = MecError(error, ecsError,null)
        ecsProductViewModel.mecError.value = mecError
    }
}