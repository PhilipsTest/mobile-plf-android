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
import com.philips.platform.mec.utils.MECutility

class ECSSetDeliveryModesCallback(private val addressViewModel: AddressViewModel) : ECSCallback<Boolean, Exception> {
    lateinit var mECRequestType : MECRequestType

    override fun onResponse(result: Boolean?) {
        addressViewModel.ecsDeliveryModeSet.value=result
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