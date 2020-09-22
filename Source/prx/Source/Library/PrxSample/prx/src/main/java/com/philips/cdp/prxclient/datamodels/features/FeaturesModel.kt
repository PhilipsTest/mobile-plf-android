package com.philips.cdp.prxclient.datamodels.features

import android.os.Parcelable
import com.google.gson.Gson
import com.philips.cdp.prxclient.response.ResponseData
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
data class FeaturesModel(var success: Boolean = true, var data: Data? = null) : ResponseData(), Parcelable {
    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), FeaturesModel::class.java)
        } else null
    }
}