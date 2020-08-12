package com.philips.cdp.prxclient.datamodels.features

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class FeatureItem (
    var featureLongDescription: String? = null,
    var featureCode: String? = null,
    var featureRank: String? = null,
    var featureName: String? = null,
    var featureGlossary: String? = null,
    var featureReferenceName: String? = null,
    var featureTopRank: String? = null,
    var singleFeatureImage: String? = null
):Parcelable