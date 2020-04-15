/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address.region

import androidx.lifecycle.MutableLiveData
import com.philips.cdp.di.ecs.model.region.ECSRegion
import com.philips.platform.mec.common.CommonViewModel
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder

class RegionViewModel : com.philips.platform.mec.common.CommonViewModel() {

    private var ecsRegionListCallback = ECSRegionListCallback(this)

    var regionsList = MutableLiveData<List<ECSRegion>>()

    var ecsServices = MECDataHolder.INSTANCE.eCSServices
    var regionRepository = RegionRepository(ecsServices)



    fun fetchRegions() {
        regionRepository.getRegions(ecsRegionListCallback)
    }

    fun retryAPI(mecRequestType: MECRequestType) {
        var retryAPI = selectAPIcall(mecRequestType)
        authAndCallAPIagain(retryAPI,authFailCallback)
    }

    fun selectAPIcall(mecRequestType: MECRequestType):() -> Unit{

        lateinit  var APIcall: () -> Unit
        when(mecRequestType) {
            MECRequestType.MEC_FETCH_REGIONS                     -> APIcall = { fetchRegions() }
        }
        return APIcall
    }


}