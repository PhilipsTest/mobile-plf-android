package com.philips.cdp.prxclient.datamodels.summary

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

/**
 * Description :
 * Project : PRX Common Component.
 */

@Parcelize
data class Price(var productPrice: String? = null, var displayPriceType: String? = null, var displayPrice: String? = null,
                 var currencyCode: String? = null, var formattedPrice: String? = null, var formattedDisplayPrice: String? = null,
                 var additionalProperties: @RawValue Map<String, Any> = HashMap()) : Parcelable