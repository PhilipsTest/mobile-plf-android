package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import com.philips.platform.ecs.microService.model.common.Price
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pricing(
        val delivery: Price?,
        val itemDiscount: Price?,
        val net: Boolean?,
        val orderDiscount: Price?,
        val orderDiscountNoDelivery: Price?,
        val subTotal: Price?,
        val tax: Price?,
        val total: Price?,
        val totalDelivery: Price?,
        val totalDiscountNoDelivery: Price?,
        val totalNoDelivery: Price?
): Parcelable