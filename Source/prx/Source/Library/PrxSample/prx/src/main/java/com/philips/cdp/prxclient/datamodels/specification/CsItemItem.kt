package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CsItemItem : Serializable {
    @SerializedName("csItemRank")
    @Expose
    var csItemRank: String? = null

    @SerializedName("unitOfMeasure")
    @Expose
    var unitOfMeasure: UnitOfMeasure? = null

    @kotlin.jvm.JvmField
    @SerializedName("csItemName")
    @Expose
    var csItemName: String? = null

    @SerializedName("csItemCode")
    @Expose
    var csItemCode: String? = null

    @SerializedName("csItemIsFreeFormat")
    @Expose
    var csItemIsFreeFormat: String? = null

    @SerializedName("csValue")
    @Expose
    var csValue: List<CsValueItem>? = null

    companion object {
        private const val serialVersionUID = -6883419441341473202L
    }
}