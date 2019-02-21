package com.udi.demouapp;

import android.content.Context;
import android.content.Intent;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by philips on 6/16/17.
 */

public class UdiDemoUAppInterface implements UappInterface {

    private Context mContext;
    static AppInfraInterface mAppInfra;
    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        mAppInfra=uappDependencies.getAppInfra();
        this.mContext=uappSettings.getContext();
    }

    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {

        if(uiLauncher instanceof ActivityLauncher){
            Intent intent=new Intent(mContext,DemoAppActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }
}
