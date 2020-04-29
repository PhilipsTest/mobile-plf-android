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

package com.philips.platform.mec.screens.history

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSOrderDetailForOrdersCallback(private val mecOrderHistoryViewModel: MECOrderHistoryViewModel) : ECSCallback<ECSOrders, Exception> {

    var mECRequestType = MECRequestType.MEC_FETCH_ORDER_DETAILS_FOR_ORDERS

    override fun onResponse(result: ECSOrders?) {
        mecOrderHistoryViewModel.countDownThread()
        mecOrderHistoryViewModel.ecsOrders.value = result
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {

        val mecError = MecError(error, ecsError, mECRequestType)

        if (MECutility.isAuthError(ecsError)) {
            mecOrderHistoryViewModel.retryAPI(mECRequestType)
        } else {
            mecOrderHistoryViewModel.countDownThread()
            mecOrderHistoryViewModel.mecError.value = mecError
        }
    }
}