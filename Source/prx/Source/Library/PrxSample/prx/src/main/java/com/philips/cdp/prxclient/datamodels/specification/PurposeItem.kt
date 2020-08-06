package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PurposeItem : Serializable {
    @SerializedName("features")
    @Expose
    var features: Features? = null

    @SerializedName("csItems")
    @Expose
    var csItems: CsItems? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    companion object {
        private const val serialVersionUID = 7461674031962600708L
    }
}