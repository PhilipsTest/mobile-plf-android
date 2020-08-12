package com.philips.cdp.prxclient.datamodels.features

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import org.json.JSONObject
import java.io.Serializable

class FeaturesModel : ResponseData(), Serializable {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    @SerializedName("success")
    @Expose
    var success = false
    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), FeaturesModel::class.java)
        } else null
    }

    companion object {
        private const val serialVersionUID = 5897313023261737994L
    }
}