package com.philips.cdp.prxclient.datamodels.features

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeatureHighlightItem(
        var featureCode: String? = null, var featureHighlightRank: String? = null, var featureReferenceName: String? = null) : Parcelable