package com.philips.cdp.prxclient.datamodels.support

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import org.json.JSONObject

/**
 * Support Model.
 */
class SupportModel(var isSuccess: Boolean = false, var data: Data? = null) : ResponseData() {

    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), SupportModel::class.java)
        } else null
    }
}