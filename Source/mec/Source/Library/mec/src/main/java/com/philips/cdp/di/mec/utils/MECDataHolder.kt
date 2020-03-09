package com.philips.cdp.di.mec.utils

import com.philips.cdp.di.mec.integration.MECBannerConfigurator
import com.bazaarvoice.bvandroidsdk.BVConversationsClient
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.model.config.ECSConfig
import com.philips.cdp.di.mec.integration.MECBazaarVoiceInput
import com.philips.cdp.di.mec.integration.MECListener
import com.philips.cdp.di.mec.screens.address.UserInfo
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState
import com.philips.platform.uappframework.listener.ActionBarListener
import java.util.ArrayList

enum class MECDataHolder {

    INSTANCE;

    lateinit var appinfra: AppInfraInterface
    lateinit var actionbarUpdateListener: ActionBarListener
    var mecListener: MECListener? = null
    var mecBannerEnabler: MECBannerConfigurator ? = null
    lateinit var locale:String
    lateinit var propositionId:String
    lateinit var voucherCode:String
    var maxCartCount:Int = 0
    lateinit var userDataInterface: UserDataInterface
    var refreshToken:String?=null
    var blackListedRetailers: List<String> ?=null
    lateinit var mecBazaarVoiceInput: MECBazaarVoiceInput
    private var privacyUrl: String? = null
    var hybrisEnabled: Boolean = true
    var retailerEnabled :Boolean = true
    var voucherEnabled :Boolean = true
    var rootCategory:String = ""
    var config: ECSConfig? = null
    lateinit var eCSServices: ECSServices

    fun getPrivacyUrl(): String? {
        return privacyUrl
    }

    fun setPrivacyUrl(privacyUrl: String) {
        this.privacyUrl = privacyUrl
    }

    fun getUserInfo():UserInfo{

        var firstName = ""
        var lastName = ""

        if(userDataInterface!=null && userDataInterface.userLoggedInState == UserLoggedInState.USER_LOGGED_IN){

            val userDataMap = ArrayList<String>()

            userDataMap.add(UserDetailConstants.GIVEN_NAME)
            userDataMap.add(UserDetailConstants.FAMILY_NAME)
            userDataMap.add(UserDetailConstants.EMAIL)
            try {
                val hashMap = userDataInterface.getUserDetails(userDataMap)
                firstName = hashMap.get(UserDetailConstants.GIVEN_NAME) as String
                lastName =  hashMap.get(UserDetailConstants.FAMILY_NAME) as String
            } catch (e: UserDataInterfaceException) {
                e.printStackTrace()
            }

        }

        return UserInfo(firstName,lastName)
    }


    var bvClient: BVConversationsClient? = null

    fun setActionBarListener(mActionbarUpdateListener: ActionBarListener,mECListener: MECListener){
        actionbarUpdateListener = mActionbarUpdateListener
        mecListener= mECListener
    }

}