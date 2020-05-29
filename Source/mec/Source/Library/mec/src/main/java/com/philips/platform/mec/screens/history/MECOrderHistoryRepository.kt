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

import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.orders.ECSOrders

class MECOrderHistoryRepository(var ecsService: ECSServices) {

    fun fetchOrderHistory(pageNumber: Int, pageSize: Int, ecsOrderHistoryCallback: ECSOrderHistoryCallback) {
        ecsService.fetchOrderHistory(pageNumber,pageSize,ecsOrderHistoryCallback)
    }
    fun fetchOrderDetail(ecsOrders: ECSOrders, ecsCallback: ECSCallback<ECSOrders, Exception>) {
        ecsService.fetchOrderDetail(ecsOrders,ecsCallback)
    }
}