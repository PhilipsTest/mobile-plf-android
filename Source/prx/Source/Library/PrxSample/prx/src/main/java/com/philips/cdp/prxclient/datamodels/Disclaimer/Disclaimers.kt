package com.philips.cdp.prxclient.datamodels.Disclaimer

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Disclaimers (var disclaimer: List<Disclaimer> = ArrayList()
):Parcelable