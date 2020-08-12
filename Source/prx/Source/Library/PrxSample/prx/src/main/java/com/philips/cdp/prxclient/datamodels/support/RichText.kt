package com.philips.cdp.prxclient.datamodels.support

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

/**
 * Rich Text Class.
 */
@Parcelize
data class RichText (var type: String? = null, var chapter: @RawValue Chapter? = null, var item: List<Item> = ArrayList()):Parcelable