package com.philips.cdp.di.mec.screens.address.region

import androidx.lifecycle.MutableLiveData
import com.philips.cdp.di.ecs.model.region.ECSRegion
import com.philips.cdp.di.mec.common.CommonViewModel
import com.philips.cdp.di.mec.common.MECRequestType
import com.philips.cdp.di.mec.utils.MECDataHolder

class RegionViewModel : CommonViewModel() {

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