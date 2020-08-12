package com.philips.cdp.prxclient.datamodels.Disclaimer

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Disclaimer (
    var disclaimerText: String? = null,
    var code: String? = null,
    var rank: String? = null, var referenceName: String? = null
    ):Parcelable