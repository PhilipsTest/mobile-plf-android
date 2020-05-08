/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.auth

import com.google.gson.Gson
import com.philips.platform.appinfra.securestorage.SecureStorageInterface
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.oauth.ECSOAuthData
import com.philips.platform.mec.analytics.MECAnalyticServer
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant
import com.philips.platform.mec.analytics.MECAnalyticsConstant.appError
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants
import com.philips.platform.pif.DataInterface.USR.enums.Error
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener
import java.util.*


class HybrisAuth {

    companion object {
        val TAG: String = HybrisAuth::class.java.simpleName

        const val KEY_MEC_EMAIL = "mec_email_id"
        const val KEY_MEC_AUTH_DATA = "mec_auth_data"


        private val sse = SecureStorageInterface.SecureStorageError()

        private fun getOAuthInput(): com.philips.platform.ecs.integration.ECSOAuthProvider {
            return object : com.philips.platform.ecs.integration.ECSOAuthProvider() {
                override fun getOAuthID(): String? {
                    return getAccessToken()
                }

                  override fun getClientID(): com.philips.platform.ecs.integration.ClientID {
                    if(MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return com.philips.platform.ecs.integration.ClientID.OIDC else return com.philips.platform.ecs.integration.ClientID.JANRAIN
                }

                override fun getGrantType(): com.philips.platform.ecs.integration.GrantType {
                    if (MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return com.philips.platform.ecs.integration.GrantType.OIDC else return com.philips.platform.ecs.integration.GrantType.JANRAIN
                }
            }
        }

        private fun getRefreshOAuthInput(): com.philips.platform.ecs.integration.ECSOAuthProvider {

            return object : com.philips.platform.ecs.integration.ECSOAuthProvider() {
                override fun getOAuthID(): String? {
                    MECLog.d(TAG, "getRefreshOAuthInput  : " + MECDataHolder.INSTANCE.refreshToken)
                    return MECDataHolder.INSTANCE.refreshToken
                }

                override fun getClientID(): com.philips.platform.ecs.integration.ClientID {
                    if(MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return com.philips.platform.ecs.integration.ClientID.OIDC
                    return super.getClientID()
                }

                override fun getGrantType(): com.philips.platform.ecs.integration.GrantType {
                    if (MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return com.philips.platform.ecs.integration.GrantType.OIDC
                    return super.getGrantType()
                }

            }
        }


        fun getAccessToken(): String? {
            val detailsKey = ArrayList<String>()
            detailsKey.add(UserDetailConstants.ACCESS_TOKEN)
            try {
                val userDetailsMap = MECDataHolder.INSTANCE.userDataInterface.getUserDetails(detailsKey)
                return userDetailsMap.get(UserDetailConstants.ACCESS_TOKEN)!!.toString()
            } catch (e: Exception) {
                MECLog.d(TAG, "Exception Occurred : " + e.message)
                MECAnalytics.trackTechnicalError(MECAnalyticsConstant.COMPONENT_NAME + ":" + appError + ":" + MECAnalyticServer.other + e.toString() + ":" + MECAnalyticsConstant.exceptionErrorCode)
            }
            return null
        }


        fun hybrisAuthentication(fragmentCallback: ECSCallback<ECSOAuthData, Exception>) {

            val hybrisCallback = object : ECSCallback<ECSOAuthData, Exception> {

                override fun onResponse(result: ECSOAuthData?) {
                    val map = HashMap<String, String>()
                    map[KEY_MEC_EMAIL] = MECDataHolder.INSTANCE.getUserInfo().email

                    val jsonString = getJsonStringOfMap(map)
                    MECDataHolder.INSTANCE.refreshToken = result?.refreshToken!!
                    MECDataHolder.INSTANCE.appinfra.secureStorage.storeValueForKey(KEY_MEC_AUTH_DATA,jsonString,sse)
                    if(sse.errorMessage != null && sse.errorCode!=null) {
                        MECAnalytics.trackTechnicalError(MECAnalyticsConstant.COMPONENT_NAME + ":" + appError+ ":" + MECAnalyticServer.other + sse.errorMessage + ":" + sse.errorCode)
                    }
                    fragmentCallback.onResponse(result)
                }

                override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
                    MECLog.d(TAG, "hybrisAuthentication : onFailure : " + error!!.message + " ECS Error code " + ecsError!!.errorcode + "ECS Error type " + ecsError!!.errorType)
                    if (MECutility.isAuthError(ecsError) ) {
                        refreshJainrain(fragmentCallback);
                    } else {
                        MECLog.d(TAG, "hybrisAuthentication : onFailure : not OAuthError")
                        fragmentCallback.onFailure(error, ecsError)
                    }
                }
            }

            MECDataHolder.INSTANCE.eCSServices.hybrisOAthAuthentication(getOAuthInput(), hybrisCallback)
        }

        private fun getJsonStringOfMap(map: HashMap<String, String>): String {
            return Gson().toJson(map)
        }


        fun hybrisRefreshAuthentication(fragmentCallback: ECSCallback<ECSOAuthData, Exception>) {

            val hybrisCallback = object : ECSCallback<ECSOAuthData, Exception> {
                override fun onResponse(result: ECSOAuthData?) {
                    MECLog.d(TAG, "hybrisRefreshAuthentication : onResponse : " + result!!.accessToken)
                    MECDataHolder.INSTANCE.refreshToken = result.refreshToken!!
                    fragmentCallback.onResponse(result) // send call back to fragment or view
                }

                override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
                    MECLog.d(TAG, "hybrisRefreshAuthentication : onFailure : " + error!!.message + " ECS Error code " + ecsError!!.errorcode + "ECS Error type " + ecsError!!.errorType)
                    if (MECutility.isAuthError(ecsError) ) {
                        refreshJainrain(fragmentCallback);
                    } else {
                        MECLog.d(TAG, "hybrisRefreshAuthentication : onFailure : not OAuthError")
                        com.philips.platform.ecs.util.ECSConfiguration.INSTANCE.setAuthToken(null)
                        fragmentCallback.onFailure(error, ecsError)
                    }
                }
            }
            MECDataHolder.INSTANCE.eCSServices.hybrisRefreshOAuth(getRefreshOAuthInput(), hybrisCallback)
        }


        fun refreshJainrain(fragmentCallback:ECSCallback<ECSOAuthData, Exception>) {
            val refreshSessionListener = object : RefreshSessionListener {
                override fun refreshSessionSuccess() {
                    MECLog.d(TAG, "refreshJainrain : refreshSessionSuccess")
                    hybrisAuthentication(fragmentCallback)
                }

                override fun refreshSessionFailed(error: Error?) {
                    MECLog.e(TAG, "refreshJainrain : refreshSessionFailed :" + error!!.errCode)
                    val ecsError = com.philips.platform.ecs.error.ECSError(5000, ECSErrorEnum.ECSinvalid_grant.name)
                    val exception = java.lang.Exception()
                    fragmentCallback.onFailure(exception, ecsError)
                }

                override fun forcedLogout() {
                    MECLog.e(TAG, "refreshJainrain : forcedLogout ")
                    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }
            MECDataHolder.INSTANCE.userDataInterface.refreshSession(refreshSessionListener)
        }

    }


}