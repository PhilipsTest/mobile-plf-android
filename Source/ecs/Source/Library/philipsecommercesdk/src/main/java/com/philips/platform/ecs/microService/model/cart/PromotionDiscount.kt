package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PromotionDiscount (val currencyIso: String?,val formattedValue: String?, val priceType: String?,val value: Double?): Parcelable
