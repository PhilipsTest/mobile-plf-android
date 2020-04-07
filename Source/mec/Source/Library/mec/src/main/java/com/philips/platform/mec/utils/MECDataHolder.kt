/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.utils

import com.android.volley.DefaultRetryPolicy
import com.bazaarvoice.bvandroidsdk.BVConversationsClient
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.model.config.ECSConfig
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.mec.integration.MECBannerConfigurator
import com.philips.platform.mec.integration.MECBazaarVoiceInput
import com.philips.platform.mec.integration.MECOrderFlowCompletion
import com.philips.platform.mec.screens.address.UserInfo
import com.philips.platform.mec.screens.payment.MECPayment
import com.philips.platform.mec.screens.payment.MECPayments
import com.philips.platform.pif.DataInterface.MEC.listeners.MECCartUpdateListener
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState
import com.philips.platform.uappframework.listener.ActionBarListener
import java.util.*

enum class MECDataHolder {

    INSTANCE;

    lateinit var appinfra: AppInfraInterface
    lateinit var actionbarUpdateListener: ActionBarListener
    var mecCartUpdateListener: MECCartUpdateListener? = null
    var mecBannerEnabler: MECBannerConfigurator? = null
    var mecOrderFlowCompletion: MECOrderFlowCompletion? = null
    lateinit var locale: String
    lateinit var propositionId: String
    lateinit var voucherCode: String
    var maxCartCount: Int = 0
    lateinit var userDataInterface: UserDataInterface
    var refreshToken: String = "UNKNOWN" //To avoid null check and Null pointer exception 
    var blackListedRetailers: List<String>? = null
    lateinit var mecBazaarVoiceInput: MECBazaarVoiceInput
    private var privacyUrl: String? = null
    private var faqUrl: String? = null
    private var termsUrl: String? = null
    var hybrisEnabled: Boolean = true
    var retailerEnabled: Boolean = true
    var voucherEnabled: Boolean = true
    var rootCategory: String = ""
    var config: ECSConfig? = null
    lateinit var eCSServices: ECSServices

    var mutableListOfPayments = mutableListOf<MECPayment>()
    var PAYMENT_HOLDER: MECPayments = MECPayments(mutableListOfPayments, false) //Default empty MECPayments

    fun getPrivacyUrl(): String? {
        return privacyUrl
    }

    fun getFaqUrl(): String? {
        return faqUrl
    }

    fun getTermsUrl(): String? {
        return termsUrl
    }

    fun setPrivacyUrl(privacyUrl: String) {
        this.privacyUrl = privacyUrl
    }

    fun getUserInfo(): UserInfo {

        var firstName = ""
        var lastName = ""
        var email = ""

        if (userDataInterface != null && userDataInterface.userLoggedInState == UserLoggedInState.USER_LOGGED_IN) {

            val userDataMap = ArrayList<String>()

            userDataMap.add(UserDetailConstants.GIVEN_NAME)
            userDataMap.add(UserDetailConstants.FAMILY_NAME)
            userDataMap.add(UserDetailConstants.EMAIL)
            try {
                val hashMap = userDataInterface.getUserDetails(userDataMap)
                firstName = hashMap.get(UserDetailConstants.GIVEN_NAME) as String
                lastName = hashMap.get(UserDetailConstants.FAMILY_NAME) as String
                email = hashMap.get(UserDetailConstants.EMAIL) as String
            } catch (e: UserDataInterfaceException) {
                e.printStackTrace()
            }

        }

        return UserInfo(firstName, lastName, email)
    }


    var bvClient: BVConversationsClient? = null

    fun setUpdateCartListener(mActionbarUpdateListener: ActionBarListener, mecCartUpdateListener: MECCartUpdateListener?) {
        actionbarUpdateListener = mActionbarUpdateListener
        if (null != mecCartUpdateListener) {
            this.mecCartUpdateListener = mecCartUpdateListener
        }
    }

    fun setPrivacyPolicyUrls(privacyUrl: String?, faqUrl: String?, termsUrl: String?) {
        this.privacyUrl = privacyUrl
        this.faqUrl = faqUrl
        this.termsUrl = termsUrl
    }

    fun isUserLoggedIn() : Boolean{
       return userDataInterface != null && userDataInterface.userLoggedInState == UserLoggedInState.USER_LOGGED_IN
    }

    fun isInternetActive() : Boolean{
        return  appinfra.restClient.isInternetReachable
    }

    fun initECSSDK() {
        val configError = AppConfigurationInterface.AppConfigurationError()
        val propositionID = appinfra.configInterface.getPropertyForKey("propositionid", "MEC", configError)
        var propertyForKey = ""
        if (propositionID != null) {
            propertyForKey = propositionID as String
        }

        var voucher: Boolean = true // if voucher key is not mentioned Appconfig then by default it will be considered True
        try {
            voucher =appinfra.configInterface.getPropertyForKey("voucherCode.enable", "MEC", configError) as Boolean
        } catch (e: Exception) {

        }

        propositionId = propertyForKey
        voucherEnabled = voucher
        val ecsServices = ECSServices(propertyForKey, appinfra as AppInfra)

        val defaultRetryPolicy = DefaultRetryPolicy( // 30 second time out
                30000,
                0,
                0f)
        ecsServices.setVolleyTimeoutAndRetryCount(defaultRetryPolicy)

        eCSServices = ecsServices // singleton

    }

}