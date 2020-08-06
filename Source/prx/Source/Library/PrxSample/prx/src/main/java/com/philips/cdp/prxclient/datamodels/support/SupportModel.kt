package com.philips.cdp.prxclient.datamodels.support

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import org.json.JSONObject

/**
 * Support Model.
 */
class SupportModel : ResponseData() {
    /**
     * @return The success
     */
    /**
     * @param success The success
     */
    @SerializedName("success")
    @Expose
    var isSuccess = false

    /**
     * @return The data
     */
    /**
     * @param data The data
     */
    @SerializedName("data")
    @Expose
    var data: Data? = null

    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), SupportModel::class.java)
        } else null
    }
}