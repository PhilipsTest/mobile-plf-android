/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.auth

import com.google.gson.Gson
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.error.ECSErrorEnum
import com.philips.cdp.di.ecs.integration.ClientType
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.integration.ECSOAuthProvider
import com.philips.cdp.di.ecs.integration.GrantType
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.platform.appinfra.securestorage.SecureStorageInterface
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants
import com.philips.platform.pif.DataInterface.USR.enums.Error
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener
import java.util.*


class HybrisAuth {


    companion object {
        private val TAG = "HybrisAuth"

        const val KEY_MEC_EMAIL = "mec_email_id"
        const val KEY_MEC_AUTH_DATA = "mec_auth_data"


        private val sse = SecureStorageInterface.SecureStorageError()

        private fun getOAuthInput(): ECSOAuthProvider {
            return object : ECSOAuthProvider() {
                override fun getOAuthID(): String? {
                    return getAccessToken()
                }

                override fun getClientType(): ClientType {
                    return if (MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) ClientType.OIDC else ClientType.JANRAIN
                }

                override fun getGrantType(): GrantType {
                    if (MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return GrantType.OIDC else return GrantType.JANRAIN
                }
            }
        }

        private fun getRefreshOAuthInput(): ECSOAuthProvider {

            return object : ECSOAuthProvider() {
                override fun getOAuthID(): String? {
                    MECLog.d(TAG, "getRefreshOAuthInput  : " + MECDataHolder.INSTANCE.refreshToken)
                    return MECDataHolder.INSTANCE.refreshToken
                }

                override fun getClientType(): ClientType {
                    if (MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return ClientType.OIDC
                    return super.getClientType()
                }

                override fun getGrantType(): GrantType {
                    if (MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return GrantType.OIDC
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
                    MECDataHolder.INSTANCE.appinfra.secureStorage.storeValueForKey(KEY_MEC_AUTH_DATA, jsonString, sse) //TODO handle sse error
                    MECLog.d(TAG, "hybrisAuthentication onResponse ")
                    fragmentCallback.onResponse(result)
                }

                override fun onFailure(error: Exception?, ecsError: ECSError?) {
                    MECLog.d(TAG, "hybrisAuthentication : onFailure : " + error!!.message + " ECS Error code " + ecsError!!.errorcode + "ECS Error type " + ecsError!!.errorType)
                    if (MECutility.isAuthError(ecsError) || ecsError.errorcode == ECSErrorEnum.ECSsomethingWentWrong.errorCode) {
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

                override fun onFailure(error: Exception?, ecsError: ECSError?) {
                    MECLog.d(TAG, "hybrisRefreshAuthentication : onFailure : " + error!!.message + " ECS Error code " + ecsError!!.errorcode + "ECS Error type " + ecsError!!.errorType)
                    if (MECutility.isAuthError(ecsError) || ecsError.errorcode == ECSErrorEnum.ECSsomethingWentWrong.errorCode) {
                        refreshJainrain(fragmentCallback);
                    } else {
                        MECLog.d(TAG, "hybrisRefreshAuthentication : onFailure : not OAuthError")
                        ECSConfiguration.INSTANCE.setAuthToken(null)
                        fragmentCallback.onFailure(error, ecsError)
                    }
                }
            }
            MECDataHolder.INSTANCE.eCSServices.hybrisRefreshOAuth(getRefreshOAuthInput(), hybrisCallback)
        }


        fun refreshJainrain(fragmentCallback: ECSCallback<ECSOAuthData, Exception>) {
            val refreshSessionListener = object : RefreshSessionListener {
                override fun refreshSessionSuccess() {
                    MECLog.d(TAG, "refreshJainrain : refreshSessionSuccess")
                    hybrisAuthentication(fragmentCallback)
                }

                override fun refreshSessionFailed(error: Error?) {
                    MECLog.d(TAG, "refreshJainrain : refreshSessionFailed :" + error!!.errCode)
                    val ecsError = ECSError(5000, ECSErrorEnum.ECSinvalid_grant.name)
                    val exception = java.lang.Exception()
                    fragmentCallback.onFailure(exception, ecsError)
                }

                override fun forcedLogout() {
                    MECLog.d(TAG, "refreshJainrain : forcedLogout ")
                    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }
            MECDataHolder.INSTANCE.userDataInterface.refreshSession(refreshSessionListener)
        }

    }


}