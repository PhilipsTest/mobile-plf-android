package com.pim.demouapp;

import android.content.Context;
import android.content.Intent;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
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
    }

    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {

        if(uiLauncher instanceof FragmentLauncher){
            Intent intent=new Intent(mContext, PIMDemoUAppActivity.class);
            mContext.startActivity(intent);
        }
    }
}
