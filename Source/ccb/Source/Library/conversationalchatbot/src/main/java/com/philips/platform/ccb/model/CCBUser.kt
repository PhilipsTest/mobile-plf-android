package com.philips.platform.ccb.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CCBUser(var secretKey: String?, val name: String?, val userID: String?) : Parcelable {
}