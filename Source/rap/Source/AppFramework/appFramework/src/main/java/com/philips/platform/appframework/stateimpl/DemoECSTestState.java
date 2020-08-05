package com.philips.platform.appframework.stateimpl;

import android.content.Context;

import androidx.annotation.NonNull;

import com.iap.demouapp.IapDemoAppSettings;
import com.iap.demouapp.IapDemoUAppDependencies;
import com.iap.demouapp.IapDemoUAppInterface;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

import static com.facebook.FacebookSdk.getApplicationContext;

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
        IapDemoUAppInterface uAppInterface = getEcsDemoUAppInterface();
        try {
            uAppInterface.init(new IapDemoUAppDependencies(new AppInfra.Builder().build(getApplicationContext())), new IapDemoAppSettings(getApplicationContext()));
           // uAppInterface.init(new IapDemoUAppDependencies(((AppFrameworkApplication) appContext.getApplicationContext()).getAppInfra(), userDataInterface), new IapDemoAppSettings(appContext));
        }catch (RuntimeException ex){
            RALog.d(TAG,ex.getMessage());
        }
        uAppInterface.launch(new ActivityLauncher(appContext, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED,
                getDLSThemeConfiguration(appContext.getApplicationContext()), 0, null), null);
    }

    @NonNull
    protected IapDemoUAppInterface getEcsDemoUAppInterface() {
        return new IapDemoUAppInterface();
    }

    @Override
    public void init(Context context) {
        appContext = context;
    }

    @Override
    public void updateDataModel() {

    }
}
