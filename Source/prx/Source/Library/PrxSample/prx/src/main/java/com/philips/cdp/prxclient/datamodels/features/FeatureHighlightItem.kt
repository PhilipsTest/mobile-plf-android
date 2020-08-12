package com.philips.cdp.prxclient.datamodels.features

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class FeatureHighlightItem  (
    var featureCode: String? = null, var featureHighlightRank: String? = null, var featureReferenceName: String? = null):Parcelable