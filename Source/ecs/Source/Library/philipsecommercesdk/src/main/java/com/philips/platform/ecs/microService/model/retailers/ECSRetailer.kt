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
package com.philips.platform.ecs.microService.model.retailers

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ECSRetailer (
     val name: String?,
     val availability: String?,
     val isPhilipsStore: String?,
     val philipsOnlinePrice: String?,
     val logoHeight:Int = 0,
     val logoWidth:Int = 0,
     val buyURL: String?,
     val logoURL: String?,
     val xactparam: String? = null
):Parcelable