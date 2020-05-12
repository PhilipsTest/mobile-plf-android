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

package com.philips.platform.ecs.microService.prx

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PRXError(
    val ERROR: ERROR
):Parcelable