/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */
package com.philips.platform.mec.screens.catalog


import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError

class ECSProductsCallback(private var ecsProductViewModel:EcsProductViewModel) : ECSCallback<ECSProducts, ECSError>{

    var mECRequestType : MECRequestType = MECRequestType.MEC_FETCH_PRODUCTS

    override fun onResponse(ecsProducts:ECSProducts) {
        ecsProductViewModel.ecsPILProducts.value = ecsProducts
    }

    override fun onFailure(error: ECSError) {
        val occECSError = com.philips.platform.ecs.error.ECSError(error.errorCode?:-100,error.errorType?.name)
        val mecError = MecError(Exception(error), occECSError, mECRequestType)
        ecsProductViewModel.mecError.value = mecError
    }
}