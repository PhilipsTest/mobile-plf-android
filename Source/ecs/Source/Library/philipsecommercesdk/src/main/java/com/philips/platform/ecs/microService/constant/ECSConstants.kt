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

        //Service IDS
        const val SERVICEID_IAP_BASEURL = "iap.baseurl"
        const val SERVICEID_PRX_ASSETS = "prxclient.assets"
        const val SERVICEID_PRX_DISCLAIMERS= "prxclient.disclaimers"
        const val SERVICEID_PRX_SUMMARY_LIST = "prxclient.summarylist"
        const val SERVICEID_ECS_PRODUCT_DETAILS = "ecs.productDetails"
    }

    fun getListOfServiceID() : ArrayList<String> {
        val listOFServiceID = mutableListOf<String>()
        listOFServiceID.add(SERVICEID_IAP_BASEURL)
        listOFServiceID.add(SERVICEID_PRX_ASSETS)
        listOFServiceID.add(SERVICEID_PRX_DISCLAIMERS)
        listOFServiceID.add(SERVICEID_PRX_SUMMARY_LIST)
        return listOFServiceID as ArrayList<String>
    }
}