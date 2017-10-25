/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.base;

import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.UIStateData;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.baseapp.screens.utility.RALog;

/**
 * This class aims to handle events inside the states and also
 * events when a particular state is loaded.
 */
abstract public class AbstractUIBasePresenter {
    public final String TAG = AbstractUIBasePresenter.class.getSimpleName();

    /*Event ID */
    protected static final int MENU_OPTION_HOME = 0;
    protected static final int MENU_OPTION_SETTINGS = 1;
    protected static final int MENU_OPTION_SHOP = 2;
    protected static final int MENU_OPTION_SUPPORT = 3;
    protected static final int MENU_OPTION_ABOUT = 4;
    protected static final int MENU_OPTION_TEST_MICROAPP = 5;
    //protected static final int MENU_OPTION_PR = 9;
    protected static final int MENU_OPTION_TELEHEALTHSERVICES = 6;
    protected static final int MENU_OPTION_POWER_SLEEP_CONNECTIVITY = 7;

    /* event to state map */
    protected static final String HOME_SETTINGS = "settings";
    protected static final String HOME_IAP = "iap";
    protected static final String HOME_SUPPORT = "support";
    protected static final String HOME_SHOPPING_CART = "shopping_cart";
    protected static final String HOME_ABOUT = "about";
    protected static final String HOME_FRAGMENT = "home_fragment";
    protected static final String HOME_SUPPORT_PR = "pr";
    protected static final String HOME_CONNECTIVITY = "connectivity";
    protected static final String HOME_TEST_MICROAPP = "testmicroapp";
    protected static final String HOME_COCO_VERSION_INFO="coco_version_info";
    protected static final String HOME_DEBUG="debug";
    protected static final String HOME_TELEHEALTHSERVICES = "telehealthservices";
    protected static final String HOME_POWER_SLEEP_CONNECTIVITY = "power_sleep_connectivity";

    private UIView uiView;

    public AbstractUIBasePresenter(final UIView uiView) {
        this.uiView = uiView;
    }

    /**
     * The onclick of objects in a particular state can be defined here
     * @param componentID The Id of any button or widget or any other component
     *
     */
    public abstract void onEvent(int componentID);

    /**
     * For seeting the current state , so that flow manager is updated with current state
     * @param stateID requires AppFlowState ID
     */
    public void setState(String stateID){

    }

    protected UIStateData setStateData(final String componentID) {
        RALog.d(TAG," setStateData called");

        switch (componentID) {
            case AppStates.HOME_FRAGMENT:
                UIStateData homeStateData = new UIStateData();
                homeStateData.setFragmentLaunchType(Constants.ADD_HOME_FRAGMENT);
                return homeStateData;
            case AppStates.SETTINGS:
                final UIStateData settingsStateData = new UIStateData();
                settingsStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return settingsStateData;
            case AppStates.IAP:
                final UIStateData iapStateData = new UIStateData();
                iapStateData.setFragmentLaunchType(Constants.CLEAR_TILL_HOME);
                return iapStateData;
            case AppStates.SUPPORT:
                final UIStateData supportStateData = new UIStateData();
                supportStateData.setFragmentLaunchType(Constants.CLEAR_TILL_HOME);
                return supportStateData;
            case AppStates.ABOUT:
                final UIStateData aboutStateData = new UIStateData();
                aboutStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return aboutStateData;
            // Commented as part of Plan A removal.
            /*case Constants.UI_SHOPPING_CART_BUTTON_CLICK:
                IAPState.InAppStateData uiStateDataModel = new IAPState().new InAppStateData();
                uiStateDataModel.setIapFlow(IAPState.IAP_SHOPPING_CART_VIEW);
                uiStateDataModel.setCtnList(getCtnList());
                return uiStateDataModel;*/
            case AppStates.PR:
                return new UIStateData();
            case AppStates.TEST_MICROAPP:
                final UIStateData testStateData=new UIStateData();
                testStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return testStateData;
            case AppStates.TEST_DEVICE_PAIRING:
                final UIStateData devicePairingStateData = new UIStateData();
                devicePairingStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return devicePairingStateData;
            case AppStates.TELEHEALTHSERVICES:
                final UIStateData teleHealthData = new UIStateData();
                teleHealthData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return teleHealthData;
            case AppStates.POWER_SLEEP_CONNECTIVITY:
                final UIStateData powerSleepStateData = new UIStateData();
                powerSleepStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return powerSleepStateData;
            default:
                RALog.d(TAG," default case selected ");
                homeStateData = new UIStateData();
                homeStateData.setFragmentLaunchType(Constants.ADD_HOME_FRAGMENT);
                return homeStateData;
        }
    }
}
