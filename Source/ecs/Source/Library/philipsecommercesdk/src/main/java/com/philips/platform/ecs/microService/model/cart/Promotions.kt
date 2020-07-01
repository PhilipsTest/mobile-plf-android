package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Promotions(
        val appliedProductPromotions: List<ProductPromotion>?,
        val potentialProductPromotions: List<ProductPromotion>?,
        val potentialPromotions: List<Promotion>?,
        val appliedPromotions: List<Promotion>?,
        val productPromotions: List<ProductPromotion> //This comes with product
): Parcelable
