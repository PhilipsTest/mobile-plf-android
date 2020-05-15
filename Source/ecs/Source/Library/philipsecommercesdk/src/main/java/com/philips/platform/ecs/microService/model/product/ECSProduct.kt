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
import com.philips.platform.ecs.microService.model.asset.Assets
import com.philips.platform.ecs.microService.model.disclaimer.Disclaimers
import com.philips.platform.ecs.microService.model.summary.Summary
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ECSProduct(var attributes: Attributes?,var id: String,var type: String?) : Parcelable{

    var summary: Summary? =null
    var assets: Assets?=null
    var disclaimers: Disclaimers?=null

    //TODO id to ctn
}