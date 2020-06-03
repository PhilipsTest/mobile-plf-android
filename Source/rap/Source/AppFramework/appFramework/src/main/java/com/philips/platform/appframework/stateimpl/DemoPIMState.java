package com.philips.platform.appframework.stateimpl;

import android.content.Context;

import androidx.annotation.NonNull;

import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.pim.demouapp.PIMDemoUAppDependencies;
import com.pim.demouapp.PIMDemoUAppInterface;
import com.pim.demouapp.PIMDemoUAppSettings;

/**
 * Created by philips on 30/03/17.
 */

public class DemoPIMState extends DemoBaseState {

    private Context appContext;

    public DemoPIMState() {
        super(AppStates.TESTPIM);
    }

    @Override
    public void navigate(UiLauncher uiLauncher) {
        PIMDemoUAppInterface pimDemoUAppDependencies = getPimDemoUAppInterface();
        pimDemoUAppDependencies.init(new PIMDemoUAppDependencies(((AppFrameworkApplication)appContext.getApplicationContext()).getAppInfra()), new PIMDemoUAppSettings(appContext));
        pimDemoUAppDependencies.launch(new ActivityLauncher(appContext, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED,
                getDLSThemeConfiguration(appContext.getApplicationContext()), 0, null), null);
    }

    @NonNull
    protected PIMDemoUAppInterface getPimDemoUAppInterface() {
        return new PIMDemoUAppInterface();
    }

    @Override
    public void init(Context context) {
        appContext = context;
    }

    @Override
    public void updateDataModel() {

    }
}
