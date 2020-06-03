package com.iap.demouapp;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uappframework.uappinput.UappDependencies;

/**
 * Created by philips on 6/16/17.
 */

public class IapDemoUAppDependencies extends UappDependencies {

    private final AppInfraInterface appInfra;

    @Override
    public AppInfraInterface getAppInfra() {
        return appInfra;
    }

    public IapDemoUAppDependencies(AppInfraInterface appInfra) {
        super(appInfra);
        this.appInfra = appInfra;
        DemoAppUserHelper.getInstance().setAppInfraInterface(appInfra);

    }
}
