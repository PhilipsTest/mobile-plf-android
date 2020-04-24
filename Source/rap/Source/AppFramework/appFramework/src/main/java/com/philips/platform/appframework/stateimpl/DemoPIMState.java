package com.philips.platform.appframework.stateimpl;

import android.content.Context;

import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.uappframework.launcher.UiLauncher;

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
//        PIMDemoUAppInterface pimDemoUAppDependencies = getPimDemoUAppInterface();
//        pimDemoUAppDependencies.init(new PIMDemoUAppDependencies(((AppFrameworkApplication)appContext.getApplicationContext()).getAppInfra()), new PIMDemoUAppSettings(appContext));
//        pimDemoUAppDependencies.launch(new ActivityLauncher(appContext, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED,
//                getDLSThemeConfiguration(appContext.getApplicationContext()), 0, null), null);
    }

//    @NonNull
//    protected PIMDemoUAppInterface getPimDemoUAppInterface() {
//        return new PIMDemoUAppInterface();
//    }

    @Override
    public void init(Context context) {
        appContext = context;
    }

    @Override
    public void updateDataModel() {

    }
}
