/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.retailers

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.retailers.ECSRetailer
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder

class ECSRetailerListCallback(private val ecsRetailerViewModel: ECSRetailerViewModel) : ECSCallback<ECSRetailerList, Exception> {

    override fun onResponse(result: ECSRetailerList?) {

        if (result != null)
            removePhilipsStoreForHybris(result)

        ecsRetailerViewModel.ecsRetailerList.value = result
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val mecError = MecError(error, ecsError,null)
        ecsRetailerViewModel.mecError.value = mecError
    }


    fun removePhilipsStoreForHybris(result: ECSRetailerList): ECSRetailerList {

        if (!MECDataHolder.INSTANCE.hybrisEnabled) return result
        val retailers = result.retailers

        val iterator = retailers.iterator()

        while (iterator.hasNext()){

            val ecsRetailer = iterator.next()

            if(isPhilipsRetailer(ecsRetailer)){
                iterator.remove()
            }
        }

        return result
    }

    private fun isPhilipsRetailer(ecsRetailer: ECSRetailer?): Boolean {
        if (ecsRetailer?.isPhilipsStore.equals("Y")) {
            return true
        }
        return false
    }
}