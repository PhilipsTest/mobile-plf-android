/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.utils

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.philips.cdp.di.ecs.model.asset.Asset
import com.philips.platform.mec.R
import com.philips.platform.mec.networkEssentials.NetworkImageLoader
import com.philips.platform.mec.screens.detail.ImageAdapter
import com.philips.platform.mec.screens.features.ProductFeatureChildRecyclerAdapter
import com.philips.platform.mec.screens.features.ProductFeatureParentRecyclerAdapter
import com.philips.platform.mec.screens.specification.SpecificationChildRecyclerAdapter
import com.philips.platform.mec.screens.specification.SpecificationParentRecyclerAdapter
import com.philips.cdp.prxclient.datamodels.features.FeatureItem
import com.philips.cdp.prxclient.datamodels.features.FeaturesModel
import com.philips.cdp.prxclient.datamodels.specification.CsItemItem
import com.philips.cdp.prxclient.datamodels.specification.SpecificationModel
import com.philips.platform.uid.view.widget.Label


class DataBindingUtility {

    companion object DataBindingAdapter {


        @BindingAdapter("image_url")
        @JvmStatic
        fun loadImage(imageView: View?, image_url: String?) {

            val imageView = imageView as NetworkImageView
            val imageLoader = com.philips.platform.mec.networkEssentials.NetworkImageLoader.getInstance(imageView.context).imageLoader
            imageLoader.get(image_url, ImageLoader.getImageListener(imageView, 0,com.philips.platform.mec.R.drawable.no_icon))
            imageView.setImageUrl(image_url!!, imageLoader)
        }

        @BindingAdapter("nullable_image_url")
        @JvmStatic
        fun loadNonNullImage(imageView: View, image_url: String?) {

            val imageView = imageView as NetworkImageView

            if(image_url != null) {
                imageView.visibility = View.VISIBLE
                val imageLoader = com.philips.platform.mec.networkEssentials.NetworkImageLoader.getInstance(imageView.context).imageLoader
                imageLoader.get(image_url, ImageLoader.getImageListener(imageView, 0, com.philips.platform.mec.R.drawable.no_icon))

                imageView.setImageUrl(image_url!!, imageLoader)
            }else{
                imageView.visibility = View.GONE
            }
        }

        @JvmStatic
        @BindingAdapter("assets")
        fun setAdapter(pager: ViewPager, assets: List<Asset> ?) {
            if(assets!=null) {

                // modifying url for specific size image
                val  width = pager.context?.resources?.displayMetrics?.widthPixels ?: 0
                val height = pager.context?.resources?.getDimension(R.dimen.iap_product_detail_image_height)?.toInt()
                        ?:0
                val sizeExtension = "?wid=$width&hei=$height&\$pnglarge$&fit=fit,1"

                for (asset in assets){
                    asset.asset =  asset.asset + sizeExtension
                }

                pager.adapter = ImageAdapter(assets)
            }
        }

        @JvmStatic
        @BindingAdapter("addView")
        fun addView(layout: LinearLayout, hack: Int) {

            val mecBannerEnabler = MECDataHolder.INSTANCE.mecBannerEnabler

            if (mecBannerEnabler != null && mecBannerEnabler.bannerViewProductList != null) {

                if (mecBannerEnabler.bannerViewProductList.parent != null) {
                    val viewGroup = mecBannerEnabler.bannerViewProductList.parent as ViewGroup
                    viewGroup.removeAllViews()
                }

                layout.addView(mecBannerEnabler.bannerViewProductList)
                layout.visibility = View.VISIBLE
            }
        }

        @JvmStatic
        @BindingAdapter("setRating")
        fun setRating(ratingBar: RatingBar, rating: String) {
            ratingBar.rating = rating.toFloat()
        }



        //For specification
        @JvmStatic
        @BindingAdapter("items")
        fun setAdapter(recyclerView: RecyclerView, csItemItems: List<CsItemItem>?) {
            if(csItemItems!=null)
            recyclerView.adapter = SpecificationChildRecyclerAdapter(csItemItems)
        }
        @JvmStatic
        @BindingAdapter("specification")
        fun setSpecificationAdapter(recyclerView: RecyclerView, specificationModel: SpecificationModel?) {
            if(specificationModel!=null)
            recyclerView.adapter = SpecificationParentRecyclerAdapter(specificationModel)
        }

        //For Product Features

        @JvmStatic
        @BindingAdapter("featureItems")
        fun setProductFeatureChildAdapter(recyclerView: RecyclerView,featureItems: List<FeatureItem>?) {
            if(featureItems!=null){
                recyclerView.adapter = ProductFeatureChildRecyclerAdapter(featureItems)
            }

        }

        @JvmStatic
        @BindingAdapter("feature")
        fun setProductFeatureParentAdapter(recyclerView: RecyclerView, featuresModel: FeaturesModel?) {
            if(featuresModel!=null){
                recyclerView.adapter = ProductFeatureParentRecyclerAdapter(featuresModel)
            }

        }


        @JvmStatic
        @BindingAdapter("setCsValueItems")
        fun setCSItem(label: Label,csItemItem: CsItemItem) {

            val csValueItems = csItemItem.csValue
            var unit =""

            if(csItemItem.unitOfMeasure!=null){
                unit = csItemItem.unitOfMeasure.unitOfMeasureSymbol
            }

            val disclaimerStringBuilder = StringBuilder()

            if (!csValueItems.isNullOrEmpty()) {

                if(csValueItems.size == 1){
                    label.text = csValueItems.get(0).csValueName +" "+unit
                    return
                }

                for (csValueItem in csValueItems) {
                    disclaimerStringBuilder.append("- ").append(csValueItem.csValueName).append(System.getProperty("line.separator"))
                }
                label.text = disclaimerStringBuilder.toString()+unit
            }
        }


    }

}