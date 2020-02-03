package com.philips.cdp.di.mec.screens.address

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.region.ECSRegion
import com.philips.cdp.di.mec.common.MecError

class ECSCreateAddressCallBack(private var addressViewModel: AddressViewModel) :ECSCallback<ECSAddress, Exception> {

    override fun onResponse(eCSAddress: ECSAddress) {
        addressViewModel.eCSAddress.value = eCSAddress
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val mecError = MecError(error, ecsError)
        addressViewModel.mecError.value = mecError
    }
}