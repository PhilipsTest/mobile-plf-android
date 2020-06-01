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

package com.philips.platform.ecs.microService.util

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.model.oauth.ECSOAuthData

object ECSDataHolder {



    var baseURL: String? = null
    var locale: String? = null
    var lang = locale?.split("_")?.get(0)
    var country = locale?.split("_")?.get(1)
    var appInfra : AppInfra? = null
    lateinit var loggingInterface: LoggingInterface

    var urlMap: MutableMap<String, ServiceDiscoveryService>? = null

    private val configError = AppConfigurationInterface.AppConfigurationError()


    var config = ECSConfig(locale,null,null,null,null,null,null,null,false)

    var eCSOAuthData : ECSOAuthData?=null

    fun getPropositionId():String?{
        try {
           return appInfra?.configInterface?.getPropertyForKey("propositionid", "MEC", configError) as String
        }catch (e :Exception){
           return null
        }
    }
}