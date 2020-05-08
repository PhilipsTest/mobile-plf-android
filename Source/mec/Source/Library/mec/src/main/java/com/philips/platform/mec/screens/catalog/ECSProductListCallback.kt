/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.ecs.model.products.ECSProducts
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError

class ECSProductListCallback(private var ecsProductViewModel:EcsProductViewModel) : com.philips.platform.ecs.integration.ECSCallback<List<com.philips.platform.ecs.model.products.ECSProduct>, Exception> {
    lateinit var mECRequestType : MECRequestType
    override fun onResponse(ecsProductList: List<com.philips.platform.ecs.model.products.ECSProduct>?) {

        val mutableLiveData = ecsProductViewModel.ecsProductsList

        val value = mutableList(mutableLiveData as MutableLiveData<MutableList<Any>>) as  MutableList<com.philips.platform.ecs.model.products.ECSProducts>

        val ecsProducts = com.philips.platform.ecs.model.products.ECSProducts()
        ecsProducts.products = ecsProductList

        value.add(ecsProducts)
        mutableLiveData.value = value as MutableList<Any>
    }

    override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)
        ecsProductViewModel.mecError.value = mecError
    }
}