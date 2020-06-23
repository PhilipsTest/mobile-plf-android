package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Promotions(
        val appliedProductPromotions: List<@RawValue Any>,
        val appliedPromotions: List< @RawValue Any>,
        val potentialProductPromotions: List<@RawValue Any>,
        val potentialPromotions: List<@RawValue Any>
): Parcelable
