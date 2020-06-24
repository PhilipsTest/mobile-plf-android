package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ECSPILShoppingCart (
    val `data`: Data
) : Parcelable