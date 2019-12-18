package com.philips.cdp.di.mec.screens.retailers

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList
import com.philips.cdp.di.mec.common.MecError

class ECSRetailerListCallback(private  val ecsRetailerViewModel: ECSRetailerViewModel) : ECSCallback<ECSRetailerList, Exception> {

    override fun onResponse(result: ECSRetailerList?) {
        ecsRetailerViewModel.ecsRetailerList.value = result
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val mecError = MecError(error, ecsError)
        ecsRetailerViewModel.mecError.value = mecError
    }
}