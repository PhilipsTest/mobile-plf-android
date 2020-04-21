package com.philips.platform.ecs.orderHistory

import com.android.volley.VolleyError
import com.philips.platform.ecs.TestUtil
import com.philips.platform.ecs.request.GetOrderDetailRequest
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class MockGetOrderDetailRequest(jsonFileName:String,orderID: String, ecsCallback: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderDetail, Exception>) : GetOrderDetailRequest(orderID, ecsCallback) {

    internal var jsonFile: String = jsonFileName

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