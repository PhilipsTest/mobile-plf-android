package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Filters : Serializable {
    @SerializedName("purpose")
    @Expose
    var purpose: List<PurposeItem>? = null

    companion object {
        private const val serialVersionUID = 2540428616162907708L
    }
}