package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.philips.platform.ecs.microService.model.common.Availability
import com.philips.platform.ecs.microService.model.common.Price
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Item(
        val availability: Availability?,
        val discountPrice: Price?,
        @SerializedName("id") var ctn: String?,
        val image: String?,
        @SerializedName("itemId") val entryNumber: String?,
        val price: Price?,
        val quantity: Int?,
        val title: String?,
        val totalPrice: Price?
): Parcelable