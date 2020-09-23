package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CsValueItem(var csValueRank: String? = null, var csValueName: String? = null, var csValueCode: String? = null) : Parcelable