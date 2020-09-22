package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeatureItem(var code: String? = null, var rank: String? = null, var referenceName: String? = null) : Parcelable