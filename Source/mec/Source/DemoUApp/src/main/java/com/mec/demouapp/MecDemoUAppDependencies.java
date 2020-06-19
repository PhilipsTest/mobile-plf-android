package com.mec.demouapp;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.uappframework.uappinput.UappDependencies;

/**
 * Created by philips on 6/16/17.
 */

public class MecDemoUAppDependencies extends UappDependencies {

    private final AppInfraInterface appInfra;

    public UserDataInterface getUserDataInterface() {
        return userDataInterface;
    }

    private final UserDataInterface userDataInterface;

    @Override
    public AppInfraInterface getAppInfra() {
        return appInfra;
    }

    public MecDemoUAppDependencies(AppInfraInterface appInfra ,UserDataInterface userDataInterface) {
        super(appInfra);
        this.appInfra = appInfra;
        this.userDataInterface = userDataInterface;
    }
}
