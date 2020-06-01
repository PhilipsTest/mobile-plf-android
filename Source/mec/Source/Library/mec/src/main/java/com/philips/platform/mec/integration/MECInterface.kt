/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import com.philips.platform.appinfra.BuildConfig
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.integration.MECDataProvider.context
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.pif.DataInterface.MEC.MECDataInterface
import com.philips.platform.pif.DataInterface.MEC.MECException
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.uappframework.UappInterface
import com.philips.platform.uappframework.launcher.UiLauncher
import com.philips.platform.uappframework.uappinput.UappDependencies
import com.philips.platform.uappframework.uappinput.UappLaunchInput
import com.philips.platform.uappframework.uappinput.UappSettings

/**
 * MECInterface is the public class for any proposition to consume MEC micro app. Its the starting initialization point.
 * @since 2001.0
 */
class MECInterface : UappInterface {
    private var mMECSettings: MECSettings? = null
    private var mUappDependencies: UappDependencies? = null
    private var mUserDataInterface: UserDataInterface? = null
    internal var mecHandler = MECHandler()

    val MEC_NOTATION = "mec"
    private val TAG: String = MECInterface::class.java.simpleName



    /**
     * @param uappDependencies Object of UappDependencies
     * @param uappSettings     Object of UppSettings
     *   * @since 2001.1
     */
    override fun init(uappDependencies: UappDependencies, uappSettings: UappSettings) {
        val MECDependencies = uappDependencies as MECDependencies
        mUserDataInterface = MECDependencies.userDataInterface
        mMECSettings = uappSettings as MECSettings
        mUappDependencies = uappDependencies
        MECDataHolder.INSTANCE.appinfra = MECDependencies.appInfra

        //enable appInfra logging
        MECLog.isLoggingEnabled = true
        MECLog.appInfraLoggingInterface = MECDependencies.appInfra.logging.createInstanceForComponent(MEC_NOTATION, BuildConfig.VERSION_NAME)

        MECDataHolder.INSTANCE.userDataInterface = MECDependencies.userDataInterface
        MECAnalytics.initMECAnalytics(((mUappDependencies as MECDependencies?)!!))

    }

    /**
     * @param uiLauncher      Object of UiLauncherxx
     * @param uappLaunchInput Object of  UappLaunchInput
     * @throws MECException : It can throw user not logged in , no internet exception or  Philips shop not available (if
     *                      Hybris is explicitly turned off from code)
     * @throws RuntimeException
     */
    @Throws(RuntimeException::class, MECException::class)
    override fun launch(uiLauncher: UiLauncher, uappLaunchInput: UappLaunchInput) {

        MECDataHolder.INSTANCE.initECSSDK()


        //TODO Make error checking at a common place : Pabitra
        if(MECDataHolder.INSTANCE.isInternetActive()) {
            val mecLaunchInput = uappLaunchInput as MECLaunchInput
            MECDataHolder.INSTANCE.hybrisEnabled = mecLaunchInput.supportsHybris

            if(isLogInRequired(mecLaunchInput)){

                if(MECDataHolder.INSTANCE.isUserLoggedIn()){
                    if(mecLaunchInput.supportsHybris) {
                        launchMEC(uiLauncher, mecLaunchInput)
                    }else{
                        throw MECException(mMECSettings?.context?.getString(R.string.mec_no_philips_shop),MECException.HYBRIS_NOT_AVAILABLE)
                    }
                }else{
                    MECLog.d(TAG, "User is not logged in")
                    throw MECException(mMECSettings?.context?.getString(R.string.mec_cart_login_error_message),MECException.USER_NOT_LOGGED_IN)
                }
            }else{
                launchMEC(uiLauncher,mecLaunchInput)
            }

        }else{
            MECLog.e(TAG, "No Network or Internet not available")
            context?.let { MECAnalytics.getDefaultString(it,R.string.mec_no_internet ) }
            throw MECException(mMECSettings?.context?.getString(R.string.mec_no_internet),MECException.NO_INTERNET)
        }
    }

    private fun isLogInRequired(mecLaunchInput: MECLaunchInput) = mecLaunchInput.flowConfigurator?.landingView == MECFlowConfigurator.MECLandingView.MEC_SHOPPING_CART_VIEW || mecLaunchInput.flowConfigurator?.landingView == MECFlowConfigurator.MECLandingView.MEC_ORDER_HISTORY


    private fun launchMEC(uiLauncher: UiLauncher, mecLaunchInput: MECLaunchInput) {
        mMECSettings?.let { mecHandler.launchMEC(it,uiLauncher,mecLaunchInput) }
    }


    fun getMECDataInterface(): MECDataInterface {
        context = mMECSettings?.context
        return MECDataProvider
    }


}



