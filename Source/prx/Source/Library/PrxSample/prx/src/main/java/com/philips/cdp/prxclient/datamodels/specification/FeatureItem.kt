package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FeatureItem : Serializable {
    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("rank")
    @Expose
    var rank: String? = null

    @SerializedName("referenceName")
    @Expose
    var referenceName: String? = null

    companion object {
        private const val serialVersionUID = -4070255334532209384L
    }
}