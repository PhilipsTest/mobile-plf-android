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
package com.philips.platform.ecs.microService.model.config

import java.io.Serializable

/**
 * The type Ecs config which contains philips e-commerce configuration data. This object is returned when configureECS is called.
 */
class ECSConfig : Serializable {

    var locale: String? = null
    val catalogId: String? = null
    val faqUrl: String? = null
    val helpDeskEmail: String? = null
    val helpDeskPhone: String? = null
    val helpUrl: String? = null
    val rootCategory: String? = null
    val siteId: String? = null
    var isHybris = false

}