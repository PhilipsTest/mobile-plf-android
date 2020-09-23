package com.philips.cdp.prxclient.datamodels.features

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KeyBenefitAreaItem(var feature: List<FeatureItem>? = null, var keyBenefitAreaCode: String? = null, var keyBenefitAreaName: String? = null, var keyBenefitAreaRank: String? = null) : Parcelable