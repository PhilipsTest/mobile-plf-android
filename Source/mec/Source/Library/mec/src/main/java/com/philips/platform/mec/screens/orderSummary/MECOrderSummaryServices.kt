package com.philips.platform.mec.screens.orderSummary

import android.content.Context
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.mec.R
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary

class MECOrderSummaryServices {

    fun addAppliedOrderPromotionsToCartSummaryList(ecsShoppingCart: ECSShoppingCart, cartSummaryList: MutableList<MECCartSummary>) {

        val appliedPromotions = ecsShoppingCart.data?.attributes?.promotions?.appliedPromotions

        appliedPromotions?.let {

            for(promotion in appliedPromotions){
                val name = promotion.code ?:""
                val price = "TO DO"
                cartSummaryList.add(MECCartSummary(name, price))
            }
        }
    }

    fun addAppliedVoucherToCartSummaryList(ecsShoppingCart: ECSShoppingCart, cartSummaryList: MutableList<MECCartSummary>) {

        val appliedVouchers = ecsShoppingCart.data?.attributes?.appliedVouchers

        appliedVouchers?.let {

            for(voucher in appliedVouchers){
                val name = voucher.name ?:""
                val price = voucher.value?.formattedValue ?:""
                cartSummaryList.add(MECCartSummary(name, price))
            }
        }
    }

    fun addDeliveryCostToCartSummaryList(context: Context, ecsShoppingCart: ECSShoppingCart, cartSummaryList: MutableList<MECCartSummary>) {
        val name: String = context.getString(R.string.mec_shipping_cost)
        val price:String = ecsShoppingCart.data?.attributes?.pricing?.delivery?.formattedValue ?:""
        cartSummaryList.add(MECCartSummary(name, price))
    }
}