package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CsItems(var csItem: List<CsItemItem>? = null) : Parcelable