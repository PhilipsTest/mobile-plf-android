package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.philips.platform.ecs.microService.model.common.Availability
import com.philips.platform.ecs.microService.model.common.Price
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ECSItem(
        val availability: Availability?,
        val discountPrice: Price?,
        @SerializedName("id") val entryNumber: String?,
        val image: String?,
        @SerializedName("itemId") val ctn: String?,
        val price: Price?,
        val quantity: Int?,
        val title: String?,
        val totalPrice: Price?
): Parcelable