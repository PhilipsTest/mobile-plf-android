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
import com.philips.platform.ecs.model.orders.ConsignmentEntries
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
                name = if (orderDetail.appliedOrderPromotions[i].promotion.code == null) " " else orderDetail.appliedOrderPromotions[i].promotion.code
                price = "-" + orderDetail.orderDiscounts.formattedValue
                cartSummaryList.add(MECCartSummary(name, price))
            }
        }
    }

    fun addAppliedVoucherToCartSummaryList(orderDetail: ECSOrderDetail, cartSummaryList: MutableList<MECCartSummary>) {
        var name: String
        var price: String
        for (i in 0 until orderDetail.appliedVouchers.size) {
            name = if(orderDetail.appliedVouchers[i].code == null) " " else orderDetail.appliedVouchers[i].code
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

    // methods to get product corresponding track url fro ECSOrder
     fun getEntriesFromConsignMent(detail: ECSOrderDetail, ctn: String): ConsignmentEntries? {
        if (detail.consignments.isNullOrEmpty()) return null
        for (consignment in detail.consignments) {
            for (entries in consignment.entries) {
                val consignmentCtn = entries.orderEntry.product.code
                if (ctn.trim { it <= ' ' }.equals(consignmentCtn.trim({ it <= ' ' }), ignoreCase = true)) {
                    return entries
                }
            }
        }
        return null
    }

     fun getOrderTrackUrl(entries: ConsignmentEntries?): String? {
        if (entries == null) return null
        if (entries.trackAndTraceIDs.isNullOrEmpty() || entries.trackAndTraceUrls.isNullOrEmpty()) {
            return null
        }
        val trackAndTraceID = entries.trackAndTraceIDs.get(0)
        val trackAndTraceUrl = entries.trackAndTraceUrls.get(0)
        return getTrackUrl(trackAndTraceID, trackAndTraceUrl)
    }

    private fun getTrackUrl(trackAndTraceID: String, trackAndTraceUrl: String): String {
        //sample URL
        //{300068874=http:\/\/www.fedex.com\/Tracking?action=track&cntry_code=us&tracknumber_list=300068874}
        val urlWithEndCurlyBrace = trackAndTraceUrl.replace("{$trackAndTraceID=", "")
        return urlWithEndCurlyBrace.replace("}", "")
    }

}