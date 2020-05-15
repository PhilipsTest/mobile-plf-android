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

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

/**
 * The type Ecs config which contains philips e-commerce configuration data. This object is returned when configureECS is called.
 */
@Parcelize
data class ECSConfig (

    var locale: String?,
    val catalogId: String?,
    val faqUrl: String?,
    val helpDeskEmail: String?,
    val helpDeskPhone: String?,
    val helpUrl: String?,
    val rootCategory: String?,
    val siteId: String?,
    var isHybris :Boolean = false

):Parcelable