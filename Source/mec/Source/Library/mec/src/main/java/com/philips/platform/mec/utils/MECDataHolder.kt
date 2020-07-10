/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.utils

import com.android.volley.DefaultRetryPolicy
import com.bazaarvoice.bvandroidsdk.BVConversationsClient
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.appinfra.tagging.ErrorCategory
import com.philips.platform.appinfra.tagging.TaggingError
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.model.config.ECSConfig
import com.philips.platform.mec.analytics.MECAnalyticServer
import com.philips.platform.mec.analytics.MECAnalyticServer.other
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant
import com.philips.platform.mec.analytics.MECAnalyticsConstant.appError
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
    var voucherCode: String? = null
    var maxCartCount: Int = 0
    lateinit var userDataInterface: UserDataInterface
    var refreshToken: String? = null //To avoid null check and Null pointer exception
    var blackListedRetailers: List<String>? = null
    var mecBazaarVoiceInput: MECBazaarVoiceInput? = null
    private var privacyUrl: String? = null
    private var faqUrl: String? = null
    private var termsUrl: String? = null
    var hybrisEnabled: Boolean = true
    var retailerEnabled: Boolean = true
    var voucherEnabled: Boolean = true
    var rootCategory: String? = null
    var mecLaunchingFragmentName:String? = null


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

        if (userDataInterface.userLoggedInState == UserLoggedInState.USER_LOGGED_IN) {

            val userDataMap = ArrayList<String>()

            userDataMap.add(UserDetailConstants.GIVEN_NAME)
            userDataMap.add(UserDetailConstants.FAMILY_NAME)
            userDataMap.add(UserDetailConstants.EMAIL)
            try {
                val hashMap = userDataInterface.getUserDetails(userDataMap)
                var firstNameValue = hashMap[UserDetailConstants.GIVEN_NAME]
                if (null != firstNameValue) {
                    firstName = firstNameValue as String
                }
                var lastNameValue = hashMap[UserDetailConstants.FAMILY_NAME]
                if (null != lastNameValue) {
                    lastName = lastNameValue as String
                }
                var emailValue = hashMap[UserDetailConstants.EMAIL]
                if (null != emailValue) {
                    email = emailValue as String
                }
            } catch (e: UserDataInterfaceException) {
                MECAnalytics.trackTechnicalError(MECAnalyticsConstant.COMPONENT_NAME + ":" + MECAnalyticsConstant.appError + ":" + MECAnalyticServer.other + e.toString() + ":" + MECAnalyticsConstant.exceptionErrorCode)
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

    fun isUserLoggedIn(): Boolean {
        return userDataInterface.userLoggedInState == UserLoggedInState.USER_LOGGED_IN
    }

    fun isInternetActive(): Boolean {
        return appinfra.restClient?.isInternetReachable ?: false
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
            voucher = appinfra.configInterface.getPropertyForKey("voucherCode.enable", "MEC", configError) as Boolean
            if (configError.errorCode != null) {
//                MECAnalytics.trackTechnicalError(COMPONENT_NAME + ":" + appError + ":" + other + configError.toString() + ":" + configError.errorCode)
                MECAnalytics.mAppTaggingInterface!!.trackErrorAction(ErrorCategory.TECHNICAL_ERROR, MECAnalytics.addCountryAndCurrency(mapOf()),
                        TaggingError(appError, other, configError.errorCode.toString(), configError.toString()))
            }
        } catch (e: Exception) {
//            MECAnalytics.trackTechnicalError(MECAnalyticsConstant.COMPONENT_NAME + ":" + appError + ":" + other + e.toString() + ":" + MECAnalyticsConstant.exceptionErrorCode)
            MECAnalytics.mAppTaggingInterface!!.trackErrorAction(ErrorCategory.TECHNICAL_ERROR, MECAnalytics.addCountryAndCurrency(mapOf()),
                    TaggingError(appError, other, MECAnalyticsConstant.exceptionErrorCode, e.toString()))
        }

        propositionId = propertyForKey
        voucherEnabled = voucher
        val ecsServices = com.philips.platform.ecs.ECSServices(propertyForKey, appinfra as AppInfra)

        val defaultRetryPolicy = DefaultRetryPolicy( // 30 second time out
                30000,
                0,
                0f)
        ecsServices.setVolleyTimeoutAndRetryCount(defaultRetryPolicy)

        eCSServices = ecsServices // singleton

    }

}