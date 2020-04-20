/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.products.ECSProducts
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError

class ECSCategorizedProductsCallback(private var  ctns: List<String> ,private var ecsProductViewModel:EcsProductViewModel) : com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.products.ECSProducts, Exception> {
    lateinit var mECRequestType : MECRequestType
    override fun onResponse(ecsProducts: com.philips.platform.ecs.model.products.ECSProducts?) {

        val mutableLiveData = ecsProductViewModel.ecsProductsList
        var value = mutableLiveData.value
        if (value.isNullOrEmpty()) value = mutableListOf<com.philips.platform.ecs.model.products.ECSProducts>()
        value?.add(ecsProducts!!)
        mutableLiveData.value = value
    }

    override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)
        ecsProductViewModel.mecError.value = mecError
    }
}