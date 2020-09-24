package com.philips.cdp.prxclient.datamodels.Disclaimer

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Disclaimers(@SerializedName("disclaimer") var disclaimerList: List<Disclaimer>?) : Parcelable