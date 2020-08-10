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

package com.philips.platform.ecs.microService.model.cart

import android.os.Parcelable
import com.philips.platform.ecs.microService.model.common.Price
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConsumedItem(
    val adjustedUnitPrice: Price?,
    val itemCartOrder: Int?,
    val quantity: Int?
):Parcelable