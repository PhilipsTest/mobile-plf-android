package com.philips.platform.ecs.microService.model.cart

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliveryCost(
    val currency: String,
    val formattedValue: String,
    val value: Double
): Parcelable {


}