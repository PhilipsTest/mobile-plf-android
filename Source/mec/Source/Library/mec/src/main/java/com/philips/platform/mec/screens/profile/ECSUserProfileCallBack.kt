/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.profile

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.address.ECSUserProfile
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSUserProfileCallBack(private var ecsProfileViewModel: ProfileViewModel) :ECSCallback<ECSUserProfile, Exception> {

    lateinit var mECRequestType : MECRequestType
    override fun onResponse(userProfile: ECSUserProfile) {
        ecsProfileViewModel.userProfile.value = userProfile
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {

        if (MECutility.isAuthError(ecsError)) {
            ecsProfileViewModel.retryAPI(mECRequestType)
        }else{
            val mecError = MecError(error, ecsError,mECRequestType)
            ecsProfileViewModel.mecError.value = mecError
        }
    }
}