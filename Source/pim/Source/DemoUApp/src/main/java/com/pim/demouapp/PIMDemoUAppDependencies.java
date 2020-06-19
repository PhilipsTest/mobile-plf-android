package com.pim.demouapp;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.pim.demouapp.PIMDemoUAppLaunchInput.RegistrationLib;

/**
 * Created by philips on 6/16/17.
 */

public class PIMDemoUAppDependencies extends UappDependencies {

    private final AppInfraInterface appInfra;
    private RegistrationLib component_type;

    @Override
    public AppInfraInterface getAppInfra() {
        return appInfra;
    }

    public PIMDemoUAppDependencies(AppInfraInterface appInfra, RegistrationLib component) {
        super(appInfra);
        this.appInfra = appInfra;
        this.component_type = component;
    }

    public RegistrationLib getRegistrationLib(){
        return component_type;
    }


}
