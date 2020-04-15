package com.philips.platform.mec.screens.orderSummary

import android.content.Context
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.platform.mec.R
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary

class MECOrderSummaryServices {
    fun addAppliedOrderPromotionsToCartSummaryList(ecsShoppingCart: ECSShoppingCart, cartSummaryList: MutableList<MECCartSummary>) {
        var name: String
        var price: String
        if (ecsShoppingCart.appliedOrderPromotions.size > 0) {
            for (i in 0 until ecsShoppingCart.appliedOrderPromotions.size) {
                name = if (ecsShoppingCart.appliedOrderPromotions[i].promotion.name == null) {
                    " "
                } else {
                    ecsShoppingCart.appliedOrderPromotions[i].promotion.name
                }
                price = "-" + ecsShoppingCart.appliedOrderPromotions.get(i).promotion.promotionDiscount.formattedValue
                cartSummaryList.add(MECCartSummary(name, price))
            }
        }
    }

    fun addAppliedVoucherToCartSummaryList(ecsShoppingCart: ECSShoppingCart, cartSummaryList: MutableList<MECCartSummary>) {
        var name: String
        var price: String
        for (i in 0 until ecsShoppingCart.appliedVouchers.size) {
            name = if (ecsShoppingCart.appliedVouchers[i].name == null) {
                " "
            } else {
                ecsShoppingCart.appliedVouchers[i].name
            }
            price = "-" + ecsShoppingCart.appliedVouchers?.get(i)?.appliedValue?.formattedValue
            cartSummaryList.add(MECCartSummary(name, price))
        }
    }

    fun addDeliveryCostToCartSummaryList(context: Context, ecsShoppingCart: ECSShoppingCart, cartSummaryList: MutableList<MECCartSummary>) {
        val name: String
        val price: String
        if (ecsShoppingCart.deliveryCost != null) {
            name = context.getString(R.string.mec_shipping_cost)
            price = ecsShoppingCart.deliveryCost.formattedValue
            cartSummaryList.add(MECCartSummary(name, price))
        }
    }
}