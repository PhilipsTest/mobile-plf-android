package com.philips.cdp.di.mec.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.error.ECSErrorEnum
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.integration.ECSOAuthProvider
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.cdp.di.mec.integration.MecHolder
import com.philips.cdp.di.mec.utils.MECDataHolder
import com.philips.platform.appinfra.securestorage.SecureStorageInterface

import com.philips.platform.pif.DataInterface.USR.UserDetailConstants
import com.philips.platform.pif.DataInterface.USR.enums.Error
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener
import java.util.ArrayList


class HybrisAuth {



    companion object {

       private  val refreshTokenKey:String ="hybrisRefreshTokenKey"

        fun getJanrainAuthInput(): ECSOAuthProvider {
            val oAuthInput = object : ECSOAuthProvider() {
                override fun getOAuthID(): String? {
                    return getMyJanRainID()
                }
            }
            return oAuthInput
        }

        fun getMyJanRainID(): String? {
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
                    ECSConfiguration.INSTANCE.setAuthToken(result!!.accessToken)
                    MECDataHolder.INSTANCE.refreshToken = result.refreshToken
                    Log.d("HYBRIS AUTH succ", result.accessToken)
                    /* val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
                     val editor: SharedPreferences.Editor =  sharedPreferences.edit()*/
                    fragmentCallback.onResponse(result) // send call back to fragment or view

                }


                override fun onFailure(error: Exception?, ecsError: ECSError?) {

                    if (ecsError!!.errorcode == ECSErrorEnum.ECSInvalidTokenError.errorCode
                            || ecsError!!.errorcode == ECSErrorEnum.ECSinvalid_grant.errorCode
                            || ecsError!!.errorcode == ECSErrorEnum.ECSinvalid_client.errorCode) {
                        refreshJainrain(fragmentCallback);
                    } else {

                        fragmentCallback.onFailure(error, ecsError)
                    }
                }
            }

            MecHolder.INSTANCE.eCSServices.hybrisOAthAuthentication(getJanrainAuthInput(), hybrisCallback)
        }


        fun hybrisRefreshAuthentication(fragmentCallback: ECSCallback<ECSOAuthData, Exception>) {

            val oAuthInput = object : ECSOAuthProvider() {
                override fun getOAuthID(): String? {

                    return MECDataHolder.INSTANCE.refreshToken
                }

            }
            val hybrisCallback = object : ECSCallback<ECSOAuthData, Exception> {
                override fun onResponse(result: ECSOAuthData?) {
                    ECSConfiguration.INSTANCE.setAuthToken(result!!.accessToken)
                    MECDataHolder.INSTANCE.refreshToken = result.refreshToken
                    Log.d("HYBRIS AUTH succ", result.accessToken)
                    fragmentCallback.onResponse(result) // send call back to fragment or view

                }

                override fun onFailure(error: Exception?, ecsError: ECSError?) {

                    if (ecsError!!.errorcode == ECSErrorEnum.ECSInvalidTokenError.errorCode
                            || ecsError!!.errorcode == ECSErrorEnum.ECSinvalid_grant.errorCode
                            || ecsError!!.errorcode == ECSErrorEnum.ECSinvalid_client.errorCode) {
                        refreshJainrain(fragmentCallback);
                    } else {

                        ECSConfiguration.INSTANCE.setAuthToken(null)
                        fragmentCallback.onFailure(error, ecsError)
                    }
                }
            }
            MecHolder.INSTANCE.eCSServices.hybrisRefreshOAuth(oAuthInput, hybrisCallback)
        }


        fun refreshJainrain(fragmentCallback: ECSCallback<ECSOAuthData, Exception>) {
            val refreshSessionListener = object : RefreshSessionListener {
                override fun refreshSessionSuccess() {
                    //re OAuth after refreshSession for janrain
                    /*  val oAuthInput = object : ECSOAuthProvider() {
                          override fun getOAuthID(): String {
                              return getMyJanRainID().toString()
                          }

                      }*/

                    //ReOAuth starts =======================
                    MecHolder.INSTANCE.eCSServices.hybrisOAthAuthentication(getJanrainAuthInput(), object : ECSCallback<ECSOAuthData, Exception> {
                        override fun onResponse(result: ECSOAuthData) {
                            ECSConfiguration.INSTANCE.setAuthToken(result.accessToken)
                            MECDataHolder.INSTANCE.refreshToken = result.refreshToken
                            Log.d("ECS succ", result.accessToken)
                            fragmentCallback.onResponse(result)

                            try {
                                //todo mIapInterface.getProductCartCount(this@EcsDemoAppActivity)
                            } catch (e: Exception) {

                            }
                        }

                        override fun onFailure(error: Exception, ecsError: ECSError) {
                            Log.d("ECS Oauth failed", error.message + " :  " + ecsError)
                            ECSConfiguration.INSTANCE.setAuthToken(null)
                            fragmentCallback.onFailure(error, ecsError)

                        }
                    })

                    // ReOAuth ends  =====================

                }

                override fun refreshSessionFailed(error: Error?) {
                    TODO("not implemented if server failed to respond") //To change body of created functions use File | Settings | File Templates.
                }

                override fun forcedLogout() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }
            MECDataHolder.INSTANCE.userDataInterface.refreshSession(refreshSessionListener)
        }


        fun authHybrisIfNotAlready() {
            // if Hybris is enabled and user logged in
            if (MECDataHolder.INSTANCE.hybrisEnabled && MECDataHolder.INSTANCE.userDataInterface != null && MECDataHolder.INSTANCE.userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                if (null == MECDataHolder.INSTANCE.refreshToken) {
                    val sse = SecureStorageInterface.SecureStorageError() // to get error code if any
                    val hybrisRrefreshToken: String? = MECDataHolder.INSTANCE.appinfra.secureStorage.fetchValueForKey(refreshTokenKey, sse)
                    if (null == sse.errorCode && null != hybrisRrefreshToken) {
                        //if refresh token is already saved in device secure storage and save it to instance
                        MECDataHolder.INSTANCE.refreshToken = hybrisRrefreshToken
                    } else {
                        // if refresh token is NOT already present then call Auth Hybris
                        val hybrisAuthCallback = object : ECSCallback<ECSOAuthData, Exception> {
                            override fun onResponse(eCSOAuthData: ECSOAuthData?) {
                                MECDataHolder.INSTANCE.refreshToken = eCSOAuthData!!.refreshToken
                                val sse = SecureStorageInterface.SecureStorageError() // to get error code if any
                                MECDataHolder.INSTANCE.appinfra.secureStorage.storeValueForKey(refreshTokenKey, eCSOAuthData!!.refreshToken, sse)
                            }

                            override fun onFailure(error: Exception?, ecsError: ECSError?) {
                                // control should not come here in normal circumstances
                            }
                        }
                        hybrisAuthentication(hybrisAuthCallback)

                    }
                }

            }

        }
    }


}