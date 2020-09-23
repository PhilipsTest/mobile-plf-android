package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Filters(var purpose: List<PurposeItem>? = null) : Parcelable