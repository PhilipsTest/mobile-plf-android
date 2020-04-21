/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.profile

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.address.ECSUserProfile
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSUserProfileCallBack(private var ecsProfileViewModel: ProfileViewModel) : com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.address.ECSUserProfile, Exception> {

    lateinit var mECRequestType : MECRequestType
    override fun onResponse(userProfile: com.philips.platform.ecs.model.address.ECSUserProfile) {
        ecsProfileViewModel.userProfile.value = userProfile
    }

    override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {

        if (MECutility.isAuthError(ecsError)) {
            ecsProfileViewModel.retryAPI(mECRequestType)
        }else{
            val mecError = MecError(error, ecsError,mECRequestType)
            ecsProfileViewModel.mecError.value = mecError
        }
    }
}