package com.philips.cdp.prxclient.datamodels.specification

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
class SpecificationModel(@SerializedName("success") var success: Boolean? = null, var data: Data? = null) : ResponseData(), Parcelable {
    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        var responseData: ResponseData? = null
        if (response != null) {
            try {
                responseData = Gson().fromJson(response.toString(), SpecificationModel::class.java)
            } catch (e: JsonParseException) {
            }
        }
        return responseData
    }
}