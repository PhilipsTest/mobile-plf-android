package com.philips.platform.appframework.stateimpl;

import android.content.Context;

import androidx.annotation.NonNull;

import com.mec.demouapp.MecDemoAppSettings;
import com.mec.demouapp.MecDemoUAppDependencies;
import com.mec.demouapp.MecDemoUAppInterface;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

/**
 * Created by philips on 30/03/17.
 */

public class DemoMECState extends DemoBaseState {

    private Context appContext;
    private final String TAG = DemoMECState.class.getSimpleName();

    public DemoMECState() {
        super(AppStates.TESTMEC);
    }

    @Override
    public void navigate(UiLauncher uiLauncher) {


        MecDemoUAppInterface uAppInterface = getMecDemoUAppInterface();
        UserDataInterface userDataInterface = ((AppFrameworkApplication) appContext.getApplicationContext()).getUserRegistrationState().getUserDataInterface();
        try {
            uAppInterface.init(new MecDemoUAppDependencies(((AppFrameworkApplication) appContext.getApplicationContext()).getAppInfra(),userDataInterface), new MecDemoAppSettings(appContext));
        }catch (RuntimeException ex){
            RALog.d(TAG,ex.getMessage());
        }
        uAppInterface.launch(new ActivityLauncher(appContext, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED,
                getDLSThemeConfiguration(appContext.getApplicationContext()), 0, null), null);
    }

    @NonNull
    protected MecDemoUAppInterface getMecDemoUAppInterface() {
        return new MecDemoUAppInterface();
    }

    @Override
    public void init(Context context) {
        appContext = context;
    }

    @Override
    public void updateDataModel() {

    }
}
