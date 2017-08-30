/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp;


import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.baseapp.screens.aboutscreen.AboutScreenState;
import com.philips.platform.baseapp.screens.cocoversion.CocoVersionState;
import com.philips.platform.baseapp.screens.consumercare.SupportFragmentState;
import com.philips.platform.baseapp.screens.debugtest.DebugTestFragmentState;
import com.philips.platform.baseapp.screens.devicepairing.DevicePairingState;
import com.philips.platform.baseapp.screens.homefragment.HomeFragmentState;
import com.philips.platform.baseapp.screens.inapppurchase.IAPRetailerFlowState;
import com.philips.platform.baseapp.screens.introscreen.welcomefragment.WelcomeState;
import com.philips.platform.baseapp.screens.productregistration.ProductRegistrationState;
import com.philips.platform.baseapp.screens.settingscreen.SettingsFragmentState;
import com.philips.platform.baseapp.screens.splash.SplashState;
import com.philips.platform.baseapp.screens.termsandconditions.TermsAndConditionsState;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationOnBoardingState;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationSettingsState;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.modularui.stateimpl.ConnectivityFragmentState;

import java.util.Map;

public class FlowManagerUtil {
    private static final String TAG = FlowManagerUtil.class.getSimpleName();

    public void addValuesToMap(final Map<String, BaseState> uiStateMap) {
        RALog.d(TAG, " addValuesToMap called");
        uiStateMap.put(AppStates.WELCOME, new WelcomeState());
        uiStateMap.put(AppStates.ON_BOARDING_REGISTRATION, new UserRegistrationOnBoardingState());
        uiStateMap.put(AppStates.SETTINGS_REGISTRATION, new UserRegistrationSettingsState());
        uiStateMap.put(AppStates.HOME_FRAGMENT, new HomeFragmentState());
        uiStateMap.put(AppStates.ABOUT, new AboutScreenState());
        uiStateMap.put(AppStates.DEBUG, new DebugTestFragmentState());
        uiStateMap.put(AppStates.SETTINGS, new SettingsFragmentState());
        uiStateMap.put(AppStates.IAP, new IAPRetailerFlowState());
        uiStateMap.put(AppStates.PR, new ProductRegistrationState());
        uiStateMap.put(AppStates.SUPPORT, new SupportFragmentState());
        uiStateMap.put(AppStates.SPLASH, new SplashState());
        uiStateMap.put(AppStates.TERMSANDCONITIONSSTATE,new TermsAndConditionsState());
        uiStateMap.put(AppStates.CONNECTIVITY, new ConnectivityFragmentState());
        uiStateMap.put(AppStates.COCO_VERSION_INFO, new CocoVersionState());
        uiStateMap.put(AppStates.DEVICE_PAIRING, new DevicePairingState());
    }
}
