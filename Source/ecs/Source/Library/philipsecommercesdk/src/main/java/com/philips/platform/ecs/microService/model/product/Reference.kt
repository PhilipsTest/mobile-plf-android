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
data class Reference(
        val availability: Availability?,
        val discountPrice: Price ?,
        val id: String ?,
        val image: String ?,
        val price: Price?,
        val productType: String ?,
        val title: String ?,
        val type: String ?
): Parcelable