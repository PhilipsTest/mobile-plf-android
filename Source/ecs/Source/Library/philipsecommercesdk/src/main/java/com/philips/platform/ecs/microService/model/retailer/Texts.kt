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

package com.philips.platform.ecs.microService.model.retailer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Texts(
    val Text: List<Text>?
):Parcelable