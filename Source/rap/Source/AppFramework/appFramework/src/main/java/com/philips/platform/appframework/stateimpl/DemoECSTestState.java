package com.philips.platform.appframework.stateimpl;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ecs.demotestuapp.integration.EcsDemoTestAppSettings;
import com.ecs.demotestuapp.integration.EcsDemoTestUAppDependencies;
import com.ecs.demotestuapp.integration.EcsDemoTestUAppInterface;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

/**
 * Created by philips on 30/03/17.
 */

public class DemoECSTestState extends DemoBaseState {

    private Context appContext;
    private final String TAG = DemoECSTestState.class.getSimpleName();

    public DemoECSTestState() {
        super(AppStates.TESTECSTEST);
    }

    @Override
    public void navigate(UiLauncher uiLauncher) {
        UserDataInterface userDataInterface = ((AppFrameworkApplication) appContext.getApplicationContext()).getUserRegistrationState().getUserDataInterface();
        EcsDemoTestUAppInterface uAppInterface = getEcsDemoUAppInterface();
        try {
            uAppInterface.init(new EcsDemoTestUAppDependencies(((AppFrameworkApplication) appContext.getApplicationContext()).getAppInfra(), userDataInterface), new EcsDemoTestAppSettings(appContext));
        }catch (RuntimeException ex){
            RALog.d(TAG,ex.getMessage());
        }
        uAppInterface.launch(new ActivityLauncher(appContext, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED,
                getDLSThemeConfiguration(appContext.getApplicationContext()), 0, null), null);
    }

    @NonNull
    protected EcsDemoTestUAppInterface getEcsDemoUAppInterface() {
        return new EcsDemoTestUAppInterface();
    }

    @Override
    public void init(Context context) {
        appContext = context;
    }

    @Override
    public void updateDataModel() {

    }
}
