package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CsValueItem : Serializable {
    @SerializedName("csValueRank")
    @Expose
    var csValueRank: String? = null

    @SerializedName("csValueName")
    @Expose
    var csValueName: String? = null

    @SerializedName("csValueCode")
    @Expose
    var csValueCode: String? = null

    companion object {
        private const val serialVersionUID = -8911022862913057105L
    }
}