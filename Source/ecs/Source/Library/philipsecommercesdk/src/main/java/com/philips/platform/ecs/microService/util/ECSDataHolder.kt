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

object ECSDataHolder {

    var baseURL: String? = null
    var locale: String? = null
    lateinit var appInfra : AppInfra

    private val configError = AppConfigurationInterface.AppConfigurationError()
    val propositionId = appInfra.configInterface.getPropertyForKey("propositionid", "MEC", configError)
}