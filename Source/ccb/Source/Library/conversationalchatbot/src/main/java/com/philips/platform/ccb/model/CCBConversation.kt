package com.philips.platform.ccb.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class CCBConversation(
        var token: String?,
        var conversationId: String?,
        var streamUrl: String,
        var expires_in: Int?,
        var referenceGrammarId: String?
)
