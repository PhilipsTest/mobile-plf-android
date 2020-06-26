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

data class Item(
    val availability: Availability,
    val discountPrice: DiscountPrice,
    val id: String,
    val image: String,
    val itemId: String,
    val price: Price,
    val quantity: Int,
    val title: String,
    val totalPrice: TotalPrice
)