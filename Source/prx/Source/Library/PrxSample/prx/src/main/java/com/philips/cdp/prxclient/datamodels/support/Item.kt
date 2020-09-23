package com.philips.cdp.prxclient.datamodels.support

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Item model class.
 */
@Parcelize
data class Item(var head: String? = null, var code: String? = null, var rank: String? = null, var lang: String? = null, var asset: String? = null) : Parcelable