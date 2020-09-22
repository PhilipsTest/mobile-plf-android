package com.philips.cdp.prxclient.datamodels.summary

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Description :
 * Project : PRX Common Component.
 */

@Parcelize
data class ReviewStatistics(var averageOverallRating: Double = 0.0, var totalReviewCount: Long = 0) : Parcelable