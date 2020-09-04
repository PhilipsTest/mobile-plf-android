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
import com.philips.platform.ecs.microService.model.common.Price
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
        ecsShoppingCartRepository.createCart("CTN")
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
        ecsShoppingCart.value?.data?.attributes?.items.let { it?.let { it1 -> MECAnalytics.tagActionsWithOrderProductsInfo(actionMap, it1) } }

    }

    fun tagProductAddedOrDeleted(){
        val productInformation = updateQuantityCartItem?.let { MECAnalytics.getProductInformation(it) } ?:""
        val actionMap = HashMap<String, String>()
        if(updateQuantityNumber< updateQuantityCartItem?.quantity!!){ // if product quantity is reduced or deleted(updateQuantityNumber=0)
            actionMap.put(MECAnalyticsConstant.specialEvents, scRemove)
            actionMap.put(MECAnalyticsConstant.mecProducts,productInformation)
            MECAnalytics.trackMultipleActions(MECAnalyticsConstant.sendData, actionMap)
        } else{// if product quantity is added
            actionMap.put(MECAnalyticsConstant.specialEvents, scAdd)
            actionMap.put(MECAnalyticsConstant.mecProducts,productInformation)
            MECAnalytics.trackMultipleActions(MECAnalyticsConstant.sendData, actionMap)
        }

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


        private fun setSpannedText(stockLabel : Label ) {
            val context = stockLabel.context
            val stockMessage: String = context.getString(R.string.mec_cart_out_of_stock_message).toLowerCase()
            val stock: String =context.getString(R.string.mec_out_of_stock).toLowerCase()

            val startIndex = stockMessage.indexOf(stock)
            val endIndex = startIndex + stock.length

            val spanTxt = SpannableStringBuilder(context.getString(R.string.mec_cart_out_of_stock_message))
            spanTxt.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.uid_signal_red_level_60)), startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            stockLabel.text = spanTxt
        }

        // ======== for PIL cart operations

        @JvmStatic
        @BindingAdapter("setEcsItemForPriceLebel")
        fun setItemPrice(priceLabel: Label, ecsItem: ECSItem?) {


            val textSize16 = priceLabel.context.resources.getDimensionPixelSize(R.dimen.mec_product_detail_discount_price_label_size)
            val textSize12 = priceLabel.context.resources.getDimensionPixelSize(R.dimen.mec_product_detail_price_label_size)

            val itemPriceFormattedValue = ecsItem?.price?.formattedValue ?:""
            val itemDiscountedPriceFormattedValue = ecsItem?.discountPrice?.formattedValue ?:""

            val itemPriceValue = ecsItem?.price?.value ?:0.00
            val itemDiscountedPriceValue = ecsItem?.discountPrice?.value ?:0.00


            if (itemDiscountedPriceFormattedValue.isNotEmpty() && (itemPriceValue - itemDiscountedPriceValue) > 0) {
                val price = SpannableString(itemPriceFormattedValue)
                price.setSpan(AbsoluteSizeSpan(textSize12), 0, itemPriceFormattedValue.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                price.setSpan(StrikethroughSpan(), 0, itemPriceFormattedValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                price.setSpan(ForegroundColorSpan(R.attr.uidContentItemTertiaryNormalTextColor), 0,  itemPriceFormattedValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                val discountPrice = SpannableString(itemDiscountedPriceFormattedValue)
                discountPrice.setSpan(AbsoluteSizeSpan(textSize16), 0,itemDiscountedPriceFormattedValue.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                val CharSequence = TextUtils.concat(price, "  ", discountPrice)
                priceLabel.text = CharSequence
            } else {
                    priceLabel.text = itemPriceFormattedValue
            }
        }

        @JvmStatic
        @BindingAdapter("setEcsItemForDiscountedPriceLebel")
        fun setItemDiscountPrice(discountPriceLabel: Label,ecsItem: ECSItem?) {

            val itemPriceValue = ecsItem?.price?.value ?:0.00
            val itemDiscountedPriceValue = ecsItem?.discountPrice?.value ?:0.00
            val discount = (itemPriceValue -itemDiscountedPriceValue) / itemPriceValue * 100

            val discountRounded: String = String.format("%.2f", discount)
            discountPriceLabel.text = "-" + discountRounded + "%"
            if (discountRounded == "0.00") {
                discountPriceLabel.visibility = View.GONE
            } else {
                discountPriceLabel.visibility = View.VISIBLE
            }
        }

        @JvmStatic
        @BindingAdapter("setEcsItemForStockLebel")
        fun setStock(stockLabel : Label, ecsItem: ECSItem?) {

                val itemQuantity = ecsItem?.quantity ?:0
                val itemAvailableStatus = ecsItem?.availability?.status ?:""
                val itemAvailableQuantity = ecsItem?.availability?.quantity ?:0

                if ((!MECutility.isStockAvailable(itemAvailableStatus, itemAvailableQuantity))) {
                    setSpannedText(stockLabel)
                }
                if(itemAvailableQuantity<=5 && itemAvailableQuantity!=0){
                    stockLabel.text = stockLabel.context.getString(R.string.mec_only) + " " + itemAvailableQuantity + " " + stockLabel.context.getString(R.string.mec_stock_available)
                }
                if(itemQuantity > itemAvailableQuantity && itemAvailableQuantity!=0) {
                    stockLabel.text = stockLabel.context.getString(R.string.mec_only) + " " + itemAvailableQuantity + " " + stockLabel.context.getString(R.string.mec_stock_available)
                }
                stockLabel.visibility = View.VISIBLE

        }

    }
    
}