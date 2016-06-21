package com.philips.cdp.appframework.modularui;

import android.content.Context;

import com.philips.cdp.appframework.R;

/**
 * Created by 310240027 on 6/21/2016.
 */
public class UISplashNavigationStateThree implements UIBaseNavigation {
    @Override
    public int onClick(int componentID, Context context) {
        @UIStateDefintions.UIStateDef int destinationScreen = 0;
        UIState wsNavStateOne = (UIState) UIFlowManager.getFromStateList(UIStateDefintions.UI_SPLASH_STATE_THREE);
        return wsNavStateOne.getStateID();
    }

    @Override
    public int onSwipe(int componentID, Context context) {
        return 0;
    }

    @Override
    public int onLongPress(int componentID, Context context) {
        return 0;
    }
}
