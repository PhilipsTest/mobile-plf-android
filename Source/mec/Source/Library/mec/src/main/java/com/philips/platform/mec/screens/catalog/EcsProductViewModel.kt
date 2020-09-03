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
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.mec.R
import com.philips.platform.mec.common.CommonViewModel
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.uid.view.widget.Label

class EcsProductViewModel : CommonViewModel() {


    var ecsPILProducts = MutableLiveData<ECSProducts>()

    val ecsPILProductsReviewList = MutableLiveData<MutableList<MECProductReview>>()

    val ecsServices = MECDataHolder.INSTANCE.eCSServices

    val ecsMicroService = ecsServices.microService

    var ecsCatalogRepository = ECSCatalogRepository()


    var ecsProductsCallback = ECSProductsCallback(this)


    fun fetchProducts(offSet: Int, limit: Int, productFilter: ProductFilter?) {
        ecsCatalogRepository.getProducts(offSet, limit, productFilter, ecsProductsCallback, ecsMicroService)
    }

    fun fetchProductSummaries(ctns: MutableList<String>) {
        ecsCatalogRepository.fetchProductSummaries(ctns, ecsProductsCallback, ecsMicroService)
    }

    fun fetchProductReview(products: List<ECSProduct>) {
        ecsCatalogRepository.fetchProductReview(products, this)
    }


    companion object DataBindingAdapter {

        //TODO to write test case
        @JvmStatic
        @BindingAdapter("setPriceInfo")
        fun setPriceInfo(priceLabel: Label, product: ECSProduct) {
            val textSize16 = priceLabel.context.getResources().getDimensionPixelSize(com.philips.platform.mec.R.dimen.mec_product_detail_discount_price_label_size);
            val textSize12 = priceLabel.context.getResources().getDimensionPixelSize(com.philips.platform.mec.R.dimen.mec_product_detail_price_label_size);
            if (product.attributes?.discountPrice?.formattedValue?.length ?: 0 > 0 && (product.attributes?.price?.value
                            ?: 0.0 - (product.attributes?.discountPrice?.value ?: 0.0)) > 0) {
                val price = SpannableString(product.attributes?.price?.formattedValue);
                price.setSpan(AbsoluteSizeSpan(textSize12), 0, product.attributes?.price?.formattedValue?.length
                        ?: 0, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                price.setSpan(StrikethroughSpan(), 0, product.attributes?.price?.formattedValue?.length
                        ?: 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                price.setSpan(ForegroundColorSpan(R.attr.uidContentItemTertiaryNormalTextColor), 0, product.attributes?.price?.formattedValue?.length
                        ?: 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                val discountPrice = SpannableString(product.attributes?.discountPrice?.formattedValue);
                discountPrice.setSpan(AbsoluteSizeSpan(textSize16), 0, product.attributes?.discountPrice?.formattedValue?.length
                        ?: 0, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                val CharSequence = TextUtils.concat(price, "  ", discountPrice);
                priceLabel.text = CharSequence
            } else {
                product.attributes?.price?.let { priceLabel.text = product.attributes?.price?.formattedValue }
            }
        }
    }

}