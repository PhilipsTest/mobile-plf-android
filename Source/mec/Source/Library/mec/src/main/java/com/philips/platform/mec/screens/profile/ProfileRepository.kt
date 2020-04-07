/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.profile

import com.philips.cdp.di.ecs.ECSServices

class ProfileRepository(val ecsServices: ECSServices) {


    fun fetchUserProfile(ecsUserProfileCallBack: ECSUserProfileCallBack) {
        ecsServices.fetchUserProfile(ecsUserProfileCallBack)
    }
}