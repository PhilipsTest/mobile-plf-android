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
package com.philips.platform.ecs.microService.model.asset

import android.os.Parcelable
import com.philips.platform.ecs.microService.prx.PRXImageAssetFilter

import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
/**
 * The type Assets contains list of assets
 */
@Parcelize
data class Assets(var asset: List<Asset> = ArrayList()) : Parcelable {


    fun getValidPRXImageAssets() : List<Asset>{
      return PRXImageAssetFilter().getValidPRXAssets(asset)
    }

}