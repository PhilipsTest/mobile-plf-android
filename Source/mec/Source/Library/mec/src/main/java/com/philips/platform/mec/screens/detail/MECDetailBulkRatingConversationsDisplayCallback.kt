/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.ConversationsDisplayCallback
import com.bazaarvoice.bvandroidsdk.ConversationsException
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.platform.mec.common.MecError

class MECDetailBulkRatingConversationsDisplayCallback(private val ecsProductDetailViewModel: EcsProductDetailViewModel) : ConversationsDisplayCallback<BulkRatingsResponse> {

    override fun onSuccess(response: BulkRatingsResponse) {
            ecsProductDetailViewModel.bulkRatingResponse.value = response
    }

    override fun onFailure(exception: ConversationsException) {

        val exception = Exception("Fetch Rating failed")
        val ecsError = ECSError(1000,"Fetch Rating failed")
        val mecError = MecError(exception, ecsError,null)
        ecsProductDetailViewModel.mecError.value =mecError
    }
}