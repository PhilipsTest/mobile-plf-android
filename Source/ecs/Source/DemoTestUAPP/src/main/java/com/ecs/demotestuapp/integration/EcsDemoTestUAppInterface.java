package com.ecs.demotestuapp.integration;

import android.content.Context;
import android.content.Intent;

import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

/**
 * Created by philips on 6/16/17.
 */

public class EcsDemoTestUAppInterface implements UappInterface {

    private Context mContext;
    private EcsDemoTestUAppDependencies uappDependencies;
    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        this.mContext=uappSettings.getContext();
        this.uappDependencies = (EcsDemoTestUAppDependencies) uappDependencies;
        DependencyHolder.INSTANCE.setuAppDependencies( this.uappDependencies);
    }

    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {

        if(uiLauncher instanceof ActivityLauncher){

            Intent intent=new Intent(mContext, EcsDemoTestActivity.class);
            mContext.startActivity(intent);
        }
    }
}
