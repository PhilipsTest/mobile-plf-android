/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError

class SetDeliveryAddressCallBack(private var addressViewModel: AddressViewModel) :ECSCallback<Boolean, Exception> {

    var mECRequestType : MECRequestType?=MECRequestType.MEC_SET_DELIVERY_ADDRESS
    override fun onResponse(isSetDeliveryAddress: Boolean) {
        addressViewModel.isDeliveryAddressSet.value = isSetDeliveryAddress
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)
        addressViewModel.mecError.value = mecError
        addressViewModel.isDeliveryAddressSet.value =false
    }
}