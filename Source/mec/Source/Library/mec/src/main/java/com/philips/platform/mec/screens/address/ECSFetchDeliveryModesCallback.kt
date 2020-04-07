/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.address.ECSDeliveryMode
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSFetchDeliveryModesCallback(private val addressViewModel: AddressViewModel) : ECSCallback<List<ECSDeliveryMode>, Exception> {
    lateinit var mECRequestType : MECRequestType
    override fun onResponse(ecsDeliveryModes: List<ECSDeliveryMode>?) {
        addressViewModel.ecsDeliveryModes.value=ecsDeliveryModes
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