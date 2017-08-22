/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.appidentity;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;


/**
 * The type App identity manager.
 */
public class AppIdentityManager implements AppIdentityInterface {

    private AppIdentityManagerHelper mAppIdentityManagerHelper;

    public AppIdentityManager(AppInfra aAppInfra) {
        AppConfigurationInterface.AppConfigurationError configError =
                new AppConfigurationInterface
                .AppConfigurationError();
        mAppIdentityManagerHelper = new AppIdentityManagerHelper(aAppInfra,
                aAppInfra.getAppInfraContext(), configError);
        // Class shall not presume appInfra to be completely initialized at this point.
        // At any call after the constructor, appInfra can be presumed to be complete.
    }


    public void validateServiceDiscoveryEnv(String serviceDiscoveryEnvironment) {
        mAppIdentityManagerHelper.serviceDiscoveryEnvValidate(serviceDiscoveryEnvironment);
    }


    public void validateMicrositeId(String micrositeId) {
        mAppIdentityManagerHelper.micrositeIdValidation(micrositeId);
    }

    @Override
    public String getAppName() {
        return mAppIdentityManagerHelper.getApplicationName();
    }


    @Override
    public String getAppVersion() {
        return mAppIdentityManagerHelper.validateAppVersion();
    }

    @Override
    public AppState getAppState() {
        return mAppIdentityManagerHelper.getApplicationState();
    }

    @Override
    public String getServiceDiscoveryEnvironment() {
        return mAppIdentityManagerHelper.getServiceDiscoveryEnvironments();
    }


    @Override
    public String getLocalizedAppName() {
        /* Vertical App should have this string defined for all supported language files
	     *  default <string name="localized_commercial_app_name">AppInfra DemoApp localized</string>
         * */
        return mAppIdentityManagerHelper.getLocalizedApplicationName();
    }


    @Override
    public String getMicrositeId() {
        return mAppIdentityManagerHelper.retrieveMicrositeId();
    }


    @Override
    public String getSector() {
        return mAppIdentityManagerHelper.validateSector();
    }

}
