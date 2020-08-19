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
import org.json.JSONObject


data class CCBActivities(
        val suggestedActions: CCBSuggestedActions?,
        val channelId: String,
        val entities: List<Any>?,
        val id: String,
        val replyToId: String?,
        val text: String,
        val timestamp: String,
        val type: String
)