package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnitOfMeasure(var unitOfMeasureSymbol: String? = null, var unitOfMeasureName: String? = null, var unitOfMeasureCode: String? = null) : Parcelable