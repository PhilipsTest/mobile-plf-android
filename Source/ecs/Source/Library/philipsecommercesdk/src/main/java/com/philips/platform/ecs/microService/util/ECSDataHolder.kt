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
import com.philips.platform.ecs.util.ECSConfiguration

object ECSDataHolder {

    var locale: String? = null
    var lang = locale?.split("_")?.get(0)
    var country = locale?.split("_")?.get(1)
    var appInfra : AppInfra? = null
    var loggingInterface: LoggingInterface? = null

    var urlMap: MutableMap<String, ServiceDiscoveryService>? = null

    //TODO to be removed later , for now we are using occ accesstoken
    var authToken: String? = ECSConfiguration.INSTANCE.accessToken

    private val configError = AppConfigurationInterface.AppConfigurationError()


    var config = ECSConfig("en_US",null,null,null,null,null,null,null,false)


    fun getPropositionId():String?{
        return try {
            appInfra?.configInterface?.getPropertyForKey("propositionid", "MEC", configError) as String
        }catch (e :Exception){
            null
        }
    }

    fun getAPIKey():String?{
        return try {
            appInfra?.configInterface?.getPropertyForKey("PIL_ECommerce_API_KEY", "MEC", configError) as String
        }catch (e :Exception){
            null
        }
    }
}