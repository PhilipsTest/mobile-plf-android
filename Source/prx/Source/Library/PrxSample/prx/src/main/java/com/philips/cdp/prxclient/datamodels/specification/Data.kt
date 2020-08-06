package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Data : Serializable {
    @SerializedName("filters")
    @Expose
    var filters: Filters? = null

    @SerializedName("csChapter")
    @Expose
    var csChapter: List<CsChapterItem>? = null

    companion object {
        private const val serialVersionUID = 8526357496790701367L
    }
}