package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Attributes(
        val deliveryMode: DeliveryMode,
        val deliveryUnits: Int,
        val email: String,
        val items: List<Item>,
        val notifications: List<@RawValue Any>,
        val pricing: Pricing,
        val promotions: Promotions,
        val units: Int
) : Parcelable