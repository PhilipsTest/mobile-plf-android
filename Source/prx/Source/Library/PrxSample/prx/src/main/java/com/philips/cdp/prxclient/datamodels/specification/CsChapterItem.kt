package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CsChapterItem(
        var csChapterCode: String? = null,
        var csItem: List<CsItemItem>? = null,
        var csChapterName: String? = null,
        var csChapterRank: String? = null
) : Parcelable