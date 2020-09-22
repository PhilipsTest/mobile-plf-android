package com.philips.cdp.prxclient.datamodels.summary

import android.os.Parcelable
import com.google.gson.Gson
import com.philips.cdp.prxclient.response.ResponseData
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

/**
 * Description :
 * Project : PRX Common Component.
 */
@Parcelize
data class SummaryModel(var isSuccess: Boolean = true, var data: Data? = null) : Parcelable, ResponseData() {

    override fun parseJsonResponseData(response: JSONObject?): ResponseData? {
        return if (response != null) {
            Gson().fromJson(response.toString(), SummaryModel::class.java)
        } else null /*else
       {
		    mSummaryModel = new Gson().fromJson(summaryResponse, SummaryModel.class);
	   }*/
    }
}