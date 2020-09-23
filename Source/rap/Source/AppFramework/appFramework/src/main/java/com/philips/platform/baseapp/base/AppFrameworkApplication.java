/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.platform.baseapp.base;

import android.app.Application;
import android.os.StrictMode;

import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.philips.platform.appframework.BuildConfig;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.abtesting.AbTestingImpl;
import com.philips.platform.appframework.googleanalytics.AnalyticsImplementor;
import com.philips.platform.appframework.flowmanager.FlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.listeners.FlowManagerListener;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;
import com.philips.platform.appinfra.consentmanager.FetchConsentCallback;
import com.philips.platform.appinfra.languagepack.LanguagePackInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.baseapp.screens.consumercare.SupportFragmentState;
import com.philips.platform.baseapp.screens.inapppurchase.IAPRetailerFlowState;
import com.philips.platform.baseapp.screens.inapppurchase.IAPState;
import com.philips.platform.baseapp.screens.myaccount.MyAccountState;
import com.philips.platform.baseapp.screens.privacysettings.PrivacySettingsState;
import com.philips.platform.baseapp.screens.productregistration.ProductRegistrationState;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationOnBoardingState;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationState;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.pif.chi.datamodel.ConsentDefinitionStatus;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.philips.platform.uid.utils.UIDLocaleHelper;
import com.squareup.leakcanary.LeakCanary;

/**
 * Application class is used for initialization
 */
public class AppFrameworkApplication extends Application {
    private static final String LOG = AppFrameworkApplication.class.getSimpleName();
    private static final String LEAK_CANARY_BUILD_TYPE = "leakCanary";
    private static final String PSRA_BUILD_TYPE = "psraRelease";
    public static final String Apteligent_APP_ID = "69bb94377f0949908f3eeba142b8b21100555300";
    public AppInfraInterface appInfra;
    public static LoggingInterface loggingInterface;
    protected FlowManager targetFlowManager;
    private UserRegistrationState userRegistrationState;

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }

    private static FirebaseAnalytics firebaseAnalytics;
    private IAPState iapState;

    private ProductRegistrationState productRegistrationState;
    private static boolean isChinaCountry = false;

    private LanguagePackInterface languagePackInterface;



    private MyAccountState myAccountState;
    private static boolean appDataInitializationStatus;
    private SupportFragmentState supportFragmentState;

    private PrivacySettingsState privacySettingsState;

    public static boolean isAppDataInitialized() {
        return appDataInitializationStatus;
    }

    public static final String TAG = AppFrameworkApplication.class.getSimpleName();

    public boolean isShopingCartVisible() {
        return isShopingCartVisible;
    }

    public void setShopingCartVisible(boolean shopingCartVisible) {
        isShopingCartVisible = shopingCartVisible;
    }

    public boolean isShopingCartVisible = false;


    @Override
    public void onCreate() {
        applyStrictMode();
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(LEAK_CANARY_BUILD_TYPE)) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This proisChinaCountrycess is dedicated to LeakCanary for heap analysis.
                // You should not initialise your app in this process.
                return;
            }
            LeakCanary.install(this);
        }
        if (!BuildConfig.BUILD_TYPE.equalsIgnoreCase(PSRA_BUILD_TYPE)) {

        }
        super.onCreate();

        if(BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug")) {
            //Facebook stetho library is used for browsing database using chrome developer tool.
            Stetho.initializeWithDefaults(this);
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
    }

    /**
     * Initialize app states
     *
     * @param callback
     */
    public void initialize(AppInitializationCallback.AppStatesInitializationCallback callback) {
        RALog.d(LOG, "UR state begin::");
        initUserRegistrationState();
        RALog.d(LOG, "UR state end::");
        RALog.d(LOG, "China flow state begin::");
        determineChinaFlow();
        RALog.d(LOG, "China flow state end::");
        RALog.d(LOG, "PR state begin::");
        productRegistrationState = new ProductRegistrationState();
        productRegistrationState.init(this);
        RALog.d(LOG, "PR state end::");
        //  determineHybrisFlow();
        RALog.d(LOG, "Mya state begin::");
        initializeMya();
        RALog.d(LOG, "Mya state end::");
        RALog.d(LOG, "Privacy settings state begin::");
        initializePrivacySettings();
        RALog.d(LOG, "Privacy settings state end::");
        RALog.d(LOG, "DS state begin::");

        RALog.d(LOG, "DS state end::");
        initializeIAP();
        /*
         * Initializing tagging class and its interface. Interface initialization needs
         * context to gets started.
         */
        RALog.d(LOG, "PN state begin::");
        RALog.d("test", "onCreate end::");
        appDataInitializationStatus = true;
        callback.onAppStatesInitialization();
        initializeCC();
    }
    private void initializePrivacySettings() {
        privacySettingsState = new PrivacySettingsState();
        privacySettingsState.init(this);
    }


    private void initializeCC() {
        supportFragmentState = new SupportFragmentState();
        supportFragmentState.init(this);
    }

    public void initUserRegistrationState() {
        userRegistrationState = new UserRegistrationOnBoardingState();
        userRegistrationState.init(this);
    }

    public UserRegistrationState getUserRegistrationState() {
        return userRegistrationState;
    }

    public LoggingInterface getLoggingInterface() {
        return loggingInterface;
    }

    public AppInfraInterface getAppInfra() {
        return appInfra;
    }

    public AppIdentityInterface.AppState getAppState() {
        return getAppInfra().getAppIdentity().getAppState();
    }

    public IAPState getIap() {
        return iapState;
    }

    public BaseFlowManager getTargetFlowManager() {
        return targetFlowManager;
    }

    public void setTargetFlowManager(FlowManagerListener flowManagerListener) {
        this.targetFlowManager = new FlowManager();
        this.targetFlowManager.initialize(getApplicationContext(), R.raw.appflow, flowManagerListener);
    }

    public boolean isChinaFlow() {
        return isChinaCountry;
    }

    public void determineChinaFlow() {
        AppInfraInterface appInfraInterface = getAppInfra();
        ServiceDiscoveryInterface serviceDiscovery = appInfraInterface.getServiceDiscovery();

        serviceDiscovery.getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
            @Override
            public void onSuccess(String s, SOURCE source) {
                if (s.equals("CN")) {
                    isChinaCountry = true;
                } else {
                    isChinaCountry = false;
                }
            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
                isChinaCountry = false;
            }
        });
    }




    private void initializeMya() {
        myAccountState = new MyAccountState();
        myAccountState.init(this);
    }

    public void initializeIAP() {
        RALog.d(LOG, "IAP state begin::");
        iapState = new IAPRetailerFlowState();
        iapState.init(this);
        try {
            getIap().isCartVisible();

        } catch (RuntimeException rte) {
            setShopingCartVisible(false);
        }
        RALog.d(LOG, "IAP state end::");
    }

    @Override
    public void onTerminate() {

        AppFrameworkTagging.getInstance().releaseApteligentReceiver();

        super.onTerminate();
    }

    private void applyStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }







    /***
     * Initializar app infra
     *
     * @param appInfraInitializationCallback
     */
    public void initializeAppInfra(AppInitializationCallback.AppInfraInitializationCallback appInfraInitializationCallback) {
        AbTestingImpl abTestingImpl = new AbTestingImpl();
        abTestingImpl.initFireBase(this);
        AppInfra.Builder builder = new AppInfra.Builder();

        builder.setAbTesting(abTestingImpl);
        builder.setAnalytics(new AnalyticsImplementor());
        appInfra = builder.build(getApplicationContext());

        abTestingImpl.initAbTesting(appInfra);
        abTestingImpl.enableDeveloperMode(true);
        loggingInterface = appInfra.getLogging();
        RALog.init(appInfra);
        AppFrameworkTagging.getInstance().initAppTaggingInterface(this);
        appInfraInitializationCallback.onAppInfraInitialization();
        languagePackInterface = appInfra.getLanguagePack();
        languagePackInterface.refresh(new LanguagePackInterface.OnRefreshListener() {
            @Override
            public void onError(AILPRefreshResult ailpRefreshResult, String s) {
                RALog.e(LOG, ailpRefreshResult.toString() + "---" + s);
            }

            @Override
            public void onSuccess(AILPRefreshResult ailpRefreshResult) {
                languagePackInterface.activate(new LanguagePackInterface.OnActivateListener() {
                    @Override
                    public void onSuccess(String filePath) {
                        UIDLocaleHelper.getInstance().setFilePath(filePath);
                        RALog.d(LOG, "Success language pack activate " + "---" + filePath);
                    }

                    @Override
                    public void onError(AILPActivateResult ailpActivateResult, String s) {
                        RALog.e(LOG, ailpActivateResult.toString() + "---" + s);
                    }
                });
            }
        });
        ConsentDefinition consentDefinition = appInfra.getConsentManager().getConsentDefinitionForType(appInfra.getAbTesting().getAbTestingConsentIdentifier());
        if (consentDefinition != null) {
            appInfra.getConsentManager().fetchConsentState(consentDefinition, new FetchConsentCallback() {
                @Override
                public void onGetConsentSuccess(ConsentDefinitionStatus consentStatus) {
                    if (consentStatus.getConsentState() == ConsentStates.active) {
                        FirebaseAnalytics.getInstance(getApplicationContext()).setAnalyticsCollectionEnabled(true);
                        abTestingImpl.updateCache(new ABTestClientInterface.OnRefreshListener() {
                            @Override
                            public void onSuccess() {
                                RALog.d(LOG, "ab-testing cache updated successfully");
                                RALog.d("FireBase instance id - ", FirebaseInstanceId.getInstance().getToken());
                            }

                            @Override
                            public void onError(ERRORVALUE error) {
                                RALog.d(LOG, "ab-testing update failed");
                            }
                        });
                    } else {
                        RALog.d(LOG, "ab-testing consent set to false");
                        FirebaseAnalytics.getInstance(getApplicationContext()).setAnalyticsCollectionEnabled(false);
                        FirebaseAnalytics.getInstance(getApplicationContext()).resetAnalyticsData();
                    }
                }

                @Override
                public void onGetConsentFailed(ConsentError error) {
                    RALog.d(getClass().getSimpleName(), "error while fetching ab-testing consent ");
                }
            });
        } else
            RALog.d(LOG, "consent definition is null");

    }

}
