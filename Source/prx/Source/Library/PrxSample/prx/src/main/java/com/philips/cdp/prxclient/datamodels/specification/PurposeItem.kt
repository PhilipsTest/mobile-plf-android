package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PurposeItem(var features: Features? = null, var csItems: CsItems? = null, var type: String? = null) : Parcelable