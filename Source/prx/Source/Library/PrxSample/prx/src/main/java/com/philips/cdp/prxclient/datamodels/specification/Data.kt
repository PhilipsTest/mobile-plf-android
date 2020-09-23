package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(var filters: Filters? = null, var csChapter: List<CsChapterItem>? = null) : Parcelable