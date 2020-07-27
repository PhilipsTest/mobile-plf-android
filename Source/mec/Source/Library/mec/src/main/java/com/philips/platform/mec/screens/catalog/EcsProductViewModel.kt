/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog


import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.mec.R
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.uid.view.widget.Label

class EcsProductViewModel : com.philips.platform.mec.common.CommonViewModel() {


    var ecsPILProducts = MutableLiveData<com.philips.platform.ecs.microService.model.product.ECSProducts>()

    val ecsPILProductsReviewList = MutableLiveData<MutableList<MECProductReview>>()

    val ecsServices = MECDataHolder.INSTANCE.eCSServices

    val ecsMicroService = ecsServices.microService

    var ecsCatalogRepository = ECSCatalogRepository()


    var ecsProductsCallback = ECSProductsCallback(this)


    fun fetchProducts(offSet: Int, limit: Int) {
        ecsCatalogRepository.getProducts(offSet,limit,ecsProductsCallback,ecsMicroService)
    }

    fun initCategorizedRetailer(ctns: MutableList<String>) {
        ecsCatalogRepository.getCategorizedProductsForRetailer(ctns,ecsProductsCallback ,ecsMicroService)
    }

    fun fetchProductReview(products: List<com.philips.platform.ecs.microService.model.product.ECSProduct>) {
        ecsCatalogRepository.fetchProductReview(products, this)
    }


    companion object DataBindingAdapter {


        @JvmStatic
        @BindingAdapter("setPriceInfo")
        fun setPriceInfo(priceLabel: Label, product: ECSProduct) {
            val textSize16 = priceLabel.context.getResources().getDimensionPixelSize(com.philips.platform.mec.R.dimen.mec_product_detail_discount_price_label_size);
            val textSize12 = priceLabel.context.getResources().getDimensionPixelSize(com.philips.platform.mec.R.dimen.mec_product_detail_price_label_size);
            if (product!=null && product.discountPrice!=null && product.discountPrice.formattedValue != null && product.discountPrice.formattedValue.length > 0 && (product.price.value - product.discountPrice.value) > 0) {
                val price = SpannableString(product.price.formattedValue);
                price.setSpan(AbsoluteSizeSpan(textSize12), 0, product.price.formattedValue.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                price.setSpan(StrikethroughSpan(), 0, product.price.formattedValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                price.setSpan(ForegroundColorSpan(R.attr.uidContentItemTertiaryNormalTextColor), 0, product.price.formattedValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                val discountPrice = SpannableString(product.discountPrice.formattedValue);
                discountPrice.setSpan(AbsoluteSizeSpan(textSize16), 0, product.discountPrice.formattedValue.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                val CharSequence = TextUtils.concat(price, "  ", discountPrice);
                priceLabel.text = CharSequence;
            } else {
                if(product.price!=null)
                priceLabel.text = product.price.formattedValue;
            }
        }

        @JvmStatic
        @BindingAdapter("setPriceInfo")
        fun setPriceInfo(priceLabel: Label, product: com.philips.platform.ecs.microService.model.product.ECSProduct) {
            val textSize16 = priceLabel.context.getResources().getDimensionPixelSize(com.philips.platform.mec.R.dimen.mec_product_detail_discount_price_label_size);
            val textSize12 = priceLabel.context.getResources().getDimensionPixelSize(com.philips.platform.mec.R.dimen.mec_product_detail_price_label_size);
            if (product.attributes?.discountPrice?.formattedValue?.length ?:0 > 0 && (product.attributes?.price?.value ?:0.0 - (product.attributes?.discountPrice?.value ?:0.0)) > 0) {
                val price = SpannableString(product.attributes?.price?.formattedValue);
                price.setSpan(AbsoluteSizeSpan(textSize12), 0, product.attributes?.price?.formattedValue?.length ?:0, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                price.setSpan(StrikethroughSpan(), 0, product.attributes?.price?.formattedValue?.length ?:0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                price.setSpan(ForegroundColorSpan(R.attr.uidContentItemTertiaryNormalTextColor), 0, product.attributes?.price?.formattedValue?.length ?:0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                val discountPrice = SpannableString(product.attributes?.discountPrice?.formattedValue);
                discountPrice.setSpan(AbsoluteSizeSpan(textSize16), 0, product.attributes?.discountPrice?.formattedValue?.length ?:0, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                val CharSequence = TextUtils.concat(price, "  ", discountPrice);
                priceLabel.text = CharSequence;
            } else {
                product.attributes?.price?.let { priceLabel.text = product.attributes?.price?.formattedValue }
            }
        }
    }

}