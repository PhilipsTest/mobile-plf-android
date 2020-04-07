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
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants
import com.philips.platform.pif.DataInterface.USR.enums.Error
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener
import java.util.*


class HybrisAuth {


    companion object {

        const val KEY_MEC_EMAIL = "mec_email_id"
        const val KEY_MEC_AUTH_DATA = "mec_auth_data"


        private  val sse = SecureStorageInterface.SecureStorageError()

        private fun getOAuthInput(): ECSOAuthProvider {
            return object : ECSOAuthProvider() {
                override fun getOAuthID(): String? {

                    return getAccessToken()
                }

                override fun getClientType(): ClientType {
                    if(MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return ClientType.OIDC else return ClientType.JANRAIN
                }

                override fun getGrantType(): GrantType {
                    if(MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return GrantType.OIDC else return GrantType.JANRAIN
                }
            }
        }

        private fun getRefreshOAuthInput() : ECSOAuthProvider{

            return object : ECSOAuthProvider() {
                override fun getOAuthID(): String? {
                    return MECDataHolder.INSTANCE.refreshToken
                }

                override fun getClientType(): ClientType {
                    if(MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return ClientType.OIDC
                    return super.getClientType()
                }

                override fun getGrantType(): GrantType {
                    if(MECDataHolder.INSTANCE.userDataInterface.isOIDCToken) return GrantType.OIDC
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
                e.printStackTrace()
            }
            return null
        }


        fun hybrisAuthentication(fragmentCallback: ECSCallback<ECSOAuthData, Exception>) {

            val hybrisCallback = object : ECSCallback<ECSOAuthData, Exception> {

                override fun onResponse(result: ECSOAuthData?) {


                    val map = HashMap<String,String>()
                    map[KEY_MEC_EMAIL] = MECDataHolder.INSTANCE.getUserInfo().email

                    var jsonString = getJsonStringOfMap(map)
                    MECDataHolder.INSTANCE.refreshToken = result?.refreshToken!!
                    MECDataHolder.INSTANCE.appinfra.secureStorage.storeValueForKey(KEY_MEC_AUTH_DATA,jsonString,sse) //TODO handle sse error
                    fragmentCallback.onResponse(result)
                }

                override fun onFailure(error: Exception?, ecsError: ECSError?) {
                    if (MECutility.isAuthError(ecsError)){
                        refreshJainrain(fragmentCallback);
                    } else {
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

                    MECDataHolder.INSTANCE.refreshToken = result?.refreshToken!!
                    fragmentCallback.onResponse(result) // send call back to fragment or view

                }

                override fun onFailure(error: Exception?, ecsError: ECSError?) {
                    if (MECutility.isAuthError(ecsError)) {
                        refreshJainrain(fragmentCallback);
                    } else {

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
                    hybrisAuthentication(fragmentCallback)
                }

                override fun refreshSessionFailed(error: Error?) {
                    val ecsError = ECSError(5000, ECSErrorEnum.ECSinvalid_grant.name)
                    val exception = java.lang.Exception()
                    fragmentCallback.onFailure(exception, ecsError)
                }

                override fun forcedLogout() {
                   // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }
            MECDataHolder.INSTANCE.userDataInterface.refreshSession(refreshSessionListener)
        }

    }


}