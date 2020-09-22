package com.philips.cdp.prxclient.datamodels.features

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
data class FeaturesModel(@SerializedName("success") var success: Boolean?=null, var data: Data? = null) : ResponseData(), Parcelable {
    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), FeaturesModel::class.java)
        } else null
    }
}