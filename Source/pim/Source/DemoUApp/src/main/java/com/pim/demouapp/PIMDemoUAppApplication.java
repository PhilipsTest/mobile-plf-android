package com.pim.demouapp;

import android.app.Application;

import androidx.annotation.NonNull;

import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pim.PIMInterface;

public class PIMDemoUAppApplication extends Application {

    @NonNull
    private AppInfraInterface appInfraInterface;
    private UserDataInterface userDataInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        appInfraInterface = new AppInfra.Builder().build(this);
    }

    public AppInfraInterface getAppInfra() {
        return appInfraInterface;
    }

    public void intialiseUR(){
        PIMDemoUAppDependencies pimDemoUAppDependencies = new PIMDemoUAppDependencies(appInfraInterface);
        PIMDemoUAppSettings pimDemoUAppSettings = new PIMDemoUAppSettings(this);
        URInterface urInterface = new URInterface();
        urInterface.init(pimDemoUAppDependencies, pimDemoUAppSettings);
        userDataInterface = urInterface.getUserDataInterface();
    }

    public void initialisePim() {
        PIMDemoUAppDependencies pimDemoUAppDependencies = new PIMDemoUAppDependencies(appInfraInterface);
        PIMDemoUAppSettings pimDemoUAppSettings = new PIMDemoUAppSettings(this);
        PIMInterface pimInterface = new PIMInterface();
        pimInterface.init(pimDemoUAppDependencies, pimDemoUAppSettings);
        userDataInterface = pimInterface.getUserDataInterface();
    }

    public UserDataInterface getUserDataInterface() {
        return userDataInterface;
    }

}
