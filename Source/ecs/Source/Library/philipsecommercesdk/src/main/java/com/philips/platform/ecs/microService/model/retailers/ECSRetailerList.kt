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

import java.io.Serializable
import java.util.*

/**
 * The type Ecs retailer list which contains the list of retailers selling the product
 * This object is returned when fetchRetailers
 */
data class ECSRetailerList(val wrbresults: WrbresultsEntity?)   {
    val retailers: List<ECSRetailer>? = wrbresults?.onlineStoresForProduct?.stores?.Store ?: ArrayList()
}