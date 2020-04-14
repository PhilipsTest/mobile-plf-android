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
import androidx.recyclerview.widget.LinearLayoutManager
import com.philips.cdp.di.ecs.model.cart.BasePriceEntity
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.uid.view.widget.Label

class MECOrderHistoryService {

    fun isScrollDown(lay: LinearLayoutManager): Boolean {
        val visibleItemCount = lay.childCount
        val firstVisibleItemPosition = lay.findFirstVisibleItemPosition()
        return visibleItemCount + firstVisibleItemPosition >= lay.itemCount && firstVisibleItemPosition >= 0
    }

    // For doing static binding operations
    companion object {
        @JvmStatic
        @BindingAdapter("setPrice", "totalPriceEntity")
        fun setPrice(priceLabel: Label, product: ECSProduct?, basePriceEntity: BasePriceEntity?) {

        }
    }
}