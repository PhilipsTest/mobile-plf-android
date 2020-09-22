package com.philips.cdp.prxclient.datamodels.Disclaimer

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
data class DisclaimerModel(@SerializedName("success") var isSuccess: Boolean? = null, var data: Data? = null) : Parcelable, ResponseData() {
    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), DisclaimerModel::class.java)
        } else null
    }
}