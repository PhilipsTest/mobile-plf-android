package com.pim.demouapp;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uappframework.uappinput.UappDependencies;

/**
 * Created by philips on 6/16/17.
 */

public class PIMDemoUAppDependencies extends UappDependencies {

    private final AppInfraInterface appInfra;

    @Override
    public AppInfraInterface getAppInfra() {
        return appInfra;
    }

    public PIMDemoUAppDependencies(AppInfraInterface appInfra) {
        super(appInfra);
        this.appInfra = appInfra;
        PIMUtility.getInstance().setAppInfra(appInfra);
    }

    public AppInfraInterface getAppInfraInterface(){
        return appInfra;
    }
}
