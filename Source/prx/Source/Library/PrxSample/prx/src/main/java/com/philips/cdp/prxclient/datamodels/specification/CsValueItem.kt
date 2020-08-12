package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class CsValueItem (var csValueRank: String? = null, var csValueName: String? = null,var csValueCode: String? = null
):Parcelable