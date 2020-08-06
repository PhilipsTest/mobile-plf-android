package com.philips.cdp.prxclient.datamodels.features

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class KeyBenefitAreaItem : Serializable {
    @kotlin.jvm.JvmField
    @SerializedName("feature")
    var feature: List<FeatureItem>? = null

    @SerializedName("keyBenefitAreaCode")
    var keyBenefitAreaCode: String? = null

    @kotlin.jvm.JvmField
    @SerializedName("keyBenefitAreaName")
    var keyBenefitAreaName: String? = null

    @SerializedName("keyBenefitAreaRank")
    var keyBenefitAreaRank: String? = null

    companion object {
        private const val serialVersionUID = -7309764568406054211L
    }
}