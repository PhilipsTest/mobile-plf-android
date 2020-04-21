/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.retailers

import com.philips.platform.ecs.ECSServices

class ECSRetailersRepository(private val ecsServices: com.philips.platform.ecs.ECSServices, private val ecsRetailerViewModel: ECSRetailerViewModel) {

    var eCSRetailerListCallback = ECSRetailerListCallback(ecsRetailerViewModel)

    fun getRetailers(ctn: String) {
        ecsServices.fetchRetailers(ctn, eCSRetailerListCallback)
    }

}