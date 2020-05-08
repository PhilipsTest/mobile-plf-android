/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import com.philips.platform.ecs.model.address.Region
import com.philips.platform.ecs.model.region.ECSRegion

class MECRegions(private val regionList: List<com.philips.platform.ecs.model.region.ECSRegion>){

    fun getRegionNames(): Array<String> ?{

        val rowItems : Array<String> = Array(regionList.size) { "it = $it" }

        if (regionList != null) {
            for (i in regionList.indices) {
                rowItems[i] = regionList[i].name
            }
        }
        return rowItems
    }

    fun getRegion(regionName:String): com.philips.platform.ecs.model.address.Region {

        val region = com.philips.platform.ecs.model.address.Region()
        for (ecsRegion in regionList){

            if(ecsRegion.name.equals(regionName,true)){

                region.isocodeShort = ecsRegion.getIsocode()
                region.name = regionName
            }
        }
        return region
    }

}