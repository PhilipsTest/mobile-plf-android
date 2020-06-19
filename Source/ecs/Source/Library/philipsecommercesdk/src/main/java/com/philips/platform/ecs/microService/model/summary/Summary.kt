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

package com.philips.platform.ecs.microService.model.summary

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Summary(
    val SEOProductName: String?,
    val accessory: Boolean,
    val alphanumeric: String?,
    val brand: Brand?,
    val brandName: String?,
    val careSop: String?,
    val catCode: String?,
    val catNme: String?,
    val catalogs: List<Catalog>?,
    val categoryPath: String?,
    val ctn: String?,
    val descriptor: String?,
    val domain: String?,
    val dtn: String?,
    val eop: String?,
    val familyName: String?,
    val filterKeys: List<String>?,
    val grpCode: String?,
    val grpNme: String?,
    val gtin: String?,
    val imageURL: String?,
    val isDeleted: Boolean,
    val leafletUrl: String?,
    val locale: String?,
    val marketingTextHeader: String?,
    val price: PriceX?,
    val priority: Int,
    val productAlias: List<String>?,
    val productPagePath: String?,
    val productStatus: String?,
    val productTitle: String?,
    val productType: String?,
    val productURL: String?,
    val rank: Int,
    val reviewStatistics: ReviewStatistics?,
    val showOnlySupport: Boolean,
    val somp: String?,
    val sop: String?,
    val subCatRank: Int,
    val subWOW: String?,
    val subcatCode: String?,
    val subcategory: String?,
    val subcategoryName: String?,
    val versions: List<String>?,
    val wow: String?
):Parcelable