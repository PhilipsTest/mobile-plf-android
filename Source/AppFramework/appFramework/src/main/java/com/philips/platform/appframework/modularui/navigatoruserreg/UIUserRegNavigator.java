package com.philips.platform.appframework.modularui.navigatoruserreg;

import android.content.Context;

import com.philips.platform.appframework.utility.SharedPreferenceUtility;
import com.philips.platform.appframework.modularui.factorymanager.UIStateFactory;
import com.philips.platform.appframework.modularui.statecontroller.UIBaseNavigation;
import com.philips.platform.appframework.modularui.statecontroller.UIStateBase;
import com.philips.platform.appframework.modularui.statecontroller.UIStateManager;
import com.philips.platform.appframework.modularui.util.UIConstants;

/**
 * Created by 310240027 on 6/20/2016.
 */
public class UIUserRegNavigator implements UIBaseNavigation {
    @Override
    public UIStateBase onClick(int componentID, Context context) {

        return null;
    }

    @Override
    public UIStateBase onPageLoad(Context context) {
        SharedPreferenceUtility.getInstance().writePreferenceInt(UIConstants.UI_START_STATUS, UIConstants.UI_SPLASH_REGISTERED_STATE);
        return  UIStateFactory.getInstance().createUIState(UIConstants.UI_HAMBURGER_STATE);
    }

    @Override
    public void setState() {
        UIStateManager.getInstance().setCurrentState(UIStateManager.getInstance().getStateMap(UIConstants.UI_REGISTRATION_STATE));
    }
}
