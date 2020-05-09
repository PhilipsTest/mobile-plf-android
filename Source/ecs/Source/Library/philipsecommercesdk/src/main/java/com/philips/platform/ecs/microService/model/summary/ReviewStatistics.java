/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */
package com.philips.platform.ecs.microService.model.summary;

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ReviewStatistics implements Serializable {

    @SerializedName("averageOverallRating")
    @Expose
    private double averageOverallRating;
    @SerializedName("totalReviewCount")
    @Expose
    private long totalReviewCount;

    /**
     * No args constructor for use in serialization
     *
     */
    public ReviewStatistics() {
    }

    /**
     *
     * @param averageOverallRating
     * @param totalReviewCount
     */
    public ReviewStatistics(double averageOverallRating, long totalReviewCount) {
        this.averageOverallRating = averageOverallRating;
        this.totalReviewCount = totalReviewCount;
    }

    /**
     *
     * @return
     * The averageOverallRating
     */
    public double getAverageOverallRating() {
        return averageOverallRating;
    }

    /**
     *
     * @param averageOverallRating
     * The averageOverallRating
     */
    public void setAverageOverallRating(double averageOverallRating) {
        this.averageOverallRating = averageOverallRating;
    }

    /**
     *
     * @return
     * The totalReviewCount
     */
    public long getTotalReviewCount() {
        return totalReviewCount;
    }

    /**
     *
     * @param totalReviewCount
     * The totalReviewCount
     */
    public void setTotalReviewCount(long totalReviewCount) {
        this.totalReviewCount = totalReviewCount;
    }

}