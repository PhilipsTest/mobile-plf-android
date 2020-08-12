package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class CsItemItem(
        var csItemRank: String? = null,
        var unitOfMeasure: UnitOfMeasure? = null,
        var csItemName: String? = null,
        var csItemCode: String? = null,
        var csItemIsFreeFormat: String? = null,
        var csValue: List<CsValueItem>? = null

) : Parcelable