package com.philips.cdp.prxclient.datamodels

import com.google.gson.Gson
import org.json.JSONObject

fun <T> JSONObject.getData(classOfT: Class<T>): T? {
    try {
        return Gson().fromJson(this.toString(), classOfT)
    } catch (e: Exception) {
        return null
    }
}