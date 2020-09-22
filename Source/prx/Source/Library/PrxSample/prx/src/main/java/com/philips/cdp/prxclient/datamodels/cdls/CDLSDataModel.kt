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
import com.google.gson.Gson
import com.philips.cdp.prxclient.response.ResponseData
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
data class CDLSDataModel(val isSuccess: Boolean = true, var data: Data? = null) : Parcelable, ResponseData() {
    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), CDLSDataModel::class.java)
        } else null
    }
}