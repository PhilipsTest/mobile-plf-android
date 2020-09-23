package com.philips.cdp.prxclient.datamodels.features

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CodeItem(
        var extent: String? = null, var number: String? = null, var extension: String? = null,
        var code: String? = null, var description: String? = null, var lastModified: String? = null,
        var locale: String? = null, var type: String? = null, var asset: String? = null
) : Parcelable