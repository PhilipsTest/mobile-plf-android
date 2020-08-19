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
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.cart.ECSItem
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.model.cart.BasePriceEntity
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.ecs.model.voucher.ECSVoucher
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant
import com.philips.platform.mec.analytics.MECAnalyticsConstant.scAdd
import com.philips.platform.mec.analytics.MECAnalyticsConstant.scRemove
import com.philips.platform.mec.analytics.MECAnalyticsConstant.voucherCode
import com.philips.platform.mec.analytics.MECAnalyticsConstant.voucherCodeApplied
import com.philips.platform.mec.analytics.MECAnalyticsConstant.voucherCodeRevoked
import com.philips.platform.mec.common.CommonViewModel
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.Label

open class EcsShoppingCartViewModel : CommonViewModel() {

    var ecsShoppingCart = MutableLiveData<ECSShoppingCart>()

    var ecsVoucher = MutableLiveData<List<ECSVoucher>>()

    val ecsProductsReviewList = MutableLiveData<MutableList<MECCartProductReview>>()



    var ecsServices = MECDataHolder.INSTANCE.eCSServices

     var updateQuantityCartItem : ECSItem? =null
     var updateQuantityNumber:Int = 0
     lateinit  var addVoucherString :String
     lateinit var deleteVoucherString :String

     var ecsShoppingCartRepository = ECSShoppingCartRepository(this,ecsServices)



    var ecsVoucherCallback = ECSVoucherCallback(this)

    fun getShoppingCart(){
        ecsVoucherCallback.mECRequestType = MECRequestType.MEC_FETCH_SHOPPING_CART
        ecsShoppingCartRepository.fetchShoppingCart()
    }

    fun createShoppingCart(request: String){
        ecsShoppingCartRepository.createCart("CTN",createShoppingCartCallback)
    }

    var createShoppingCartCallback=  object: ECSCallback<ECSShoppingCart, ECSError> {
        override fun onResponse(result: ECSShoppingCart) {
            getShoppingCart()
        }
        override fun onFailure(ecsError: ECSError) {
            val occECSError = com.philips.platform.ecs.error.ECSError(ecsError.errorCode?:-100,ecsError.errorType?.name)
            val mECError = MecError(Exception(ecsError.errorMessage), occECSError, null)
            mecError.value = mECError
        }
    }


    fun updateQuantity(cartItem: ECSItem, quantity: Int) {
        updateQuantityCartItem=cartItem
        updateQuantityNumber=quantity
        ecsShoppingCartRepository.updateShoppingCart(cartItem,quantity)
    }

    fun fetchProductReview(items: MutableList<ECSItem>) {
        ecsShoppingCartRepository.fetchProductReview(items, this,MECDataHolder.INSTANCE.bvClient)
    }

    fun addVoucher(voucherCode : String,  mECRequestType :MECRequestType){
        ecsVoucherCallback.mECRequestType = mECRequestType
        addVoucherString=voucherCode
        ecsShoppingCartRepository.applyVoucher(voucherCode,ecsVoucherCallback)
    }

    fun removeVoucher(voucherCode : String){
        ecsVoucherCallback.mECRequestType = MECRequestType.MEC_REMOVE_VOUCHER
        deleteVoucherString=voucherCode
        ecsShoppingCartRepository.removeVoucher(voucherCode,ecsVoucherCallback)
    }

    fun tagApplyOrDeleteVoucher(mECRequestType :MECRequestType){
        val actionMap = HashMap<String, String>()
        if(mECRequestType==MECRequestType.MEC_APPLY_VOUCHER || mECRequestType==MECRequestType.MEC_APPLY_VOUCHER_SILENT){
            actionMap[MECAnalyticsConstant.specialEvents] = voucherCodeApplied
            actionMap[voucherCode] = addVoucherString
        }else{
            actionMap[MECAnalyticsConstant.specialEvents] = voucherCodeRevoked
            actionMap[voucherCode] = deleteVoucherString
        }
        //TODO tagging
       // ecsShoppingCart.value?.data?.attributes?.items.let { MECAnalytics.tagActionsWithOrderProductsInfo(actionMap, it) }

    }

    fun tagProductAddedOrDeleted(){

        //TODO tag deleted product
     /*   var actionMap = HashMap<String, String>()
        if(updateQuantityNumber< updateQuantityCartItem?.quantity!!){ // if product quantity is reduced or deleted(updateQuantityNumber=0)
            actionMap.put(MECAnalyticsConstant.specialEvents, scRemove)
            actionMap.put(MECAnalyticsConstant.mecProducts, MECAnalytics.getProductInfoWithChangedQuantity(updateQuantityCartItem?.product!!, updateQuantityCartItem?.basePrice!!, updateQuantityCartItem?.quantity!!-updateQuantityNumber))
            MECAnalytics.trackMultipleActions(MECAnalyticsConstant.sendData, actionMap)
        } else{// if product quantity is added
            actionMap.put(MECAnalyticsConstant.specialEvents, scAdd)
            actionMap.put(MECAnalyticsConstant.mecProducts, MECAnalytics.getProductInfoWithChangedQuantity(updateQuantityCartItem?.product!!, updateQuantityCartItem?.basePrice!!, updateQuantityNumber-updateQuantityCartItem?.quantity!!))
            MECAnalytics.trackMultipleActions(MECAnalyticsConstant.sendData, actionMap)
        }*/

    }

    fun selectAPIcall(mecRequestType: MECRequestType):() -> Unit{

        lateinit  var APIcall: () -> Unit
        when(mecRequestType) {
            MECRequestType.MEC_FETCH_SHOPPING_CART  -> APIcall = { getShoppingCart() }
            MECRequestType.MEC_UPDATE_SHOPPING_CART -> APIcall = { updateQuantityCartItem?.let { updateQuantity(it,updateQuantityNumber) } }
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
            if (product != null && basePriceEntity?.formattedValue != null && basePriceEntity?.formattedValue!!.length > 0 && (product.price.value - basePriceEntity.value) > 0) {
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
        fun setStock(stockLabel : Label, product: ECSProduct?, quantity: Int) {
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