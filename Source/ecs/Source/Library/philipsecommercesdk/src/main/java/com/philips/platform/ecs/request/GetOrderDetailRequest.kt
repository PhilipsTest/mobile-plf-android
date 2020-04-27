/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.request


import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.ecs.store.ECSURLBuilder
import com.philips.platform.ecs.util.ECSConfiguration
import com.philips.platform.ecs.error.ECSNetworkError
import com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap

open class GetOrderDetailRequest(orderID: String, ecsCallback: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderDetail, Exception>) : com.philips.platform.ecs.request.OAuthAppInfraAbstractRequest(), Response.Listener<JSONObject> {

    val orderID = orderID
    val ecsCallback = ecsCallback

    override fun onErrorResponse(error: VolleyError?) {
        val ecsErrorWrapper = com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage(error,this)
        ecsCallback.onFailure(ecsErrorWrapper.exception, ecsErrorWrapper.ecsError)
    }

    override fun getURL(): String {
        return com.philips.platform.ecs.store.ECSURLBuilder().getOrderDetailUrl(orderID)
    }

    override fun getHeader(): Map<String, String>? {
        val authMap = HashMap<String, String>()
        authMap["Authorization"] = "Bearer " + com.philips.platform.ecs.util.ECSConfiguration.INSTANCE.accessToken
        return authMap
    }

    override fun getMethod(): Int {
        return Request.Method.GET
    }


    override fun onResponse(response: JSONObject?) {

        try {
            val orderDetail = Gson().fromJson(response.toString(), com.philips.platform.ecs.model.orders.ECSOrderDetail::class.java)
            ecsCallback.onResponse(orderDetail)
        } catch (e: Exception) {
            val ecsErrorWrapper = getErrorLocalizedErrorMessage(com.philips.platform.ecs.error.ECSErrorEnum.ECSsomethingWentWrong, e, response.toString())
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.ecsError)
        }
    }

    override fun getJSONSuccessResponseListener(): Response.Listener<JSONObject> {
        return this;
    }
}