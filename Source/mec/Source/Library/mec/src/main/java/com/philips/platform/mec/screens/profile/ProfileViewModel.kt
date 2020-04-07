/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.profile


import androidx.lifecycle.MutableLiveData
import com.philips.cdp.di.ecs.model.address.ECSUserProfile
import com.philips.platform.mec.common.CommonViewModel
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder

class ProfileViewModel : com.philips.platform.mec.common.CommonViewModel() {


    val userProfile = MutableLiveData<ECSUserProfile>()

    private var ecsUserProfileCallBack = ECSUserProfileCallBack(this)

    var ecsServices = MECDataHolder.INSTANCE.eCSServices

    var profileRepository = ProfileRepository(ecsServices)

    fun fetchUserProfile(){
        profileRepository.fetchUserProfile(ecsUserProfileCallBack)
    }

    fun retryAPI(mecRequestType: MECRequestType) {
        var retryAPI = selectAPIcall(mecRequestType)
        authAndCallAPIagain(retryAPI, authFailCallback)
    }

    fun selectAPIcall(mecRequestType: MECRequestType): () -> Unit {

        lateinit var APIcall: () -> Unit
        when (mecRequestType) {
            MECRequestType.MEC_FETCH_USER_PROFILE -> APIcall = { fetchUserProfile() }
        }
        return APIcall
    }


}