/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address.region

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.model.region.ECSRegion
import com.philips.platform.mec.common.CommonViewModel
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder

class RegionViewModel : CommonViewModel() {

    internal var ecsRegionListCallback = ECSRegionListCallback(this)

    internal var regionsList = MutableLiveData<List<ECSRegion>>()

    internal var ecsServices = MECDataHolder.INSTANCE.eCSServices
    internal var regionRepository = RegionRepository()


    fun fetchRegions() {
        regionRepository.getRegions(ecsServices,ecsRegionListCallback)
    }

    fun retryAPI(mecRequestType: MECRequestType) {
        val retryAPI = selectAPIcall(mecRequestType)
        authAndCallAPIagain(retryAPI, authFailCallback)
    }

    private fun selectAPIcall(mecRequestType: MECRequestType): () -> Unit {

        lateinit var APIcall: () -> Unit
        when (mecRequestType) {
            MECRequestType.MEC_FETCH_REGIONS -> APIcall = { fetchRegions() }
        }
        return APIcall
    }


}