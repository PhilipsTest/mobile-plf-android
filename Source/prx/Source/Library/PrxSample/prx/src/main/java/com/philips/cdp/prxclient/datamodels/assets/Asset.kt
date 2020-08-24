package com.philips.cdp.prxclient.datamodels.assets

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Description :
 * Project : PRX Common Component.
 */
@Parcelize
data class Asset(var code: String?,var  description: String?,var  extension: String?,var  extent: String?,
                 var lastModified: String?,var  locale: String?,var  number: String?,var  type: String?,var  asset: String?): Parcelable


