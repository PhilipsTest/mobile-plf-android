package com.philips.cdp.prxclient.datamodels.features

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FeatureItem : Serializable {
    @kotlin.jvm.JvmField
    @SerializedName("featureLongDescription")
    var featureLongDescription: String? = null

    @SerializedName("featureCode")
    var featureCode: String? = null

    @SerializedName("featureRank")
    var featureRank: String? = null

    @SerializedName("featureName")
    var featureName: String? = null

    @kotlin.jvm.JvmField
    @SerializedName("featureGlossary")
    var featureGlossary: String? = null

    @SerializedName("featureReferenceName")
    var featureReferenceName: String? = null

    @SerializedName("featureTopRank")
    var featureTopRank: String? = null

    var singleFeatureImage: String? = null

    companion object {
        private const val serialVersionUID = 6716648931315785132L
    }
}