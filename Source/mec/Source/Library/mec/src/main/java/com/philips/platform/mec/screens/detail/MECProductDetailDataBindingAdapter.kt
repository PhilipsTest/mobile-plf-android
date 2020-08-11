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

package com.philips.platform.mec.screens.detail

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.philips.platform.ecs.microService.model.product.ECSProduct

import com.philips.platform.mec.R
import com.philips.platform.mec.screens.reviews.MECReview
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.Label
import com.philips.platform.uid.view.widget.ProgressBar

object MECProductDetailDataBindingAdapter {


    @JvmStatic
    @BindingAdapter("setDisclaimer")
    fun setDisclaimer(label: Label, ecsProduct: ECSProduct?) {

        val disclaimerStringBuilder = StringBuilder()

        ecsProduct?.disclaimers?.disclaimerList?.let {

            for (disclaimer in it) {
                disclaimer.disclaimerText
                disclaimerStringBuilder.append("- ").append(disclaimer.disclaimerText).append(System.getProperty("line.separator"))
            }
            label.text = disclaimerStringBuilder.toString()
        }
    }

    @JvmStatic
    @BindingAdapter("setStockInfo")
    fun setStockInfo(stockLabel: Label, product: ECSProduct?) {
        if (null != product && null != product.attributes?.availability) {
            if (MECutility.isStockAvailable(product.attributes?.availability!!.status, product.attributes?.availability?.quantity ?:0)) {
                stockLabel.text = stockLabel.context.getString(R.string.mec_in_stock)
                stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_green_level_30))
                // stockLabel.setTextColor(R.attr.uidContentItemSignalNormalTextSuccessColor)

            } else {
                stockLabel.text = stockLabel.context.getString(R.string.mec_out_of_stock)
                stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_red_level_30))
                // stockLabel.setTextColor(R.attr.uidContentItemSignalNormalTextErrorColor)
            }
        }
    }


    @BindingAdapter("mec_item_progressbar", "pager_item_image")
    @JvmStatic
    fun loadImage(imageView: ImageView, mec_item_progressbar: ProgressBar, pager_item_image: String?) {

        mec_item_progressbar.visibility = View.VISIBLE
        val imageLoader = com.philips.platform.mec.networkEssentials.NetworkImageLoader.getInstance(imageView.context).imageLoader

        imageLoader.get(pager_item_image, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer?, isImmediate: Boolean) {


                if (response?.bitmap != null) {
                    mec_item_progressbar.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    imageView.setImageBitmap(response.bitmap)
                }
            }

            override fun onErrorResponse(error: VolleyError?) {
                mec_item_progressbar.visibility = View.GONE

                if (error?.message != null) {
                    MECLog.e("Volley Loading", error?.message)
                }
                imageView.visibility = View.VISIBLE
                imageView.setImageDrawable(imageView.context.getDrawable(R.drawable.no_icon))
            }
        })
    }

    @BindingAdapter("review")
    @JvmStatic
    fun setAdapter(recyclerView: RecyclerView, mecReviews: MutableList<MECReview>) {
        recyclerView.adapter = MECReviewsAdapter(mecReviews)
    }

    @BindingAdapter("info")
    @JvmStatic
    fun setInfoAdapter(recyclerView: RecyclerView, product: ECSProduct?) {

        product?.let {

            var products: MutableList<ECSProduct> = mutableListOf()
            products.add(product)

            val mecProductInfoRecyclerAdapter = MECProductInfoRecyclerAdapter(products)
            recyclerView.adapter = mecProductInfoRecyclerAdapter
        }


    }
}