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
package com.philips.platform.ecs.microService.model.retailers

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class WrbresultsEntity (
    val ctn: String?,
    val storeLocatorUrl: String?,
    val eloquaSiteURL: String?,
    val showBuyButton: String?,
    val eloquaSiteId :Int =0,
    val isRetailStoreAvailableFlag :Boolean= false,
    val texts: TextsEntity?,
    val onlineStoresForProduct: OnlineStoresForProductEntity?

):Parcelable