/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.ecs.microService.util

import android.util.Base64
import com.android.volley.VolleyError
import com.google.gson.Gson
import org.json.JSONObject

fun<T> JSONObject.getData(classOfT: Class<T>) : T{
    return Gson().fromJson(this.toString(),classOfT)
}

fun String.replaceParam(replaceMap:Map<String,String>): String{

    var convertedString = this

    for(entries in replaceMap.entries){
        var key = entries.key
        var value = entries.value
        convertedString =  convertedString.replace("%$key%", value)
    }
    return convertedString
}

fun VolleyError.getJsonError():JSONObject?{
    var errorJSON :JSONObject? = null
    var data = this.networkResponse?.data
    data?.let {
        val encodedString = Base64.encodeToString(it, Base64.DEFAULT)
        val decode: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
        var errorString = String(decode)
        JSONObject(errorString)
    }
    return errorJSON
}
