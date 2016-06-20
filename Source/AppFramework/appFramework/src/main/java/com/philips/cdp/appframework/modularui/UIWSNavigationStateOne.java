package com.philips.cdp.appframework.modularui;

import android.content.Context;

import com.philips.cdp.appframework.R;

/**
 * Created by 310240027 on 6/16/2016.
 */
public class UIWSNavigationStateOne implements UIBaseNavigation {
    @Override
    public  @UIStateDefintions.UIStateDef int onClick(int componentID,Context context) {
        @UIStateDefintions.UIStateDef int destinationScreen = 0;
        switch (componentID){
            case R.id.start_registration_button:
                destinationScreen = UIStateDefintions.UI_WELCOME_STATE_ONE;

                break;
            case R.id.appframework_skip_button:
                destinationScreen = UIStateDefintions.UI_WELCOME_STATE_ONE;

                break;

        }
        UIFlowManager.currentState = destinationScreen;
        return destinationScreen;
    }

    @Override
    public  @UIStateDefintions.UIStateDef int onSwipe(int componentID,Context context) {
        return UIStateDefintions.UI_SPLASH_STATE_ONE;
    }

    @Override
    public  @UIStateDefintions.UIStateDef int onLongPress(int componentID,Context context) {
        return UIStateDefintions.UI_SPLASH_STATE_ONE;
    }
}
