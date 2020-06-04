package com.pim.demouapp;

import android.content.Context;
import android.content.Intent;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

/**
 * Created by philips on 6/16/17.
 */

public class PIMDemoUAppInterface implements UappInterface {

    private Context mContext;
    static AppInfraInterface mAppInfra;
    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        mAppInfra=uappDependencies.getAppInfra();
        this.mContext=uappSettings.getContext();
        USRUDIHelper.getInstance().init(mContext,(PIMDemoUAppDependencies) uappDependencies);
    }

    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {

        if(uiLauncher instanceof ActivityLauncher){
            Intent intent=new Intent(mContext, PIMDemoUAppActivity.class);
            if(((ActivityLauncher) uiLauncher).getBundle() != null)
              intent.putExtras(((ActivityLauncher) uiLauncher).getBundle());
            ((ActivityLauncher) uiLauncher).getActivityContext().startActivity(intent);
        }
    }

    public boolean isUserLoggedIn(){
        return USRUDIHelper.getInstance().getUserDataInterface().getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN;
    }
}
