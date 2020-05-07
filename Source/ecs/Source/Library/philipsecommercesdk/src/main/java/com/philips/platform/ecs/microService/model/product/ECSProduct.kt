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
import com.google.gson.Gson
import com.philips.platform.ecs.microService.model.ResponseData
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
class ECSProduct() : Parcelable ,ResponseData {

    var attributes: Attributes? = null
    var id: String? = null
    val type: String? = null


    override fun parseJsonResponseData(response: JSONObject?): ECSProduct? {
        return if (response != null) {
            Gson().fromJson(response.toString(), ECSProduct::class.java)
        } else null
    }

    fun JSONObject.getData(Class<T> classOfT)

}