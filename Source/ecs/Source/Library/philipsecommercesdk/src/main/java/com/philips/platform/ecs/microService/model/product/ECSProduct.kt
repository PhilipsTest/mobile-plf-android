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

package com.philips.platform.ecs.microService.model.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.datamodels.Disclaimer.Disclaimers
import kotlinx.android.parcel.Parcelize
import com.philips.cdp.prxclient.datamodels.assets.Assets
import com.philips.cdp.prxclient.datamodels.summary.Summary

@Parcelize
data class ECSProduct(var attributes: Attributes? = null, @SerializedName("id") var ctn: String, var type: String? = null) : Parcelable {

    var summary: Summary? = null
    var assets: Assets? = null
    var disclaimers: Disclaimers? = null

}