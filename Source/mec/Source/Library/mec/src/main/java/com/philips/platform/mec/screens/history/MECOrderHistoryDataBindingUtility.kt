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

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.cart.BasePriceEntity
import com.philips.cdp.di.ecs.model.orders.Entries
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.uid.view.widget.Label

class MECOrderHistoryDataBindingUtility {

    companion object {
        @JvmStatic
        @BindingAdapter("setPrice", "totalPriceEntity")
        fun setPrice(priceLabel: Label, product: ECSProduct?, basePriceEntity: BasePriceEntity?) {

        }

        @JvmStatic
        @BindingAdapter("mecOrderModel")
        fun setOrderHistoryAdapter(recyclerView: RecyclerView, mecOrdersModel: MECOrdersModel?) {
            if(mecOrdersModel!=null)
                recyclerView.adapter = MECOrderHistoryAdapter(mecOrdersModel)
        }

        @JvmStatic
        @BindingAdapter("mecEntriesModel")
        fun setOrderDetailAdapter(recyclerView: RecyclerView, entriesList: List<Entries>?) {
            if(entriesList!=null)
                recyclerView.adapter = MECOrderDetailAdapter(MECEntriesModel(entriesList as MutableList<Entries>))
        }
    }
}