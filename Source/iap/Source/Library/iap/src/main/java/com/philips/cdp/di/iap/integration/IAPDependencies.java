/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.integration;

import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.uappframework.uappinput.UappDependencies;

/**
 * IAPDependencies handles the dependency required for IAP. So right now, IAP has one dependency i.e AppInfra. So vertical needs to initialize IAPDependencies and set the app infra object. This app infra object will be responsible for logging, tagging and some configuration.
 * @since 1.0.0
 */
public class IAPDependencies extends UappDependencies {

    private UserDataInterface userDataInterface;

    /**
     * Create IAPDependencies instance from AppInfraInterface object
     * @param appInfra  to pass the instance of AppInfraInterface
     * @since 1.0.0
     * @deprecated Deprecated since 1902
     *
     */
    public IAPDependencies(AppInfraInterface appInfra) {
        super(appInfra);
        CartModelContainer.getInstance().setAppInfraInstance(appInfra);
    }

    /**
     * Create IAPDependencies instance from AppInfraInterface and UserDataInterface object
     * @param appInfra  to pass the instance of AppInfraInterface
     * @param userDataInterface to pass the instance of UserDataInterface
     * @since 1902
     */
    public IAPDependencies(AppInfraInterface appInfra, UserDataInterface userDataInterface) {
        super(appInfra);
        CartModelContainer.getInstance().setAppInfraInstance(appInfra);
        this.userDataInterface = userDataInterface;
    }

    public UserDataInterface getUserDataInterface() {
        return userDataInterface;
    }
}
