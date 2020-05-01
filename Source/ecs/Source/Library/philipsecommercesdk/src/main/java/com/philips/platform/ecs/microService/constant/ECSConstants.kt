/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.ecs.microService.constant

import java.util.*

class ECSConstants {

    companion object {

        const val CONFIG = "config"
    }

    fun getListOfServiceID() : ArrayList<String> {
        val SERVICE_ID = "iap.baseurl"
        val listOFServiceID = mutableListOf<String>()
        listOFServiceID.add(SERVICE_ID)
        return listOFServiceID as ArrayList<String>
    }
}