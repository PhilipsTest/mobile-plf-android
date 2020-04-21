/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address.region

import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.util.ECSConfiguration

class RegionRepository(val ecsServices: com.philips.platform.ecs.ECSServices) {

    fun getRegions(ecsRegionListCallback: ECSRegionListCallback){
        ecsServices.fetchRegions(com.philips.platform.ecs.util.ECSConfiguration.INSTANCE.country,ecsRegionListCallback)
    }

}