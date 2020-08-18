package com.philips.cdp.prxclient.datamodels.assets

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
@Parcelize
data class Asset(@SerializedName("code")var code: String?,var  description: String?,var  extension: String?,var  extent: String?,
                 var lastModified: String?,var  locale: String?,var  number: String?,var  type: String?,var  asset: String?): Parcelable


