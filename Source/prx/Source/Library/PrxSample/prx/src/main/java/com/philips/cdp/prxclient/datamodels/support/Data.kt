package com.philips.cdp.prxclient.datamodels.support

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Data Model class.
 */
@Parcelize
data class Data(var richTexts: @RawValue RichTexts? = null) : Parcelable