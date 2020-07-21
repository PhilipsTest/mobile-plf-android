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
package com.philips.platform.mec.screens.catalog

import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.ConversationsDisplayCallback
import com.bazaarvoice.bvandroidsdk.ConversationsException
import com.bazaarvoice.bvandroidsdk.Statistics
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import java.text.DecimalFormat

class PILMECBulkRatingConversationsDisplayCallback(val ecsProducts: List<ECSProduct>, val ecsProductViewModel: EcsProductViewModel) : ConversationsDisplayCallback<BulkRatingsResponse> {


    override fun onSuccess(response: BulkRatingsResponse) {

        if (response.results.isEmpty()) {
            //ecsProductViewModel.mecError = //Util.showMessage(this@DisplayOverallRatingActivity, "Empty results", "No ratings found for this product")
        } else {
            createMECProductReviewObject(ecsProducts,response.results)
        }
    }

    override fun onFailure(exception: ConversationsException) {

        val ecsError = com.philips.platform.ecs.error.ECSError(1000, exception.localizedMessage)
        val mecError = MecError(exception, ecsError, MECRequestType.MEC_FETCH_REVIEW)
        ecsProductViewModel.mecError.value = mecError
    }


    private fun createMECProductReviewObject(ecsProducts: List<ECSProduct>, statisticsList: List<Statistics>) {

        var mecProductReviewList :MutableList<PILMECProductReview> = mutableListOf()

        for(ecsProduct in ecsProducts){

            for(statistics in statisticsList){

                if(ecsProduct.ctn isEqualsTo statistics.productStatistics.productId){

                    mecProductReviewList.add (PILMECProductReview(ecsProduct, DecimalFormat("0.0").format(statistics.productStatistics.reviewStatistics.averageOverallRating), " ("+statistics.productStatistics.reviewStatistics.totalReviewCount.toString()))
                }
            }
        }

        ecsProductViewModel.ecsPILProductsReviewList.value = mecProductReviewList
    }

    infix fun String.isEqualsTo(value: String): Boolean = this.replace("/", "_").equals(value)
}