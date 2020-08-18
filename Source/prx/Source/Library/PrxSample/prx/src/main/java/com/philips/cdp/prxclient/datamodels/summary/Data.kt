package com.philips.cdp.prxclient.datamodels.summary

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
@Parcelize
 data class Data(var locale: String? = null, var ctn: String? = null, var dtn: String? = null, var leafletUrl: String? = null,
                var productTitle: String? = null, var alphanumeric: String? = null, var brandName: String? = null,
                var brand: @RawValue Brand? = null, var familyName: String? = null, var productURL: String? = null,
                var productPagePath: String? = null, var descriptor: String? = null, var domain: String? = null,
                var versions: List<String> = ArrayList(), var productStatus: String? = null, var imageURL: String? = null,
                var sop: String? = null, var somp: String? = null, var eop: String? = null, var isIsDeleted: Boolean = false,
                var priority: Long = 0, var price: Price? = null, var reviewStatistics: ReviewStatistics? = null,
                var keyAwards: List<String> = ArrayList(), var wow: String? = null, var subWOW: String? = null,
                var marketingTextHeader: String? = null, var careSop: String? = null, var filterKeys: List<String> = ArrayList(),
                var subcategory: String? = null,var additionalProperties :@RawValue Map<String, Object>?=null
) : Parcelable