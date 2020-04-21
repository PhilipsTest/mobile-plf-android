/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.profile


import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.model.address.ECSUserProfile
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder

class ProfileViewModel : com.philips.platform.mec.common.CommonViewModel() {


    val userProfile = MutableLiveData<com.philips.platform.ecs.model.address.ECSUserProfile>()

    private var ecsUserProfileCallBack = ECSUserProfileCallBack(this)

    var ecsServices = MECDataHolder.INSTANCE.eCSServices

    var profileRepository = ProfileRepository(ecsServices)

    fun fetchUserProfile(){
        profileRepository.fetchUserProfile(ecsUserProfileCallBack)
    }

    fun retryAPI(mecRequestType: MECRequestType) {
        val retryAPI = selectAPIcall(mecRequestType)
        authAndCallAPIagain(retryAPI, authFailCallback)
    }

    fun selectAPIcall(mecRequestType: MECRequestType): () -> Unit {

        lateinit var APIcall: () -> Unit
        if (mecRequestType == MECRequestType.MEC_FETCH_USER_PROFILE) APIcall = { fetchUserProfile() }
        return APIcall
    }


}