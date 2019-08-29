package com.philips.cdp.di.ecs.request

import android.util.Pair
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.retailers.WebResults
import com.philips.cdp.di.ecs.util.ECSConfig
import com.philips.cdp.di.ecs.error.ECSNetworkError
import org.json.JSONObject
import java.util.HashMap

open class GetRetailersInfoRequest (ecsCallback: ECSCallback<WebResults,Exception>, ctn :String) :OAuthAppInfraAbstractRequest() , Response.Listener<JSONObject> {

    val PREFIX_RETAILERS = "www.philips.com/api/wtb/v1"
    val RETAILERS_ALTER = "online-retailers?product=%s&lang=en"
    val PRX_SECTOR_CODE = "B2C"

    val callBack = ecsCallback
    val ctn = ctn
    override fun getURL(): String {
        return createURL()
    }

    fun createURL():String{
            val builder = StringBuilder("https://")
            builder.append(PREFIX_RETAILERS).append("/")
            builder.append(PRX_SECTOR_CODE).append("/")
            builder.append(ECSConfig.INSTANCE.locale).append("/")
            builder.append(RETAILERS_ALTER)
            return String.format(builder.toString(), ctn)
    }

    override fun onErrorResponse(error: VolleyError?) {
        val ecsError = ECSNetworkError.getErrorLocalizedErrorMessage(error)
        callBack.onFailure(ecsError.exception, ecsError.errorcode)
    }

    override fun getMethod(): Int {
        return Request.Method.GET
    }

    override fun getHeader(): Map<String, String>? {
        val authMap = HashMap<String, String>()
        authMap["Authorization"] = "Bearer " + ECSConfig.INSTANCE.accessToken
        return authMap
    }

    override fun onResponse(response: JSONObject?) {

        try{
           val webResults = Gson().fromJson(response.toString(),
                    WebResults::class.java)
            callBack.onResponse(webResults)
        }catch (exception:Exception){
            
        }

    }

    override fun getJSONSuccessResponseListener(): Response.Listener<JSONObject> {
        return this
    }


}