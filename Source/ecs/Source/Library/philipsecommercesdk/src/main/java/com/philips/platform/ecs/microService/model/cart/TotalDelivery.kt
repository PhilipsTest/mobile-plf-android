package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TotalDelivery(
    val currency: String,
    val formattedValue: String,
    val value: Double
): Parcelable