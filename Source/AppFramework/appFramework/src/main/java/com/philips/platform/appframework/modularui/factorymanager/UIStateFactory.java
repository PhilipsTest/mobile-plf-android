package com.philips.platform.appframework.modularui.factorymanager;

import com.philips.platform.appframework.modularui.navigatorsplash.UISplashDonePressedNavigator;
import com.philips.platform.appframework.modularui.navigatorsplash.UISplashRegisteredNavigator;
import com.philips.platform.appframework.modularui.navigatorsplash.UISplashUnRegisteredNavigator;
import com.philips.platform.appframework.modularui.statecontroller.UIStateManager;
import com.philips.platform.appframework.modularui.navigatoruserreg.UIUserRegNavigator;
import com.philips.platform.appframework.modularui.navigatorwelcome.UIWelcomeScreenNavigator;
import com.philips.platform.appframework.modularui.navigatorhamburger.UIHamDebugNavigator;
import com.philips.platform.appframework.modularui.navigatorhamburger.UIHamHomeNavigator;
import com.philips.platform.appframework.modularui.navigatorhamburger.UIHamSettingsNavigator;
import com.philips.platform.appframework.modularui.navigatorhamburger.UIHamSupportNavigator;
import com.philips.platform.appframework.modularui.navigatorhamburger.UIHamburgerNavigator;
import com.philips.platform.appframework.modularui.statecontroller.UIState;
import com.philips.platform.appframework.modularui.statecontroller.UIStateBase;
import com.philips.platform.appframework.modularui.util.UIConstants;

/**
 * Created by 310240027 on 6/28/2016.
 */
public class UIStateFactory {

    private static UIStateFactory instance = new UIStateFactory();

    private UIStateFactory(){

    }

    public static UIStateFactory getInstance(){
        if(null == instance){
            instance = new UIStateFactory();
        }
        return instance;
    }


    public UIStateBase createUIState(@UIConstants.UIStateDef int uiStateID){

        UIState uistate = new UIState();
        if(!UIStateManager.getInstance().getUiStateMap().containsKey(uiStateID)){
            switch (uiStateID) {
                case UIConstants.UI_SPLASH_UNREGISTERED_STATE:
                    uistate.setStateID(UIConstants.UI_SPLASH_UNREGISTERED_STATE);
                    uistate.setNavigator(new UISplashUnRegisteredNavigator());
                    break;
                case UIConstants.UI_SPLASH_REGISTERED_STATE:
                    uistate.setStateID(UIConstants.UI_SPLASH_REGISTERED_STATE);
                    uistate.setNavigator(new UISplashRegisteredNavigator());
                    break;
                case UIConstants.UI_SPLASH_DONE_PRESSED_STATE:
                    uistate.setStateID(UIConstants.UI_SPLASH_DONE_PRESSED_STATE);
                    uistate.setNavigator(new UISplashDonePressedNavigator());
                    break;
                case UIConstants.UI_WELCOME_STATE:
                    uistate.setStateID(UIConstants.UI_WELCOME_STATE);
                    uistate.setNavigator(new UIWelcomeScreenNavigator());
                    break;
                case UIConstants.UI_REGISTRATION_STATE:
                    uistate.setStateID(UIConstants.UI_REGISTRATION_STATE);
                    uistate.setNavigator(new UIUserRegNavigator());
                    break;
                case UIConstants.UI_HAMBURGER_STATE:
                    uistate.setStateID(UIConstants.UI_HAMBURGER_STATE);
                    uistate.setNavigator(new UIHamburgerNavigator());
                    break;
                case UIConstants.UI_HAMBURGER_HOME_STATE_ONE:
                    uistate.setStateID(UIConstants.UI_HAMBURGER_HOME_STATE_ONE);
                    uistate.setNavigator(new UIHamHomeNavigator());
                    break;
                case UIConstants.UI_HAMBURGER_SUPPORT_STATE_ONE:
                    uistate.setStateID(UIConstants.UI_HAMBURGER_SUPPORT_STATE_ONE);
                    uistate.setNavigator(new UIHamSupportNavigator());
                    break;
                case UIConstants.UI_HAMBURGER_SETTINGS_STATE_ONE:
                    uistate.setStateID(UIConstants.UI_HAMBURGER_SETTINGS_STATE_ONE);
                    uistate.setNavigator(new UIHamSettingsNavigator());
                    break;
                case UIConstants.UI_HAMBURGER_DEBUG_STATE_STATE_ONE:
                    uistate.setStateID(UIConstants.UI_HAMBURGER_DEBUG_STATE_STATE_ONE);
                    uistate.setNavigator(new UIHamDebugNavigator());
                    break;
            }
            UIStateManager.getInstance().addToStateList(uistate);
            return uistate;
        }else {
           return UIStateManager.getInstance().getStateMap(uiStateID);
        }

    }
}
