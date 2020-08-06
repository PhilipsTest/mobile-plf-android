package com.philips.cdp.prxclient.datamodels.summary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
class ReviewStatistics {
    /**
     *
     * @return
     * The averageOverallRating
     */
    /**
     *
     * @param averageOverallRating
     * The averageOverallRating
     */
    @SerializedName("averageOverallRating")
    @Expose
    var averageOverallRating = 0.0

    /**
     *
     * @return
     * The totalReviewCount
     */
    /**
     *
     * @param totalReviewCount
     * The totalReviewCount
     */
    @SerializedName("totalReviewCount")
    @Expose
    var totalReviewCount: Long = 0

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param averageOverallRating
     * @param totalReviewCount
     */
    constructor(averageOverallRating: Double, totalReviewCount: Long) {
        this.averageOverallRating = averageOverallRating
        this.totalReviewCount = totalReviewCount
    }

}