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

import android.content.Context
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.mec.R
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary

class MECOrderDetailService {

    fun getProductSubcategory(ecsOrderDetail: ECSOrderDetail?): String? {
        val entriesList = ecsOrderDetail?.entries
        if(entriesList.isNullOrEmpty()) return null
        return entriesList.get(0)?.product?.summary?.subcategory
    }

    fun addAppliedOrderPromotionsToCartSummaryList(orderDetail: ECSOrderDetail, cartSummaryList: MutableList<MECCartSummary>) {
        var name: String
        var price: String
        // name should come instead of code
        if (orderDetail.appliedOrderPromotions.size > 0) {
            for (i in 0 until orderDetail.appliedOrderPromotions.size) {
                name = if (orderDetail.appliedOrderPromotions[i].promotion.code == null) {
                    " "
                } else {
                    orderDetail.appliedOrderPromotions[i].promotion.code
                }
                price = "-" + orderDetail.orderDiscounts.formattedValue
                cartSummaryList.add(MECCartSummary(name, price))
            }
        }
    }

    fun addAppliedVoucherToCartSummaryList(orderDetail: ECSOrderDetail, cartSummaryList: MutableList<MECCartSummary>) {
        var name: String
        var price: String
        for (i in 0 until orderDetail.appliedVouchers.size) {
            name = if (orderDetail.appliedVouchers[i].code == null) {
                " "
            } else {
                orderDetail.appliedVouchers[i].code
            }
            price = "-" + orderDetail.appliedVouchers?.get(i)?.appliedValue?.formattedValue
            cartSummaryList.add(MECCartSummary(name, price))
        }
    }

    fun addDeliveryCostToCartSummaryList(context: Context, orderDetail: ECSOrderDetail, cartSummaryList: MutableList<MECCartSummary>) {
        val name: String
        val price: String
        if (orderDetail.deliveryCost != null) {
            name = context.getString(R.string.mec_shipping_cost)
            price = orderDetail.deliveryCost.formattedValue
            cartSummaryList.add(MECCartSummary(name, price))
        }
    }
}