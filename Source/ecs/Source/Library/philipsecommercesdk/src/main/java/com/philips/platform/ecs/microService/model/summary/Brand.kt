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
data class Brand(
    val partnerBrandType: String?,
    val partnerLogo: String?,
    val brandLogo:String?
): Parcelable