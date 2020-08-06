package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CsItems : Serializable {
    @SerializedName("csItem")
    @Expose
    var csItem: List<CsItemItem>? = null

    companion object {
        private const val serialVersionUID = -2107030004235630968L
    }
}