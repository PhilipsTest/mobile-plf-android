package com.philips.platform.ccb.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class CCBActions(
        val type: String?,
        val title: String,
        val value: String
)
