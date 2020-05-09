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
package com.philips.platform.ecs.microService.prx.serviceDiscovery

import android.text.TextUtils

class ProductSummaryListServiceDiscoveryRequest(private val ctns: List<String?>) : ServiceDiscoveryRequest {

    private fun getString(ctns: List<String?>): String {
        return TextUtils.join(",", ctns)
    }

    override fun getServiceID(): String {
        return "prxclient.summarylist"
    }
    override fun getReplaceURL(): MutableMap<String, String> {
        val replaceURL = super.getReplaceURL()
        replaceURL["ctns"] = getString(ctns)
        return replaceURL
    }

}