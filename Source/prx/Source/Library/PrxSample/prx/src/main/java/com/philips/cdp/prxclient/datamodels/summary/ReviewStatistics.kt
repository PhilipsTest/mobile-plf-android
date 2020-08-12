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
data class ReviewStatistics (var averageOverallRating: Double = 0.0, var totalReviewCount: Long = 0): Parcelable