package com.philips.cdp.prxclient.datamodels.summary

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import org.json.JSONObject
import java.util.*

/**
 * Created by philips on 2/28/19.
 */
class PRXSummaryListResponse : ResponseData {
    /**
     *
     * @return
     * The success
     */
    /**
     *
     * @param success
     * The success
     */
    @SerializedName("success")
    @Expose
    var isSuccess = false

    /**
     *
     * @return
     * The data
     */
    /**
     *
     * @param data
     * The data
     */
    @SerializedName("data")
    @Expose
    var data: ArrayList<Data>? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param data
     * @param success
     */
    constructor(success: Boolean, data: ArrayList<Data>?) {
        isSuccess = success
        this.data = data
    }

    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), PRXSummaryListResponse::class.java)
        } else null
    }
}