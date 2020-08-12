package com.philips.cdp.prxclient.datamodels.summary

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
data class Price (var productPrice: String? = null, var displayPriceType: String? = null ,var displayPrice: String? = null,
                  var currencyCode: String? = null, var formattedPrice: String? = null, var formattedDisplayPrice: String? = null ):Parcelable