package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.philips.platform.ecs.microService.model.common.Price
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliveryMode(
        val collectionPoint: Boolean?,
        val deliveryCost: Price?,
        val description: String?,
        val id: String?,
        @SerializedName("label") val name: String?
): Parcelable