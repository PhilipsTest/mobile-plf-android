package com.philips.platform.ccb.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class CCBSuggestedActions(
        val actions: List<CCBActions>
)