package com.philips.cdp.prxclient.datamodels.support

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Data Model class.
 */
@Parcelize
data class Data(
    var richTexts: @RawValue RichTexts? = null
):Parcelable