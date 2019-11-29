package com.philips.cdp.di.ecs.orderHistory

import com.android.volley.VolleyError
import com.philips.cdp.di.ecs.TestUtil
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory
import com.philips.cdp.di.ecs.request.GetOrderHistoryRequest
import org.json.JSONException
import org.json.JSONObject

class MockGetOrderHistoryRequest(jsonFile: String, currentPage: Int, ecsCallback: ECSCallback<ECSOrderHistory, Exception>) : GetOrderHistoryRequest(currentPage,2, ecsCallback) {


    internal var jsonFile: String = jsonFile

    override fun executeRequest() {
        var result: JSONObject? = null
        val `in` = javaClass.classLoader!!.getResourceAsStream(jsonFile)
        val jsonString = TestUtil.loadJSONFromFile(`in`)
        try {
            result = JSONObject(jsonString)
        } catch (e: JSONException) {
            e.printStackTrace()
            val volleyError = VolleyError(e.message)
            onErrorResponse(volleyError)
        }

        onResponse(result)
    }
}