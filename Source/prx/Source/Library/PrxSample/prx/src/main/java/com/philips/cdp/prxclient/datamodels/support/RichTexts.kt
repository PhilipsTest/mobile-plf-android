package com.philips.cdp.prxclient.datamodels.support

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Rich Texts.
 */
@Parcelize
data class RichTexts(var richText: List<RichText> = ArrayList()) : Parcelable