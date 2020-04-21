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
import com.philips.platform.ecs.store.ECSURLBuilder
import com.philips.platform.ecs.error.ECSNetworkError
import com.philips.platform.ecs.model.address.ECSUserProfile
import org.json.JSONObject

open class GetUserProfileRequest(ecsCallback: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.address.ECSUserProfile, Exception>) : com.philips.platform.ecs.request.OAuthAppInfraAbstractRequest() , Response.Listener<JSONObject> {

    val ecsCallback = ecsCallback;

    override fun getURL(): String {
       return com.philips.platform.ecs.store.ECSURLBuilder().getUserUrl()
    }

    override fun onErrorResponse(error: VolleyError?) {
        val ecsErrorWrapper = com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage(error,this)
        ecsCallback.onFailure(ecsErrorWrapper.exception, ecsErrorWrapper.ecsError)
    }

    override fun getMethod(): Int {
        return Request.Method.GET
    }

    override fun onResponse(response: JSONObject?) {

        try{
           val userProfile = Gson().fromJson(response.toString(),
                    com.philips.platform.ecs.model.address.ECSUserProfile::class.java)
            ecsCallback.onResponse(userProfile)
        }catch (e :Exception){
            val ecsErrorWrapper = com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage(com.philips.platform.ecs.error.ECSErrorEnum.ECSsomethingWentWrong, e, response.toString())
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.ecsError)
        }
    }

    override fun getJSONSuccessResponseListener(): Response.Listener<JSONObject> {
        return this
    }
}