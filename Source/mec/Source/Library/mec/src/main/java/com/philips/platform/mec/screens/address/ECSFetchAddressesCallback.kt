/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.address.ECSAddress
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSFetchAddressesCallback (private val addressViewModel: AddressViewModel)  : com.philips.platform.ecs.integration.ECSCallback<List<com.philips.platform.ecs.model.address.ECSAddress>, Exception> {

    var mECRequestType  = MECRequestType.MEC_FETCH_SAVED_ADDRESSES
    override fun onResponse(ecsAddresses: List<com.philips.platform.ecs.model.address.ECSAddress>) {
        addressViewModel.ecsAddresses.value = ecsAddresses
    }

    override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)
        if (MECutility.isAuthError(ecsError)) {
            addressViewModel.retryAPI(mECRequestType)
        }else{
            addressViewModel.mecError.value = mecError
        }
    }
}