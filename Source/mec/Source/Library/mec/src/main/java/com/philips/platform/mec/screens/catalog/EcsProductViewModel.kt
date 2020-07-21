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
import com.philips.platform.ecs.model.products.ECSProducts
import com.philips.platform.mec.R
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.uid.view.widget.Label

class EcsProductViewModel : com.philips.platform.mec.common.CommonViewModel() {

    var ecsProductsList = MutableLiveData<MutableList<ECSProducts>>()

    var ecsPILProducts = MutableLiveData<com.philips.platform.ecs.microService.model.product.ECSProducts>()

    val ecsProductsReviewList = MutableLiveData<MutableList<MECProductReview>>()

    val ecsPILProductsReviewList = MutableLiveData<MutableList<PILMECProductReview>>()

    val ecsServices = MECDataHolder.INSTANCE.eCSServices

    var ecsCatalogRepository = ECSCatalogRepository()

    var ecsProductsCallback = ECSProductsCallback(this)

    var ecsPILProductsCallback = ECSPILProductsCallback(this)

    var ecsProductListCallback = ECSProductListCallback(this)


    fun fetchProducts(pageNumber: Int, pageSize: Int) {
        ecsProductListCallback.mECRequestType=MECRequestType.MEC_FETCH_PRODUCTS
        ecsCatalogRepository.getProducts(pageNumber, pageSize,ecsProductsCallback,ecsServices)
    }

    fun fetchPILProducts(offSet: Int, limit: Int) {
        ecsProductListCallback.mECRequestType=MECRequestType.MEC_FETCH_PRODUCTS
        ecsCatalogRepository.getProducts(offSet,limit,ecsPILProductsCallback,ecsServices)
    }

    fun initCategorizedRetailer(ctn: MutableList<String>) {
        ecsProductListCallback.mECRequestType=MECRequestType.MEC_FETCH_PRODUCTS
        ecsCatalogRepository.getCategorizedProductsForRetailer(ctn,ecsProductListCallback ,ecsServices)
    }


    fun initCategorized(pageNumber: Int, pageSize: Int, ctns: List<String>) {
        ecsProductListCallback.mECRequestType=MECRequestType.MEC_FETCH_PRODUCTS
        ecsCatalogRepository.getCategorizedProducts(pageNumber, pageSize,ctns.size, ctns,this.ecsProductsList.value, this)
    }

    fun fetchProductReview(products: List<ECSProduct>) {
        ecsCatalogRepository.fetchProductReview(products, this)
    }

    fun fetchPILProductReview(products: List<com.philips.platform.ecs.microService.model.product.ECSProduct>) {
        ecsCatalogRepository.fetchPILProductReview(products, this)
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
    }

}