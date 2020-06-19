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
data class Price(
    val currencyCode: String?,
    val displayPrice: String?,
    val displayPriceType: String?,
    val formattedDisplayPrice: String?,
    val formattedPrice: String?,
    val productPrice: String?
):Parcelable