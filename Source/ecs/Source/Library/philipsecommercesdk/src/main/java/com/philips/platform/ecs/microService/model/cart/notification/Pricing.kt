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

package com.philips.platform.ecs.microService.model.cart.notification

data class Pricing(
    val delivery: Delivery,
    val itemDiscount: ItemDiscount,
    val net: Boolean,
    val orderDiscount: OrderDiscount,
    val orderDiscountNoDelivery: OrderDiscountNoDelivery,
    val subTotal: SubTotal,
    val tax: Tax,
    val total: Total,
    val totalDelivery: TotalDelivery,
    val totalDiscountNoDelivery: TotalDiscountNoDelivery,
    val totalNoDelivery: TotalNoDelivery
)