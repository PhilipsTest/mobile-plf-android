/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart


import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.cart.BasePriceEntity
import com.philips.cdp.di.ecs.model.cart.ECSEntries
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.cdp.di.ecs.model.voucher.ECSVoucher
import com.philips.platform.mec.R
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.Label

open class EcsShoppingCartViewModel : com.philips.platform.mec.common.CommonViewModel() {

    var ecsShoppingCart = MutableLiveData<ECSShoppingCart>()

    var ecsVoucher = MutableLiveData<List<ECSVoucher>>()

    val ecsProductsReviewList = MutableLiveData<MutableList<MECCartProductReview>>()



    var ecsServices = MECDataHolder.INSTANCE.eCSServices

     lateinit  var updateQuantityEntries :ECSEntries
     var updateQuantityNumber:Int = 0
     lateinit  var addVoucherString :String
     lateinit var deleteVoucherString :String

    private var ecsShoppingCartRepository = ECSShoppingCartRepository(this,ecsServices)



    var ecsVoucherCallback = ECSVoucherCallback(this)

    fun getShoppingCart(){
        ecsShoppingCartRepository.fetchShoppingCart()
    }

    fun createShoppingCart(request: String){
        val createShoppingCartCallback=  object: ECSCallback<ECSShoppingCart, Exception> {
            override fun onResponse(result: ECSShoppingCart?) {
                getShoppingCart()
            }
            override fun onFailure(error: Exception?, ecsError: ECSError?) {
                val mECError = MecError(error, ecsError,null)
                mecError.value = mECError
            }
        }
        ecsShoppingCartRepository.createCart(createShoppingCartCallback)
    }

    fun updateQuantity(entries: ECSEntries, quantity: Int) {
        updateQuantityEntries=entries
        updateQuantityNumber=quantity
        ecsShoppingCartRepository.updateShoppingCart(entries,quantity)
    }

    fun fetchProductReview(entries: MutableList<ECSEntries>) {
        ecsShoppingCartRepository.fetchProductReview(entries, this)
    }

    fun addVoucher(voucherCode : String, mECRequestType :MECRequestType){
        ecsVoucherCallback.mECRequestType = mECRequestType
        addVoucherString=voucherCode
        ecsShoppingCartRepository.applyVoucher(voucherCode,ecsVoucherCallback)
    }

    fun removeVoucher(voucherCode : String){
        ecsVoucherCallback.mECRequestType = MECRequestType.MEC_REMOVE_VOUCHER
        deleteVoucherString=voucherCode
        ecsShoppingCartRepository.removeVoucher(voucherCode,ecsVoucherCallback)
    }

    fun selectAPIcall(mecRequestType: MECRequestType):() -> Unit{

        lateinit  var APIcall: () -> Unit
        when(mecRequestType) {
            MECRequestType.MEC_FETCH_SHOPPING_CART  -> APIcall = { getShoppingCart() }
            MECRequestType.MEC_UPDATE_SHOPPING_CART -> APIcall = { updateQuantity(updateQuantityEntries,updateQuantityNumber) }
            MECRequestType.MEC_APPLY_VOUCHER        -> APIcall = { addVoucher(addVoucherString,ecsVoucherCallback.mECRequestType) }
            MECRequestType.MEC_REMOVE_VOUCHER       -> APIcall = { removeVoucher(deleteVoucherString) }

        }
        return APIcall
    }

    fun retryAPI(mecRequestType: MECRequestType) {
        var retryAPI = selectAPIcall(mecRequestType)
        authAndCallAPIagain(retryAPI,authFailCallback)
    }





    companion object {
        @JvmStatic
        @BindingAdapter("setPrice", "totalPriceEntity")
        fun setPrice(priceLabel: Label, product: ECSProduct?, basePriceEntity: BasePriceEntity?) {
            val textSize16 = priceLabel.context.getResources().getDimensionPixelSize(R.dimen.mec_product_detail_discount_price_label_size)
            val textSize12 = priceLabel.context.getResources().getDimensionPixelSize(R.dimen.mec_product_detail_price_label_size);
            if (product != null && basePriceEntity!!.formattedValue != null && basePriceEntity?.formattedValue!!.length > 0 && (product.price.value - basePriceEntity.value) > 0) {
                val price = SpannableString(product.price.formattedValue);
                price.setSpan(AbsoluteSizeSpan(textSize12), 0, product.price.formattedValue.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                price.setSpan(StrikethroughSpan(), 0, product.price.formattedValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                price.setSpan(ForegroundColorSpan(R.attr.uidContentItemTertiaryNormalTextColor), 0, product.price.formattedValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                val discountPrice = SpannableString(basePriceEntity.formattedValue)
                discountPrice.setSpan(AbsoluteSizeSpan(textSize16), 0, basePriceEntity.formattedValue.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                val CharSequence = TextUtils.concat(price, "  ", discountPrice)
                priceLabel.text = CharSequence;
            } else {
                if (product!!.price != null)
                    priceLabel.text = product.price.formattedValue
            }
        }

        @JvmStatic
        @BindingAdapter("setDiscountPrice", "totalPriceEntity")
        fun setDiscountPrice(discountPriceLabel: Label, product: ECSProduct?, basePriceEntity: BasePriceEntity?) {
            val discount = (product!!.price!!.value - basePriceEntity!!.value) / product.price!!.value * 100

            val discountRounded: String = String.format("%.2f", discount).toString()
            discountPriceLabel.text = "-" + discountRounded + "%"
            if (discountRounded.equals("0.00")) {
                discountPriceLabel.visibility = View.GONE
            } else {
                discountPriceLabel.visibility = View.VISIBLE
            }
        }

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("setStock","setQuantity")
        fun setStock(stockLabel : Label , product: ECSProduct?, quantity: Int) {
            if (null != product && null != product.stock) {
                if ((!MECutility.isStockAvailable(product.stock!!.stockLevelStatus, product.stock!!.stockLevel)) || (product.stock.stockLevel==0)) {
                    setSpannedText(stockLabel)
                }
                if(product.stock.stockLevel<=5 && product.stock.stockLevel!=0){
                    stockLabel.text = stockLabel.context.getString(R.string.mec_only) + " " + product.stock.stockLevel + " " + stockLabel.context.getString(R.string.mec_stock_available)
                }
                if(quantity>product.stock!!.stockLevel && product.stock.stockLevel!=0) {
                    stockLabel.text = stockLabel.context.getString(R.string.mec_only) + " " + product.stock.stockLevel + " " + stockLabel.context.getString(R.string.mec_stock_available);
                }
                stockLabel.visibility = View.VISIBLE
            }
        }

        private fun setSpannedText(stockLabel : Label ) {
            var context = stockLabel.context
            val stockMessage: String = context.getString(R.string.mec_cart_out_of_stock_message).toLowerCase()
            val stock: String =context.getString(R.string.mec_out_of_stock).toLowerCase()

            val startIndex = stockMessage.indexOf(stock)
            val endIndex = startIndex + stock.length

            val spanTxt = SpannableStringBuilder(context.getString(R.string.mec_cart_out_of_stock_message))
            spanTxt.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.uid_signal_red_level_60)), startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            stockLabel.text = spanTxt
        }
    }
    
}