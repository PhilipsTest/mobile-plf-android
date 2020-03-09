package com.philips.cdp.di.mec.screens.address.region

import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.util.ECSConfiguration

class RegionRepository(val ecsServices: ECSServices) {

    fun getRegions(ecsRegionListCallback: ECSRegionListCallback){
        ecsServices.fetchRegions(ECSConfiguration.INSTANCE.country,ecsRegionListCallback)
    }

}