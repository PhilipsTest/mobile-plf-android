package com.philips.platform.ccb.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CCBActivities(val text: String?, val inputHint: String?) : Parcelable {
}