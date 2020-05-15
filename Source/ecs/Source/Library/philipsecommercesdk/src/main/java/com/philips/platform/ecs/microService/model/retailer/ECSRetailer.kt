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

package com.philips.platform.ecs.microService.model.retailer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ECSRetailer(
    val availability: String?,
    val buyURL: String?,
    val isPhilipsStore: String?,
    val logoHeight: Int?,
    val logoURL: String?,
    val logoWidth: Int?,
    val name: String?,
    val philipsOnlinePrice: String?,
    val storeType: String?,
    val xactparam: String?
):Parcelable