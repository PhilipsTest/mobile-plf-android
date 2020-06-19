/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class UpdateAddressCallBack(private var addressViewModel: AddressViewModel) :ECSCallback<Boolean, Exception> {

    var mECRequestType : MECRequestType = MECRequestType.MEC_UPDATE_ADDRESS
    override fun onResponse(isSetDeliveryAddress: Boolean) {
        addressViewModel.isAddressUpdate.value = isSetDeliveryAddress
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)

        if (MECutility.isAuthError(ecsError)) {
            addressViewModel.retryAPI(mECRequestType)
        }else {
            addressViewModel.mecError.value = mecError
        }
    }
}