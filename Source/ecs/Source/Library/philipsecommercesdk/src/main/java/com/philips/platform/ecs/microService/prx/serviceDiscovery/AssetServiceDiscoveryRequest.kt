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

class AssetServiceDiscoveryRequest(val ctn: String) : ServiceDiscoveryRequest {

    override fun getServiceID(): String {
        return "prxclient.assets"
    }

    override fun getReplaceURL(): MutableMap<String, String> {
        val replaceURL = super.getReplaceURL()
        replaceURL["ctn"] = ctn
        return replaceURL
    }
}
