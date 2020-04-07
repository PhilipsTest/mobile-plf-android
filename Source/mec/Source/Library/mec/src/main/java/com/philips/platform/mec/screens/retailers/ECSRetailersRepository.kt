/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.retailers

import com.philips.cdp.di.ecs.ECSServices

class ECSRetailersRepository(private val ecsServices: ECSServices,private val ecsRetailerViewModel: ECSRetailerViewModel) {

    var eCSRetailerListCallback = ECSRetailerListCallback(ecsRetailerViewModel)

    fun getRetailers(ctn: String) {
        ecsServices.fetchRetailers(ctn, eCSRetailerListCallback)
    }

}