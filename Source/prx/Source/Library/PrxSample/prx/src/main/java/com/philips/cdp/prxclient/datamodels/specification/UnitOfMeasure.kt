package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UnitOfMeasure : Serializable {
    @SerializedName("unitOfMeasureSymbol")
    @Expose
    var unitOfMeasureSymbol: String? = null

    @SerializedName("unitOfMeasureName")
    @Expose
    var unitOfMeasureName: String? = null

    @SerializedName("unitOfMeasureCode")
    @Expose
    var unitOfMeasureCode: String? = null

    companion object {
        private const val serialVersionUID = 8843113320809077214L
    }
}