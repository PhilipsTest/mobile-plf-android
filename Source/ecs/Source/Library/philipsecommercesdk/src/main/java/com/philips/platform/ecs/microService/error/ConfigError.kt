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
package com.philips.platform.ecs.microService.error

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConfigError(
    val message: String?,
    val reason: String?,
    val subject: String?,
    val subjectType: String?,
    val type: String?
) : Parcelable