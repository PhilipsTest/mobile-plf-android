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
import kotlinx.android.parcel.Parcelize

@Parcelize
class AssetModel(val success :Boolean = false,val data: Data?):Parcelable