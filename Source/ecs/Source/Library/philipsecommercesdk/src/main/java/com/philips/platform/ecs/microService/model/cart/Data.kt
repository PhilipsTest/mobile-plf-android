package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    val attributes: Attributes?,
    val id: String?,
    val type: String?
) : Parcelable