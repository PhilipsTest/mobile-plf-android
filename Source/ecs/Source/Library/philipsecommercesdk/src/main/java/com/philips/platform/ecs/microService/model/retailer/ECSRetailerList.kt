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
import java.util.ArrayList

@Parcelize
data class ECSRetailerList(val wrbresults: Wrbresults?): Parcelable {

    val retailers: List<ECSRetailer>? = wrbresults?.OnlineStoresForProduct?.Stores?.Store
}
