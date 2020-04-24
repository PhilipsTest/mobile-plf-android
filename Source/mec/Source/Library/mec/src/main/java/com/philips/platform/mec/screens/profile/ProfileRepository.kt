/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.profile

import com.philips.platform.ecs.ECSServices

class ProfileRepository(var ecsServices:ECSServices) {


    fun fetchUserProfile(ecsUserProfileCallBack: ECSUserProfileCallBack) {
        ecsServices.fetchUserProfile(ecsUserProfileCallBack)
    }
}