package com.philips.platform.appframework.stateimpl;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintSet;

import com.ccb.demouapp.CCBDemoUAppActivity;
import com.ccb.demouapp.integration.CCBDemoUAppDependencies;
import com.ccb.demouapp.integration.CCBDemoUAppInterface;
import com.ccb.demouapp.integration.CCBDemoUAppSettings;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;

public class DemoCCBState extends DemoBaseState {

    private Context mContext;
    private CCBDemoUAppInterface ccbDemoUAppInterface;


    public DemoCCBState( ) {
        super(AppStates.TESTCCB);
    }

    @Override
    public void navigate(UiLauncher uiLauncher) {
        ccbDemoUAppInterface.launch(new ActivityLauncher(mContext, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, null, 0, null),new UappLaunchInput());
    }

    @Override
    public void init(Context context) {
        mContext = context;
        CCBDemoUAppDependencies ccbDemoUAppDependencies = new CCBDemoUAppDependencies(((AppFrameworkApplication) context.getApplicationContext()).getAppInfra());
        CCBDemoUAppSettings ccbDemoUAppSettings = new CCBDemoUAppSettings(context);
        ccbDemoUAppInterface = new CCBDemoUAppInterface();
        ccbDemoUAppInterface.init(ccbDemoUAppDependencies, ccbDemoUAppSettings);
    }

    @Override
    public void updateDataModel() {
    }
}
