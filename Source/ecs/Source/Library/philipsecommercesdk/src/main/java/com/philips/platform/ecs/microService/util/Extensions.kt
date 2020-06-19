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
import org.json.JSONException
import org.json.JSONObject

fun<T> JSONObject.getData(classOfT: Class<T>) : T?{
    try {
        return Gson().fromJson(this.toString(),classOfT)
    }catch (e : Exception){
        return null
    }

}

fun String.addQueryParam( key:String, param :String ) : String{
    var sb  = StringBuilder (this)
    sb.append("&")
    sb.append(key)
    sb.append("=")
    sb.append(param)
    return  sb.toString()
}

fun String.replaceParam(replaceMap:Map<String,String>): String{

    var convertedString = this

    for(entries in replaceMap.entries){
        val key = entries.key
        val value = entries.value
        convertedString =  convertedString.replace("%$key%", value)
    }
    return convertedString
}

fun VolleyError.getJsonError():JSONObject?{
    var errorJSON :JSONObject? = null
    val data = this.networkResponse?.data
    println("byte array : "+data)

    data?.let {
        try {
            val encodedString = Base64.encodeToString(it, Base64.DEFAULT)
            val decode: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            val errorString = String(decode)

            println("error json string : " + data)
            errorJSON = JSONObject(errorString)
        }catch (exception : JSONException){
            return errorJSON
        }
    }
    return errorJSON
}
