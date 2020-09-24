/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.ecs.microService.model.summary

import android.os.Parcelable
import com.philips.cdp.prxclient.datamodels.summary.Summary
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ECSProductSummary(
        val `data`: List<Summary>?,
        val invalidCtns: List<String>?,
        val success: Boolean,
        val failureReason:String?
):Parcelable