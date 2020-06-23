package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val availability: Availability,
    val discountPrice: DiscountPrice,
    val id: String,
    val image: String,
    val itemId: String,
    val price: Price,
    val quantity: Int,
    val title: String,
    val totalPrice: TotalPrice
): Parcelable