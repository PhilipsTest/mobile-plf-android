/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address.region

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.region.ECSRegion
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSRegionListCallback(private var regionViewModel: RegionViewModel) : com.philips.platform.ecs.integration.ECSCallback<List<com.philips.platform.ecs.model.region.ECSRegion>, Exception> {
     var mECRequestType = MECRequestType.MEC_FETCH_REGIONS
    override fun onResponse(result: List<com.philips.platform.ecs.model.region.ECSRegion>?) {
        regionViewModel.regionsList.value = result
    }

    override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)
        if (MECutility.isAuthError(ecsError)) {
            regionViewModel.retryAPI(mECRequestType)
        }else{
            regionViewModel.mecError.value = mecError
        }
    }
}