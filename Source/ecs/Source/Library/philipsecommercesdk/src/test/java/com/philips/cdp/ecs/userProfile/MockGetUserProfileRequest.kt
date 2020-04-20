package com.philips.cdp.ecs.userProfile

import com.android.volley.VolleyError
import com.philips.cdp.ecs.TestUtil
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.address.ECSUserProfile
import com.philips.cdp.ecs.request.GetUserProfileRequest
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class MockGetUserProfileRequest(jsonFileName:String, ecsCallback: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.address.ECSUserProfile, Exception>) : GetUserProfileRequest(ecsCallback) {

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