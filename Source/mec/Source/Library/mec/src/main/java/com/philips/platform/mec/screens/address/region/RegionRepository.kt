/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address.region

import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.util.ECSConfiguration

class RegionRepository(val ecsServices: ECSServices) {

    fun getRegions(ecsRegionListCallback: ECSRegionListCallback){
        ecsServices.fetchRegions(ECSConfiguration.INSTANCE.country,ecsRegionListCallback)
    }

}