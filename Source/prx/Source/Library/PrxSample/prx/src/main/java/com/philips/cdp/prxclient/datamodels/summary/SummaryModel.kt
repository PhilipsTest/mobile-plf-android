package com.philips.cdp.prxclient.datamodels.summary

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

/**
 * Description :
 * Project : PRX Common Component.
 */
@Parcelize
data class SummaryModel(@SerializedName("success") var isSuccess: Boolean? = null, var data: Data? = null) : Parcelable, ResponseData() {

    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        var responseData: ResponseData? = null
        if (response != null) {
            try {
                responseData = Gson().fromJson(response.toString(), SummaryModel::class.java)
            } catch (e: JsonParseException) {
            }
        }
        return responseData
    }
}