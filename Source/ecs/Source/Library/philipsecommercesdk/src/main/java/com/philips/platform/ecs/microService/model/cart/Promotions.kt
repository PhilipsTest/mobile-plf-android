package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Promotions(
        val appliedProductPromotions: List<ProductPromotion>,
        val appliedPromotions: List<Promotion>,
        val potentialProductPromotions: List<ProductPromotion>,
        val potentialPromotions: List<Promotion>
): Parcelable
