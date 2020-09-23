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
package com.philips.cdp.prxclient.datamodels.cdls

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatData(
        var openingHoursWeekdays: String? = null,
        var openingHoursSaturday: String? = null,
        var content: String? = null) : Parcelable
