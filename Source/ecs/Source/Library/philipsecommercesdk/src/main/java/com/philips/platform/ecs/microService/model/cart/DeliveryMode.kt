package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliveryMode(
    val collectionPoint: Boolean,
    val deliveryCost: DeliveryCost,
    val description: String,
    val id: String,
    val label: String
): Parcelable