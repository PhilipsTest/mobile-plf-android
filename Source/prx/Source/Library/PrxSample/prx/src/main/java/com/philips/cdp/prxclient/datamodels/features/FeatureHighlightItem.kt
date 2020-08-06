package com.philips.cdp.prxclient.datamodels.features

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FeatureHighlightItem : Serializable {
    @SerializedName("featureCode")
    var featureCode: String? = null

    @SerializedName("featureHighlightRank")
    var featureHighlightRank: String? = null

    @SerializedName("featureReferenceName")
    var featureReferenceName: String? = null

    companion object {
        private const val serialVersionUID = 4150127431517255657L
    }
}