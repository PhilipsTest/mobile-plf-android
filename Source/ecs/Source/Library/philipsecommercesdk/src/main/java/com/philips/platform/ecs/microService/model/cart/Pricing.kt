package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pricing(
    val delivery: Delivery,
    val itemDiscount: ItemDiscount,
    val net: Boolean,
    val orderDiscount: OrderDiscount,
    val orderDiscountNoDelivery: OrderDiscountNoDelivery,
    val subTotal: SubTotal,
    val tax: Tax,
    val total: Total,
    val totalDelivery: TotalDelivery,
    val totalDiscountNoDelivery: TotalDiscountNoDelivery,
    val totalNoDelivery: TotalNoDelivery
): Parcelable