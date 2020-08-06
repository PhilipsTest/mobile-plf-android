package com.philips.cdp.prxclient.datamodels.specification

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import org.json.JSONObject
import java.io.Serializable

class SpecificationModel : ResponseData(), Serializable {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    @SerializedName("success")
    @Expose
    var success = false
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

    companion object {
        private const val serialVersionUID = -4223759915273396007L
    }
}