/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.ConversationsDisplayCallback
import com.bazaarvoice.bvandroidsdk.ConversationsException
import com.bazaarvoice.bvandroidsdk.Statistics
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.common.MecError
import java.text.DecimalFormat

class MECBulkRatingConversationsDisplayCallback(val ecsProducts: List<ECSProduct>, val ecsProductViewModel: EcsProductViewModel) : ConversationsDisplayCallback<BulkRatingsResponse> {


    override fun onSuccess(response: BulkRatingsResponse) {

        if (response.results.isEmpty()) {
            //ecsProductViewModel.mecError = //Util.showMessage(this@DisplayOverallRatingActivity, "Empty results", "No ratings found for this product")
        } else {
            createMECProductReviewObject(ecsProducts,response.results)
        }
    }

    override fun onFailure(exception: ConversationsException) {

        val ecsError = ECSError(1000,exception.localizedMessage)
        val mecError = MecError(exception, ecsError,null)
        ecsProductViewModel.mecError.value = mecError
    }


    private fun createMECProductReviewObject(ecsProducts: List<ECSProduct>, statisticsList: List<Statistics>) {

        var mecProductReviewList :MutableList<MECProductReview> = mutableListOf()

        for(ecsProduct in ecsProducts){

            for(statistics in statisticsList){

                if(ecsProduct.code isEqualsTo statistics.productStatistics.productId){

                    mecProductReviewList.add (MECProductReview(ecsProduct, DecimalFormat("0.0").format(statistics.productStatistics.reviewStatistics.averageOverallRating), " ("+statistics.productStatistics.reviewStatistics.totalReviewCount.toString()))
                }
            }
        }

        ecsProductViewModel.ecsProductsReviewList.value = mecProductReviewList
    }

    infix fun String.isEqualsTo(value: String): Boolean = this.replace("/", "_").equals(value)
}