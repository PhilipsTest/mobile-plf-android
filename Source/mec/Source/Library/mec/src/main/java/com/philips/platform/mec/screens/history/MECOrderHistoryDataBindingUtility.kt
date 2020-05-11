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

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.R
import com.philips.platform.mec.common.ItemClickListener


class MECOrderHistoryDataBindingUtility {

    companion object {

        @JvmStatic
        @BindingAdapter("ecsOrders","itemClickListener")
        fun setOrderDetailAdapter(recyclerView: RecyclerView, ecsOrders: ECSOrders?, itemClickListener: ItemClickListener) {
            if(ecsOrders?.orderDetail != null )
                recyclerView.adapter = MECOrderDetailAdapter(ecsOrders,itemClickListener)
        }

        @JvmStatic
        @BindingAdapter("ecsOrdersList","ordersClickListener")
        fun setOrdersAdapter(recyclerView: RecyclerView, ecsOrdersList: MutableList<ECSOrders>, itemClickListener: ItemClickListener) {

            if(!ecsOrdersList.isNullOrEmpty()){
                val mecOrdersAdapter = MECOrdersAdapter(ecsOrdersList, itemClickListener)
                recyclerView.adapter = mecOrdersAdapter
            }

        }

        @BindingAdapter("history_order_product_image")
        @JvmStatic
        fun setOrderHistoryProductURL(imageView: View?, history_order_product_image_url: String?) {

            if(history_order_product_image_url!=null) {
                val imageView = imageView as NetworkImageView

                val  width = imageView.context?.resources?.displayMetrics?.widthPixels ?: 0
                val height = imageView.context?.resources?.getDimension(R.dimen.iap_list_header)?.toInt()
                        ?:0
                val sizeExtension = "?wid=$width&hei=$height&\$pnglarge$&fit=fit,1"
                val sinURL = history_order_product_image_url + sizeExtension
                val imageLoader = com.philips.platform.mec.networkEssentials.NetworkImageLoader.getInstance(imageView.context).imageLoader
                imageLoader.get(sinURL, ImageLoader.getImageListener(imageView, 0, com.philips.platform.mec.R.drawable.no_icon))
                imageView.setImageUrl(sinURL, imageLoader)
            }
        }
    }
}