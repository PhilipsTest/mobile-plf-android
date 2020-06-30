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

package com.philips.platform.ecs.microService.model.product

import android.os.Parcelable
import com.philips.platform.ecs.microService.model.common.Availability
import com.philips.platform.ecs.microService.model.common.Price
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Attributes(
        val availability: Availability?,
        val deliveryTime: String?,
        val discountPrice: Price?,
        val image: String?,
        val price: Price?,
        val promotions: Promotions?,
        val references: List<Reference>?,
        val taxRelief: Boolean?,
        val title: String?
): Parcelable