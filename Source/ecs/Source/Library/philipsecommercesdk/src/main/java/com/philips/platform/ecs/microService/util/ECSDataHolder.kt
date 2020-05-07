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
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.model.oauth.ECSOAuthData
import java.lang.Exception

object ECSDataHolder {

    fun getURLMapper(config: String): String? {
        return urlMAp[config]
    }

    var baseURL: String? = null
    var locale: String? = null
    var appInfra : AppInfra? = null

    private val configError = AppConfigurationInterface.AppConfigurationError()


    lateinit var urlMAp : HashMap<String,String>

    var config = ECSConfig()

    var eCSOAuthData : ECSOAuthData?=null

    fun getPropositionId():String?{
        try {
           return appInfra?.configInterface?.getPropertyForKey("propositionid", "MEC", configError) as String
        }catch (e :Exception){
           return null
        }
    }
}