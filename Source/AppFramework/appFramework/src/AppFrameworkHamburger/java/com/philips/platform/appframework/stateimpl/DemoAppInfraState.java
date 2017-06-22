package com.philips.platform.appframework.stateimpl;

import android.content.Context;

import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.uappframework.launcher.UiLauncher;

/**
 * Created by philips on 30/03/17.
 */

public class DemoAppInfraState extends BaseState {

    private Context context;

    public DemoAppInfraState() {
        super(AppStates.TESTAPPINFRA);
    }

    @Override
    public void navigate(UiLauncher uiLauncher) {
//        AILDemouAppDependencies mAIDemoAppDependencies = new AILDemouAppDependencies(((AppFrameworkApplication) context.getApplicationContext()).getAppInfra());
//        AILDemouAppSettings aiDemoAppSettings = new AILDemouAppSettings(context.getApplicationContext());
//        AILDemouAppLaunchInput mAIDemoAppLaunchInput = new AILDemouAppLaunchInput();
//        AILDemouAppInterface mAIDemoAppInterface = new AILDemouAppInterface();
//        mAIDemoAppInterface.init(mAIDemoAppDependencies, aiDemoAppSettings);
//        ActivityLauncher activityLauncher = new ActivityLauncher(ActivityLauncher.
//                ActivityOrientation.SCREEN_ORIENTATION_SENSOR, 0);
//        mAIDemoAppInterface.launch(activityLauncher, mAIDemoAppLaunchInput);
    }

    @Override
    public void init(Context context) {
        this.context = context;
    }

    @Override
    public void updateDataModel() {

    }
}
