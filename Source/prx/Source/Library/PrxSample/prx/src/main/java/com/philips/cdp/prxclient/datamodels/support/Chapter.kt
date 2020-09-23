package com.philips.cdp.prxclient.datamodels.support

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chapter(var name: String? = null, var code: String? = null, var rank: String? = null, var referenceName: String? = null) : Parcelable