/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart

import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.ConversationsDisplayCallback
import com.bazaarvoice.bvandroidsdk.ConversationsException
import com.bazaarvoice.bvandroidsdk.Statistics
import com.philips.platform.ecs.microService.model.cart.ECSItem
import com.philips.platform.mec.common.MecError
import java.text.DecimalFormat

class MECBulkRatingCallback(private val ecsProducts: MutableList<ECSItem>, private val ecsShoppingCartViewModel: EcsShoppingCartViewModel) : ConversationsDisplayCallback<BulkRatingsResponse> {


    override fun onSuccess(response: BulkRatingsResponse) {

        if (response.results.isEmpty()) {
            //ecsProductViewModel.mecError = //Util.showMessage(this@DisplayOverallRatingActivity, "Empty results", "No ratings found for this product")
        } else {
            createMECProductReviewObject(ecsProducts,response.results)
        }
    }

    override fun onFailure(exception: ConversationsException) {

        val ecsError = com.philips.platform.ecs.error.ECSError(1000, exception.localizedMessage)
        val mecError = MecError(exception, ecsError,null)
        ecsShoppingCartViewModel.mecError.value = mecError
    }


    private fun createMECProductReviewObject(ecsProducts: MutableList<ECSItem>, statisticsList: List<Statistics>) {

        var mecProductReviewList :MutableList<MECCartProductReview> = mutableListOf()

        for(ecsProduct in ecsProducts){

            for(statistics in statisticsList){

                ecsProduct.ctn?.let {
                    if(it.isEqualsTo (statistics.productStatistics.productId)){

                        mecProductReviewList.add (MECCartProductReview(ecsProduct, DecimalFormat("#.#").format(statistics.productStatistics.reviewStatistics.averageOverallRating), " ("+statistics.productStatistics.reviewStatistics.totalReviewCount.toString()+ ")"))
                    }
                }

            }
        }

        ecsShoppingCartViewModel.ecsProductsReviewList.value = mecProductReviewList
    }

    private infix fun String.isEqualsTo(value: String): Boolean = this.replace("/", "_").equals(value)
}