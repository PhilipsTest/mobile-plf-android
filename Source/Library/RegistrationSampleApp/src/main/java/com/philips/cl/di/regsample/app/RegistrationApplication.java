
package com.philips.cl.di.regsample.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.philips.cdp.localematch.PILLocaleManager;
import com.philips.cdp.registration.AppIdentityInfo;
import com.philips.cdp.registration.configuration.Configuration;
import com.philips.cdp.registration.configuration.HSDPInfo;
import com.philips.cdp.registration.configuration.URConfigurationConstants;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.URDependancies;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URSettings;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;

import java.util.Locale;

public class RegistrationApplication extends Application {


    private static volatile RegistrationApplication mRegistrationHelper = null;

    private AppInfraInterface mAppInfraInterface;

    /**
     * @return instance of this class
     */
    public synchronized static RegistrationApplication getInstance() {
        return mRegistrationHelper;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRegistrationHelper = this;
        mAppInfraInterface = new AppInfra.Builder().build(this);
        SharedPreferences prefs = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE);
        String restoredText = prefs.getString("reg_environment", null);
        if (restoredText != null) {
            String restoredHSDPText = prefs.getString("reg_hsdp_environment", null);
            if (restoredHSDPText != null && restoredHSDPText.equals(restoredText)) {
                initHSDP(RegUtility.getConfiguration(restoredHSDPText));
            }
            initRegistration(RegUtility.getConfiguration(restoredText));
        } else {
            initRegistration(Configuration.STAGING);
        }
    }


    public void initRegistration(Configuration configuration) {
        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();
        if(mAppInfraInterface == null){
            mAppInfraInterface = new AppInfra.Builder().build(this);
        }
        SharedPreferences.Editor editor = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE).edit();
        editor.putString("reg_environment", configuration.getValue());
        editor.commit();


        String languageCode = Locale.getDefault().getLanguage();
        String countryCode = Locale.getDefault().getCountry();

        PILLocaleManager localeManager = new PILLocaleManager(this);
       localeManager.setInputLocale(languageCode, countryCode);
     //   localeManager.setInputLocale("zh", "CN");

        initAppIdentity(configuration);
        URDependancies urDependancies = new URDependancies(mAppInfraInterface);
        URSettings urSettings = new URSettings(this);
        URInterface urInterface = new URInterface();
        urInterface.init(urDependancies, urSettings);


       mAppInfraInterface.getConfigInterface().setPropertyForKey("appidentity.micrositeId",
                "appinfra",
                "81451",
                configError);

    }
    public void initHSDP(Configuration configuration) {
        if(mAppInfraInterface == null){
            mAppInfraInterface = new AppInfra.Builder().build(this);
        }
        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();
        //store hsdp last envoronment
        HSDPInfo hsdpInfo;
        SharedPreferences.Editor editor = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE).edit();
        switch (configuration) {
            case EVALUATION:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_APPLICATION_NAME,
                        URConfigurationConstants.UR,
                        "uGrow",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_SECRET,
                        URConfigurationConstants.UR,
                        "e33a4d97-6ada-491f-84e4-a2f7006625e2",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_SHARED,
                        URConfigurationConstants.UR,
                        "e95f5e71-c3c0-4b52-8b12-ec297d8ae960",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_BASE_URL,
                        URConfigurationConstants.UR,
                        "https://ugrow-ds-staging.eu-west.philips-healthsuite.com",
                        configError);

                editor.putString("reg_hsdp_environment", configuration.getValue());
                editor.commit();
                break;
            case DEVELOPMENT:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_APPLICATION_NAME,
                        URConfigurationConstants.UR,
                        "uGrow",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_SECRET,
                        URConfigurationConstants.UR,
                        "c623685e-f02c-11e5-9ce9-5e5517507c66",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_SHARED,
                        URConfigurationConstants.UR,
                        "c62362a0-f02c-11e5-9ce9-5e5517507c66",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_BASE_URL,
                        URConfigurationConstants.UR,
                        "https://ugrow-ds-development.cloud.pcftest.com",
                        configError);
                editor.putString("reg_hsdp_environment", configuration.getValue());
                editor.commit();

                break;
            case PRODUCTION:
                SharedPreferences prefs = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE);
                prefs.edit().remove("reg_hsdp_environment").commit();
                break;
            case STAGING:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_APPLICATION_NAME,
                        URConfigurationConstants.UR,
                        "uGrow",
                        configError);
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_SECRET,
                        URConfigurationConstants.UR,
                        "e33a4d97-6ada-491f-84e4-a2f7006625e2",
                        configError);
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_SHARED,
                        URConfigurationConstants.UR,
                        "e95f5e71-c3c0-4b52-8b12-ec297d8ae960",
                        configError);
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        URConfigurationConstants.
                                HSDP_CONFIGURATION_BASE_URL,
                        URConfigurationConstants.UR,
                        "https://ugrow-ds-staging.eu-west.philips-healthsuite.com",
                        configError);
                editor.putString("reg_hsdp_environment", configuration.getValue());
                editor.commit();

                break;
            case TESTING:
                prefs = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE);
                prefs.edit().remove("reg_hsdp_environment").commit();
                break;
        }



    }

    public static final String SERVICE_DISCOVERY_TAG = "ServiceDiscovery";
    final String AI = "appinfra";
    private void initAppIdentity(Configuration configuration) {
        if(mAppInfraInterface == null){
            mAppInfraInterface = new AppInfra.Builder().build(this);
        }
        AppIdentityInterface mAppIdentityInterface;
        mAppIdentityInterface = mAppInfraInterface.getAppIdentity();
        AppConfigurationInterface appConfigurationInterface = mAppInfraInterface.
                getConfigInterface();

        //Dynamically set the values to appInfar and app state

        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();
       /* mAppInfraInterface.
                getConfigInterface().setPropertyForKey(
                "appidentity.micrositeId",
                AI,
                "77000",
                configError);*/

        mAppInfraInterface.
                getConfigInterface().setPropertyForKey(
                "appidentity.sector",
                AI,
                "b2c",
                configError);

        mAppInfraInterface.
                getConfigInterface().setPropertyForKey(
                "appidentity.serviceDiscoveryEnvironment",
                AI,
                "Production",
                configError);


        switch (configuration) {
            case EVALUATION:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "appidentity.appState",
                        AI,
                        "ACCEPTANCE",
                        configError);
                break;
            case DEVELOPMENT:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "appidentity.appState",
                        AI,
                        "DEVELOPMENT",
                        configError);

                break;
            case PRODUCTION:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "appidentity.appState",
                        AI,
                        "PRODUCTION",
                        configError);
                break;
            case STAGING:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "appidentity.appState",
                        AI,
                        "STAGING",
                        configError);

                break;
            case TESTING:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "appidentity.appState",
                        AI,
                        "TEST",
                        configError);
                break;
        }

        AppIdentityInfo appIdentityInfo = new AppIdentityInfo();
        appIdentityInfo.setAppLocalizedNAme(mAppIdentityInterface.getLocalizedAppName());
        appIdentityInfo.setSector(mAppIdentityInterface.getSector());
        appIdentityInfo.setMicrositeId(mAppIdentityInterface.getMicrositeId());
        appIdentityInfo.setAppName(mAppIdentityInterface.getAppName());
        appIdentityInfo.setAppState(mAppIdentityInterface.getAppState().toString());
        appIdentityInfo.setAppVersion(mAppIdentityInterface.getAppVersion());
        appIdentityInfo.setServiceDiscoveryEnvironment(mAppIdentityInterface.getServiceDiscoveryEnvironment());

        Log.i(SERVICE_DISCOVERY_TAG, " AppIdentity AppLocalizedNAme : " + appIdentityInfo.getAppLocalizedNAme());
        Log.i(SERVICE_DISCOVERY_TAG, " AppIdentity Sector : " + appIdentityInfo.getSector());
        Log.i(SERVICE_DISCOVERY_TAG, " AppIdentity MicrositeId : " + appIdentityInfo.getMicrositeId());
        Log.i(SERVICE_DISCOVERY_TAG, " AppIdentity AppName : " + appIdentityInfo.getAppName());
        Log.i(SERVICE_DISCOVERY_TAG, " AppIdentity AppState : " + appIdentityInfo.getAppState().toString());
        Log.i(SERVICE_DISCOVERY_TAG, " AppIdentity AppVersion : " + appIdentityInfo.getAppVersion());
        Log.i(SERVICE_DISCOVERY_TAG, " AppIdentity ServiceDiscoveryEnvironment : " + appIdentityInfo.getServiceDiscoveryEnvironment());
    }
}

