package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class CsChapterItem(
    var csChapterCode: String? = null,

    var csItem: List<CsItemItem>? = null,

    var csChapterName: String? = null,

    var csChapterRank: String? = null
) : Parcelable