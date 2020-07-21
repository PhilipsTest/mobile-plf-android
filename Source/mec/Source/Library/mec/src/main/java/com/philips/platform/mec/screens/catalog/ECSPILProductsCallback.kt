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
import com.philips.platform.mec.common.MecError

class ECSPILProductsCallback(private var ecsProductViewModel:EcsProductViewModel) : ECSCallback<ECSProducts, ECSError>{

    override fun onResponse(ecsProducts:ECSProducts) {
        ecsProductViewModel.ecsPILProducts.value = ecsProducts
    }

    override fun onFailure(ecsError: ECSError) {

    }
}