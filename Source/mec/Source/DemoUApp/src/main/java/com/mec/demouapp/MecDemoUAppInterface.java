package com.mec.demouapp;

import android.content.Context;
import android.content.Intent;

import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

/**
 * Created by philips on 6/16/17.
 */

public class MecDemoUAppInterface implements UappInterface {

    private Context mContext;
    private MecDemoUAppDependencies mecDemoUAppDependencies;

    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        this.mContext=uappSettings.getContext();
        mecDemoUAppDependencies = (MecDemoUAppDependencies)uappDependencies;
        DependencyHolder.INSTANCE.setMecDemoUAppDependencies(mecDemoUAppDependencies);
    }

    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {

        Intent intent=new Intent(mContext,DemoLauncherActivity.class);
        mContext.startActivity(intent);
    }
}
