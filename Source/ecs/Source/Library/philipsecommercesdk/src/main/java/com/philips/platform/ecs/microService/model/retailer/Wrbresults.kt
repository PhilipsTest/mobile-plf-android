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

package com.philips.platform.ecs.microService.model.retailer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wrbresults(
    val Ctn: String?,
    val EloquaSiteId: Int?,
    val EloquaSiteURL: String?,
    val OnlineStoresForProduct: OnlineStoresForProduct?,
    val RetailStoreAvailableFlag: Boolean?,
    val ShowBuyButton: String?,
    val Texts: Texts?,
    val storeLocatorUrl: String?
): Parcelable