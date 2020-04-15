/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSCreateAddressCallBack(private var addressViewModel: AddressViewModel) :ECSCallback<ECSAddress, Exception> {

    var mECRequestType = MECRequestType.MEC_CREATE_ADDRESS
    override fun onResponse(eCSAddress: ECSAddress) {
        addressViewModel.eCSAddress.value = eCSAddress
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)
        if (MECutility.isAuthError(ecsError)) {
            addressViewModel.retryAPI(mECRequestType)
        }else{
            addressViewModel.mecError.value = mecError
        }
    }
}