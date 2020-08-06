package com.philips.cdp.prxclient.datamodels.features

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CodeItem : Serializable {
    @SerializedName("extent")
    var extent: String? = null

    @SerializedName("number")
    var number: String? = null

    @SerializedName("extension")
    var extension: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("lastModified")
    var lastModified: String? = null

    @SerializedName("locale")
    var locale: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("asset")
    var asset: String? = null

    companion object {
        private const val serialVersionUID = -8425374871117962060L
    }
}