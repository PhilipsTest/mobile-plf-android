package com.philips.cdp.prxclient.datamodels.Disclaimer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Data(var disclaimers: @RawValue Disclaimers? = null) : Parcelable