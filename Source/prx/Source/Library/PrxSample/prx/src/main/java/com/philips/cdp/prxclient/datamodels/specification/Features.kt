package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Features : Serializable {
    @SerializedName("feature")
    @Expose
    var feature: List<FeatureItem>? = null

    companion object {
        private const val serialVersionUID = 4714472613554904424L
    }
}