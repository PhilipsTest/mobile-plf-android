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

import android.os.Parcelable
import com.philips.platform.ecs.microService.model.common.Price
import kotlinx.android.parcel.Parcelize

data class ProductPromotion(
    val consumedItems: List<ConsumedItem>,
    val id: String,
    val percentageDiscount: Double,
    val promotionDiscount: Price,
    val type: String
)