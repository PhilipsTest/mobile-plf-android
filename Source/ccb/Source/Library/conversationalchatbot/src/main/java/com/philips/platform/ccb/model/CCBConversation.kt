package com.philips.platform.ccb.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CCBConversation(val token: String?, val conversationId: String?, val streamUrl: String?, val expires_in: Int?, val referenceGrammarId: String?) : Parcelable
