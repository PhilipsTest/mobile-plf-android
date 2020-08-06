package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CsChapterItem : Serializable {
    @SerializedName("csChapterCode")
    @Expose
    var csChapterCode: String? = null

    @kotlin.jvm.JvmField
    @SerializedName("csItem")
    @Expose
    var csItem: List<CsItemItem>? = null

    @kotlin.jvm.JvmField
    @SerializedName("csChapterName")
    @Expose
    var csChapterName: String? = null

    @SerializedName("csChapterRank")
    @Expose
    var csChapterRank: String? = null

    companion object {
        private const val serialVersionUID = 5009770779919717578L
    }
}