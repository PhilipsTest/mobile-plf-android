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

package com.philips.platform.ecs.microService.model.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val billingAddress: Boolean?,
    val country: Country?,
    val defaultAddress: Boolean?,
    val deliveryAddress: Boolean?,
    val firstName: String?,
    val houseNumber: String?,
    val id: String?,
    val lastName: String?,
    val line1: String?,
    val line2: String?,
    val phone: String?,
    val postalCode: String?,
    val region: Region?,
    val titleCode: String?,
    val town: String?
):Parcelable