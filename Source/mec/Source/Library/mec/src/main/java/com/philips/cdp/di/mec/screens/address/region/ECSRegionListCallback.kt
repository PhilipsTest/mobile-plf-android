package com.philips.cdp.di.mec.screens.address.region

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.region.ECSRegion
import com.philips.cdp.di.mec.common.MECRequestType
import com.philips.cdp.di.mec.common.MecError
import com.philips.cdp.di.mec.utils.MECutility

class ECSRegionListCallback(private var regionViewModel: RegionViewModel) : ECSCallback<List<ECSRegion>, Exception> {
     var mECRequestType = MECRequestType.MEC_FETCH_REGIONS
    override fun onResponse(result: List<ECSRegion>?) {
        regionViewModel.regionsList.value = result
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val mecError = MecError(error, ecsError,mECRequestType)
        if (MECutility.isAuthError(ecsError)) {
            regionViewModel.retryAPI(mECRequestType)
        }else{
            regionViewModel.mecError.value = mecError
        }
    }
}