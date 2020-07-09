package com.philips.platform.ccb.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CCBMessage(var ccbActivities: List<CCBActivities>?) : Parcelable {
}