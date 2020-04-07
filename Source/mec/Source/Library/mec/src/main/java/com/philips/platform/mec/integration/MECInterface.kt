/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import com.philips.platform.appinfra.BuildConfig
import com.philips.platform.mec.R
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.pif.DataInterface.MEC.MECDataInterface
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.uappframework.UappInterface
import com.philips.platform.uappframework.launcher.UiLauncher
import com.philips.platform.uappframework.uappinput.UappDependencies
import com.philips.platform.uappframework.uappinput.UappLaunchInput
import com.philips.platform.uappframework.uappinput.UappSettings

/**
 * MECInterface is the public class for any proposition to consume MEC micro app. Its the starting initialization point.
 * @since 1.0.0
 */
 class MECInterface : UappInterface {
    private var mMECSettings: MECSettings?=null
    private var mUappDependencies: UappDependencies? = null
    private var mUserDataInterface: UserDataInterface? = null
    val MEC_NOTATION = "mec"


    /**
     * @param uappDependencies Object of UappDependencies
     * @param uappSettings     Object of UppSettings
     *   * @since 2001.1
     */
    override fun init(uappDependencies: UappDependencies, uappSettings: UappSettings) {
        val MECDependencies = uappDependencies as MECDependencies
        mUserDataInterface = MECDependencies.userDataInterface


        if (null == mUserDataInterface)
            throw RuntimeException("UserDataInterface is not injected in MECDependencies.")

        mMECSettings = uappSettings as MECSettings
        mUappDependencies = uappDependencies


        MECDataHolder.INSTANCE.appinfra = MECDependencies.appInfra

        //enable appInfra logging
        MECLog.isLoggingEnabled = true
        MECLog.appInfraLoggingInterface = MECDependencies.appInfra.logging.createInstanceForComponent(MEC_NOTATION, BuildConfig.VERSION_NAME)

        MECDataHolder.INSTANCE.userDataInterface = MECDependencies.userDataInterface

    }

    /**
     * @param uiLauncher      Object of UiLauncherxx
     * @param uappLaunchInput Object of  UappLaunchInput
     * @throws MECLaunchException : It can through user not logged in or no internet exception
     * @throws RuntimeException
     */
    @Throws(RuntimeException::class,MECLaunchException::class)
    override fun launch(uiLauncher: UiLauncher, uappLaunchInput: UappLaunchInput) {

        MECDataHolder.INSTANCE.initECSSDK()

        if(MECDataHolder.INSTANCE.isInternetActive()) {
            val mecLaunchInput = uappLaunchInput as MECLaunchInput

            if(mecLaunchInput.flowConfigurator?.landingView == MECFlowConfigurator.MECLandingView.MEC_SHOPPING_CART_VIEW){

                if(MECDataHolder.INSTANCE.isUserLoggedIn()){
                    launchMEC(uiLauncher,mecLaunchInput)
                }else{
                    throw MECLaunchException(MECDataHolder.INSTANCE.appinfra.appInfraContext.getString(R.string.mec_cart_login_error_message),MECLaunchException.ERROR_CODE_NOT_LOGGED_IN)
                }
            }else{
                launchMEC(uiLauncher,mecLaunchInput)
            }


        }else{
            throw MECLaunchException(MECDataHolder.INSTANCE.appinfra.appInfraContext.getString(R.string.mec_no_internet),MECLaunchException.ERROR_CODE_NO_INTERNET)
        }
    }


    private fun launchMEC(uiLauncher: UiLauncher, mecLaunchInput: MECLaunchInput){
        val mecHandler = this!!.mMECSettings?.let { MECHandler((mUappDependencies as MECDependencies?)!!, it, uiLauncher, mecLaunchInput) }
        mecHandler?.launchMEC()
    }


    companion object {

        val instance = MECDataProvider()
        /**
         * Get the Singleton MEC Data Interface to call MEC public API
         *
         * @since 2002.0
         */

        @JvmStatic
        open fun getMECDataInterface(): MECDataInterface {
            return instance
        }
    }


}



