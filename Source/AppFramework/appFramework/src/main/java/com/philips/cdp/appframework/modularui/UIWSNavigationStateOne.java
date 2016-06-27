package com.philips.cdp.appframework.modularui;

import android.content.Context;

import com.philips.cdp.appframework.R;

/**
 * Created by 310240027 on 6/16/2016.
 */
public class UIWSNavigationStateOne implements UIBaseNavigation {
    @Override
    public
    @UIConstants.UIStateDef
    int onClick(int componentID, Context context) {
        @UIConstants.UIStateDef int destinationScreen = 0;

        switch (componentID) {
            case R.id.start_registration_button:
                destinationScreen = UIConstants.UI_WELCOME_STATE_TWO;
                break;
            case R.id.appframework_skip_button:
                destinationScreen = UIConstants.UI_WELCOME_STATE_TWO;
                break;

        }

        return destinationScreen;
    }

    @Override
    public
    @UIConstants.UIStateDef
    int onSwipe(int componentID, Context context) {
        return UIConstants.UI_SPLASH_STATE_ONE;
    }

    @Override
    public
    @UIConstants.UIStateDef
    int onLongPress(int componentID, Context context) {
        return UIConstants.UI_SPLASH_STATE_ONE;
    }

    @Override
    public int onPageLoad(Context context) {
        return 0;
    }

    @Override
    public void setState() {
        UIFlowManager.currentState = UIFlowManager.getFromStateList(UIConstants.UI_WELCOME_STATE_ONE);
    }
}
