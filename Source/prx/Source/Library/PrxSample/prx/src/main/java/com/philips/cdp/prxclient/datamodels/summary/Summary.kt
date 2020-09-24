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

package com.philips.cdp.prxclient.datamodels.summary

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Summary(
        val SEOProductName: String? = null,
        val accessory: Boolean,
        val alphanumeric: String? = null,
        val brand: Brand? = null,
        val brandName: String? = null,
        val careSop: String? = null,
        val catCode: String?,
        val catNme: String? = null,
        val catalogs: List<Catalog>? = null,
        val categoryPath: String? = null,
        var ctn: String? = null,
        val descriptor: String? = null,
        val domain: String? = null,
        val dtn: String? = null,
        val eop: String? = null,
        val familyName: String? = null,
        val filterKeys: List<String>? = null,
        val grpCode: String? = null,
        val grpNme: String? = null,
        val gtin: String? = null,
        var imageURL: String? = null,
        val isDeleted: Boolean,
        val leafletUrl: String? = null,
        val locale: String? = null,
        val marketingTextHeader: String? = null,
        val price: SummaryPrice? = null,
        val priority: Int,
        val productAlias: List<String>? = null,
        val productPagePath: String? = null,
        val productStatus: String? = null,
        var productTitle: String? = null,
        val productType: String? = null,
        val productURL: String? = null,
        val rank: Int,
        val reviewStatistics: ReviewStatistics? = null,
        val showOnlySupport: Boolean? = null,
        val somp: String? = null,
        val sop: String? = null,
        val subCatRank: Int,
        val subWOW: String? = null,
        val subcatCode: String? = null,
        val subcategory: String? = null,
        val subcategoryName: String? = null,
        val versions: List<String>? = null,
        val wow: String? = null
) : Parcelable