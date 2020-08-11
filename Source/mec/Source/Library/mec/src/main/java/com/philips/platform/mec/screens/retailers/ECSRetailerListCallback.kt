/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.retailers

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.retailer.ECSRetailer
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder

class ECSRetailerListCallback(private val ecsRetailerViewModel: ECSRetailerViewModel) : ECSCallback<ECSRetailerList?, ECSError> {

    var mECRequestType : MECRequestType = MECRequestType.MEC_FETCH_RETAILER_FOR_PRODUCT

    override fun onResponse(result:ECSRetailerList?) {
        removePhilipsStoreForHybris(result)
        ecsRetailerViewModel.ecsRetailerList.value = result
    }

    override fun onFailure(ecsError: ECSError) {
        val occECSError = com.philips.platform.ecs.error.ECSError(ecsError.errorCode?:-100,ecsError.errorType?.name)
        val mecError = MecError(Exception(ecsError.errorMessage), occECSError, mECRequestType)
        ecsRetailerViewModel.mecError.value = mecError
    }


    fun removePhilipsStoreForHybris(result:ECSRetailerList?): ECSRetailerList? {

        if (!MECDataHolder.INSTANCE.hybrisEnabled) return result
        val retailers = result?.getRetailers()


        var ecsPhilipsRetailer : ECSRetailer ? = null


        val iterator = retailers?.iterator()

        while (iterator?.hasNext() == true){

            val ecsRetailer = iterator.next()

            if(isPhilipsRetailer(ecsRetailer)){
                ecsPhilipsRetailer = ecsRetailer
            }
        }

        ecsPhilipsRetailer?.let {retailers?.toMutableList()?.remove(ecsPhilipsRetailer)}

        return result
    }

    private fun isPhilipsRetailer(ecsRetailer: ECSRetailer?): Boolean {
        if (ecsRetailer?.isPhilipsStore.equals("Y")) {
            return true
        }
        return false
    }
}