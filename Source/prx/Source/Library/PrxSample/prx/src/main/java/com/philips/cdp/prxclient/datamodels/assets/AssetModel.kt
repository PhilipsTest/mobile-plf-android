package com.philips.cdp.prxclient.datamodels.assets

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Description :
 * Project : PRX Common Component.
 */
@Parcelize
data class AssetModel(@SerializedName("success") var isSuccess: Boolean? = null, var data: Data? = null) : Parcelable