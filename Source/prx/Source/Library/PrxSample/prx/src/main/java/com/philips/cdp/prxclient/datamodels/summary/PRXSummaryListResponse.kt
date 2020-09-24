package com.philips.cdp.prxclient.datamodels.summary

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.philips.cdp.prxclient.response.ResponseData
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.util.*

/**
 * Created by philips on 2/28/19.
 */
@Parcelize
data class PRXSummaryListResponse(@SerializedName("success") var isSuccess: Boolean? = null,val invalidCtns: List<String>? = null, var data: ArrayList<Summary>? = null, val failureReason:String? = null) : Parcelable, ResponseData() {
    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), PRXSummaryListResponse::class.java)
        } else null
    }
}