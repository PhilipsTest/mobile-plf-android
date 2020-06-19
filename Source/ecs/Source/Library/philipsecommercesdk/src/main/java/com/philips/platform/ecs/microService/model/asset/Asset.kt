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
package com.philips.platform.ecs.microService.model.asset

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Asset (
    var code: String? ,
    var description: String?,
    var extension: String?,
    var extent: String?,
    var lastModified: String,
    var locale: String?,
    var number: String?,
    var type: String?,
    var asset: String?
):Parcelable

