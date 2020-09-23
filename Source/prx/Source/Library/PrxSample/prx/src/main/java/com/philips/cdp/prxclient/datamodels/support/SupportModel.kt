package com.philips.cdp.prxclient.datamodels.support

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import org.json.JSONObject

/**
 * Support Model.
 */
class SupportModel(@SerializedName("success") var isSuccess: Boolean? = null, var data: Data? = null) : ResponseData() {
    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), SupportModel::class.java)
        } else null
    }
}