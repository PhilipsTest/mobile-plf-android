package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CsItemItem(
        var csItemRank: String? = null,
        var unitOfMeasure: UnitOfMeasure? = null,
        var csItemName: String? = null,
        var csItemCode: String? = null,
        var csItemIsFreeFormat: String? = null,
        var csValue: List<CsValueItem>? = null
) : Parcelable