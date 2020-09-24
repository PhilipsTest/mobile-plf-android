package com.philips.cdp.prxclient.datamodels.summary

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Description :
 * Project : PRX Common Component.
 */
@Parcelize
data class Brand(val partnerBrandType: String?, val partnerLogo: String?, val brandLogo: String?) : Parcelable