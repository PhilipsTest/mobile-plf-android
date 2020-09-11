package com.pim.demouapp;

import android.content.Context;


import com.philips.cdp.registration.ui.utils.URDependancies;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URSettings;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pim.PIMDependencies;
import com.philips.platform.pim.PIMInterface;
import com.philips.platform.pim.PIMLaunchFlow;
import com.philips.platform.pim.PIMLaunchInput;
import com.philips.platform.pim.PIMSettings;
import com.philips.platform.pim.listeners.UserLoginListener;
import com.philips.platform.pim.listeners.UserMigrationListener;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.pim.demouapp.PIMDemoUAppLaunchInput.RegistrationLib;

import java.util.HashMap;

class USRUDIHelper {
    private static final USRUDIHelper ourInstance = new USRUDIHelper();
    private Context mContext;
    private PIMDemoUAppDependencies pimDemoUAppDependencies;
    private PIMInterface pimInterface;
    private URInterface urInterface;

    static USRUDIHelper getInstance() {
        return ourInstance;
    }

    private USRUDIHelper() {
        pimInterface = new PIMInterface();
        urInterface = new URInterface();
    }

    void init(Context mContext, PIMDemoUAppDependencies pimDemoUAppDependencies) {
        this.mContext = mContext;
        this.pimDemoUAppDependencies = pimDemoUAppDependencies;
        if (pimDemoUAppDependencies.getRegistrationLib() == RegistrationLib.USR)
            intialiseUSR();
        else
            initialiseUDI();
    }

    public AppInfraInterface getAppInfra() {
        return pimDemoUAppDependencies.getAppInfra();
    }

    private void intialiseUSR() {
        PIMDependencies pimDependencies = new PIMDependencies(getAppInfra());
        PIMSettings pimSettings = new PIMSettings(mContext);
        urInterface.init(pimDependencies, pimSettings);
    }

    private void initialiseUDI() {
        URDependancies urDependancies = new URDependancies(getAppInfra());
        URSettings urSettings = new URSettings(mContext);
        pimInterface.init(urDependancies, urSettings);
    }

    void launchUDIAsFragment(FragmentLauncher fragmentLauncher, PIMLaunchInput launchInput) {
        pimInterface.launch(fragmentLauncher, launchInput);
    }

    void launchUSR(FragmentLauncher fragmentLauncher, UappLaunchInput uappLaunchInput) {
        urInterface.launch(fragmentLauncher, uappLaunchInput);
    }

    void migrateJanrainUserToPIM(UserMigrationListener userMigrationListener) {
        pimInterface.migrateJanrainUserToPIM(userMigrationListener);
    }

    UserDataInterface getUserDataInterface() {
        if (getRegistrationLib() == RegistrationLib.USR)
            return urInterface.getUserDataInterface();
        else
            return pimInterface.getUserDataInterface();
    }

    RegistrationLib getRegistrationLib() {
        return pimDemoUAppDependencies.getRegistrationLib();
    }

    public void setLoginListener(UserLoginListener userLoginListener) {
        if (getRegistrationLib() == RegistrationLib.UDI)
            pimInterface.setLoginListener(userLoginListener);
    }
}
