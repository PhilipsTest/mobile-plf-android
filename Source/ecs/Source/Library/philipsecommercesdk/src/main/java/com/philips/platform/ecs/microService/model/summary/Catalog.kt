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

package com.philips.platform.ecs.microService.model.summary

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Catalog(
    val catalogId: String?,
    val clearance: Boolean,
    val eop: String?,
    val isDeleted: Boolean,
    val price: List<Price>?,
    val priority: Int,
    val rank: Int,
    val somp: String?,
    val sop: String?,
    val status: String?,
    val visibility: Boolean
):Parcelable