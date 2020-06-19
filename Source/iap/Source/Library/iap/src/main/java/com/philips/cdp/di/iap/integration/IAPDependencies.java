/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.integration;

import androidx.annotation.NonNull;

import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.uappframework.uappinput.UappDependencies;

/**
 * IAPDependencies handles the dependency required for IAP. So right now, IAP has one dependency i.e AppInfra. So vertical needs to initialize IAPDependencies and set the app infra object. This app infra object will be responsible for logging, tagging and some configuration.
 * @since 1.0.0
 */
public class IAPDependencies extends UappDependencies {

    private static final long serialVersionUID = -6048667063981632120L;
    private UserDataInterface userDataInterface;

    /**
     * Create IAPDependencies instance from AppInfraInterface and UserDataInterface object
     * @param appInfra  to pass the instance of AppInfraInterface
     * @param userDataInterface to pass the instance of UserDataInterface
     * @since 1903
     */
    public IAPDependencies(@NonNull AppInfraInterface appInfra,@NonNull UserDataInterface userDataInterface) {
        super(appInfra);
        CartModelContainer.getInstance().setAppInfraInstance(appInfra);
        this.userDataInterface = userDataInterface;
    }

    public UserDataInterface getUserDataInterface() {
        return userDataInterface;
    }
}
