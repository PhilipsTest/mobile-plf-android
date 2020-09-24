package com.philips.cdp.prxclient.datamodels.summary

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Description :
 * Project : PRX Common Component.
 */
@Parcelize
data class ReviewStatistics(val averageOverallRating: Double, val totalReviewCount: Int) : Parcelable