package com.philips.cdp.registration.sample;

import android.app.Application;
import android.util.Log;

import com.philips.cdp.localematch.PILLocaleManager;
import com.philips.cdp.registration.configuration.Configuration;
import com.philips.cdp.registration.configuration.HSDPInfo;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.ui.utils.URDependancies;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URSettings;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;

import java.util.ArrayList;
import java.util.Locale;

public class RegistrationApplication extends Application {


    public static final String SERVICE_DISCOVERY_TAG = "ServiceDiscovery";

    private AppInfraInterface mAppInfraInterface;
    @Override
    public void onCreate() {
        super.onCreate();
        mAppInfraInterface = new AppInfra.Builder().build(this);
        initRegistration(Configuration.STAGING);
    }

    public void initRegistration(Configuration configuration) {
        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();
        if(mAppInfraInterface == null){
            mAppInfraInterface = new AppInfra.Builder().build(this);
        }
        mAppInfraInterface.getConfigInterface().setPropertyForKey("JanRainConfiguration." +
                        "RegistrationClientID." + Configuration.DEVELOPMENT
                , "UserRegistration",
                "8kaxdrpvkwyr7pnp987amu4aqb4wmnte",
                configError);
        mAppInfraInterface.getConfigInterface().setPropertyForKey("JanRainConfiguration." +
                        "RegistrationClientID." + Configuration.TESTING
                , "UserRegistration",
                "g52bfma28yjbd24hyjcswudwedcmqy7c",
                configError);
        mAppInfraInterface.getConfigInterface().setPropertyForKey("JanRainConfiguration." +
                        "RegistrationClientID." + Configuration.EVALUATION
                , "UserRegistration",
                "f2stykcygm7enbwfw2u9fbg6h6syb8yd",
                configError);
        mAppInfraInterface.getConfigInterface().setPropertyForKey("JanRainConfiguration." +
                        "RegistrationClientID." + Configuration.STAGING
                , "UserRegistration",
                "f2stykcygm7enbwfw2u9fbg6h6syb8yd",
                configError);
        mAppInfraInterface.getConfigInterface().setPropertyForKey("JanRainConfiguration." +
                        "RegistrationClientID." + Configuration.PRODUCTION
                , "UserRegistration",
                "9z23k3q8bhqyfwx78aru6bz8zksga54u",
                configError);

        mAppInfraInterface.getConfigInterface().setPropertyForKey("PILConfiguration." +
                        "MicrositeID",
                "UserRegistration",
                "77000",
                configError);
        mAppInfraInterface.getConfigInterface().setPropertyForKey("PILConfiguration." +
                        "RegistrationEnvironment",
                "UserRegistration",
                configuration.getValue(),
                configError);

        mAppInfraInterface.
                getConfigInterface().setPropertyForKey("Flow." +
                        "EmailVerificationRequired",
                "UserRegistration",
                "" + true,
                configError);
        mAppInfraInterface.
                getConfigInterface().setPropertyForKey("Flow." +
                        "TermsAndConditionsAcceptanceRequired",
                "UserRegistration",
                "" + true,
                configError);

        String minAge = "{ \"NL\":12 ,\"GB\":0,\"default\": 16}";
        mAppInfraInterface.
                getConfigInterface().setPropertyForKey("Flow." +
                        "MinimumAgeLimit",
                "UserRegistration",
                minAge,
                configError);

        ArrayList<String> providers = new ArrayList<String>();
        providers.add("apple");
        providers.add("facebook");
        providers.add("googleplus");
        mAppInfraInterface.
                getConfigInterface().setPropertyForKey("SigninProviders." +
                        "NL",
                "UserRegistration",
                providers,
                configError);

        mAppInfraInterface.
                getConfigInterface().setPropertyForKey("SigninProviders." +
                        "US",
                "UserRegistration",
                providers,
                configError);

        mAppInfraInterface.
                getConfigInterface().setPropertyForKey("SigninProviders." +
                        "default",
                "UserRegistration",
                providers,
                configError);



        //HSDP configuration
        //initHSDP(configuration);


        String languageCode = Locale.getDefault().getLanguage();
        String countryCode = Locale.getDefault().getCountry();

        PILLocaleManager localeManager = new PILLocaleManager(this);
        localeManager.setInputLocale(languageCode, countryCode);

        URDependancies urDependancies = new URDependancies(mAppInfraInterface);
        URSettings urSettings = new URSettings(this);
        URInterface urInterface = new URInterface();
        urInterface.init(urDependancies, urSettings);

    }

    public void initHSDP(Configuration configuration) {
        if(mAppInfraInterface == null){
            mAppInfraInterface = new AppInfra.Builder().build(this);
        }
        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();
        switch (configuration) {
            case EVALUATION:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.ApplicationName",
                        "UserRegistration",
                        "uGrow",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.Secret",
                        "UserRegistration",
                        "e33a4d97-6ada-491f-84e4-a2f7006625e2",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.Shared",
                        "UserRegistration",
                        "e95f5e71-c3c0-4b52-8b12-ec297d8ae960",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.BaseURL",
                        "UserRegistration",
                        "https://user-registration-assembly-staging.eu-west.philips-healthsuite.com",
                        configError);

                break;
            case DEVELOPMENT:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.ApplicationName",
                        "UserRegistration",
                        "uGrow",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.Secret",
                        "UserRegistration",
                        "c623685e-f02c-11e5-9ce9-5e5517507c66",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.Shared",
                        "UserRegistration",
                        "c62362a0-f02c-11e5-9ce9-5e5517507c66",
                        configError);

                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.BaseURL",
                        "UserRegistration",
                        "https://user-registration-assembly-staging.eu-west.philips-healthsuite.com",
                        configError);

                break;
            case PRODUCTION:
                break;
            case STAGING:
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.ApplicationName",
                        "UserRegistration",
                        "uGrow",
                        configError);
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.Secret",
                        "UserRegistration",
                        "e33a4d97-6ada-491f-84e4-a2f7006625e2",
                        configError);
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.Shared",
                        "UserRegistration",
                        "e95f5e71-c3c0-4b52-8b12-ec297d8ae960",
                        configError);
                mAppInfraInterface.
                        getConfigInterface().setPropertyForKey(
                        "HSDPConfiguration.BaseURL",
                        "UserRegistration",
                        "https://user-registration-assembly-staging.eu-west.philips-healthsuite.com",
                        configError);

                break;
            case TESTING:
                break;
        }
    }
}

