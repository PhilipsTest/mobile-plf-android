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

package com.philips.platform.mec.screens.history.orderDetail

import com.philips.platform.ecs.model.orders.ECSOrderDetail

class MECOrderDetailService {

    fun getProductSubcategory(ecsOrderDetail: ECSOrderDetail?): String? {
        return ecsOrderDetail?.entries?.get(0)?.product?.summary?.subcategory
    }
}