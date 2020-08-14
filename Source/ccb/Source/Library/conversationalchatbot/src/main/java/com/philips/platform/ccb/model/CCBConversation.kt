/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

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
