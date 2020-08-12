package com.philips.cdp.prxclient.datamodels.features

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class CodeItem (
    var extent: String? = null, var number: String? = null, var extension: String? = null ,
    var code: String? = null,  var description: String? = null,var lastModified: String? = null,
    var locale: String? = null, var type: String? = null, @SerializedName("asset")
    var asset: String? = null
    ):Parcelable