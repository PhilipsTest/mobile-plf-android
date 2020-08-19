package com.philips.platform.ccb.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class CCBMessage(
        var activities: List<CCBActivities>?,
        val watermark: String?
)
