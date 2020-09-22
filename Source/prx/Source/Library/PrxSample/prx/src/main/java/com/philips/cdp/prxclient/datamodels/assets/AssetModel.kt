package com.philips.cdp.prxclient.datamodels.assets

import android.os.Parcelable
import com.google.gson.Gson
import com.philips.cdp.prxclient.response.ResponseData
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

/**
 * Description :
 * Project : PRX Common Component.
 */
@Parcelize
data class AssetModel(var isSuccess: Boolean = true, var data: Data? = null) : Parcelable, ResponseData() {
    override fun parseJsonResponseData(jsonResponse: JSONObject?): ResponseData? {
        return if (jsonResponse != null) {
            Gson().fromJson(jsonResponse.toString(), AssetModel::class.java)
        } else null
    }
}