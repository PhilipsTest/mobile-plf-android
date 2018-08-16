/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.platform.baseapp.base;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.philips.cdp.cloudcontroller.DefaultCloudController;
import com.philips.cdp.cloudcontroller.api.CloudController;
import com.philips.cdp.uikit.utils.UikitLocaleHelper;
import com.philips.cdp2.commlib.ble.context.BleTransportContext;
import com.philips.cdp2.commlib.cloud.context.CloudTransportContext;
import com.philips.cdp2.commlib.core.CommCentral;
import com.philips.cdp2.commlib.core.configuration.RuntimeConfiguration;
import com.philips.cdp2.commlib.lan.context.LanTransportContext;
import com.philips.platform.appframework.abtesting.AbTestingImpl;
import com.philips.platform.appframework.BuildConfig;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.abtesting.FireBaseWrapper;
import com.philips.platform.appframework.connectivity.demouapp.RefAppApplianceFactory;
import com.philips.platform.appframework.connectivity.demouapp.RefAppKpsConfigurationInfo;
import com.philips.platform.appframework.flowmanager.FlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.listeners.FlowManagerListener;
import com.philips.platform.appframework.stateimpl.DemoDataServicesState;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;
import com.philips.platform.appinfra.consentmanager.consenthandler.DeviceStoredConsentHandler;
import com.philips.platform.appinfra.languagepack.LanguagePackInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.baseapp.screens.consumercare.SupportFragmentState;
import com.philips.platform.baseapp.screens.inapppurchase.IAPRetailerFlowState;
import com.philips.platform.baseapp.screens.inapppurchase.IAPState;
import com.philips.platform.baseapp.screens.myaccount.MyAccountState;
import com.philips.platform.baseapp.screens.neura.NeuraConsentProvider;
import com.philips.platform.baseapp.screens.privacysettings.PrivacySettingsState;
import com.philips.platform.baseapp.screens.productregistration.ProductRegistrationState;
import com.philips.platform.baseapp.screens.telehealthservices.TeleHealthServicesState;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationOnBoardingState;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationState;
import com.philips.platform.baseapp.screens.utility.BaseAppUtil;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.receivers.ConnectivityChangeReceiver;
import com.philips.platform.referenceapp.PushNotificationManager;
import com.squareup.leakcanary.LeakCanary;

/**
 * Application class is used for initialization
 */
public class AppFrameworkApplication extends Application {
    private static final String LOG = AppFrameworkApplication.class.getSimpleName();
    private static final String LEAK_CANARY_BUILD_TYPE = "leakCanary";
    public static final String Apteligent_APP_ID = "69bb94377f0949908f3eeba142b8b21100555300";
    public AppInfraInterface appInfra;
    public static LoggingInterface loggingInterface;
    protected FlowManager targetFlowManager;
    private UserRegistrationState userRegistrationState;
    private IAPState iapState;
    private DemoDataServicesState dataSyncScreenState;
    private ProductRegistrationState productRegistrationState;
    private static boolean isChinaCountry = false;
    private PushNotificationManager pushNotificationManager;
    private LanguagePackInterface languagePackInterface;
    private ConnectivityChangeReceiver connectivityChangeReceiver;
    private CommCentral commCentral = null;
    private RefAppApplianceFactory applianceFactory;
    private MyAccountState myAccountState;
    private static boolean appDataInitializationStatus;
    private SupportFragmentState supportFragmentState;
    private TeleHealthServicesState teleHealthServicesState;
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
        super.onCreate();
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
        initDataServiceState();
        RALog.d(LOG, "DS state end::");
        initializeIAP();
        /*
         * Initializing tagging class and its interface. Interface initialization needs
         * context to gets started.
         */
        RALog.d(LOG, "PN state begin::");
        if (BaseAppUtil.isDSPollingEnabled(getApplicationContext())) {
            RALog.d(PushNotificationManager.TAG, "Polling is enabled");
        } else {
            RALog.d(PushNotificationManager.TAG, "Push notification is enabled");
            pushNotificationManager = PushNotificationManager.getInstance();
            pushNotificationManager.init(appInfra, getDataServiceState());
            pushNotificationManager.startPushNotificationRegistration(getApplicationContext());
            connectivityChangeReceiver = new ConnectivityChangeReceiver();
            registerReceiver(connectivityChangeReceiver,
                    new IntentFilter(
                            ConnectivityManager.CONNECTIVITY_ACTION));
        }
        RALog.d("test", "onCreate end::");
        appDataInitializationStatus = true;
        callback.onAppStatesInitialization();
        initializeCC();
        initializeTHS();
        registerNeuraHandler();
    }
    private void initializePrivacySettings() {
        privacySettingsState = new PrivacySettingsState();
        privacySettingsState.init(this);
    }
    private void initializeTHS() {
        teleHealthServicesState = new TeleHealthServicesState();
        teleHealthServicesState.init(this);
    }

    private void initializeCC() {
        supportFragmentState = new SupportFragmentState();
        supportFragmentState.init(this);
    }

    public void initUserRegistrationState() {
        userRegistrationState = new UserRegistrationOnBoardingState();
        userRegistrationState.init(this);
    }

    private void registerNeuraHandler() {
        new NeuraConsentProvider(new DeviceStoredConsentHandler(appInfra)).registerConsentHandler(appInfra);
    }

    public UserRegistrationState getUserRegistrationState() {
        return userRegistrationState;
    }

    public void initDataServiceState() {
        dataSyncScreenState = new DemoDataServicesState();
        dataSyncScreenState.init(this);
        DataServicesManager.getInstance().synchronize();
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

    public DemoDataServicesState getDataServiceState() {
        if (dataSyncScreenState == null) {
            initDataServiceState();
        }
        return dataSyncScreenState;
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
        if (connectivityChangeReceiver != null) {
            unregisterReceiver(connectivityChangeReceiver);
        }
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

    public synchronized CommCentral getCommCentralInstance() {
        if (commCentral == null) {
            commCentral = createCommCentral(getApplicationContext(), getAppInfra());
        }
        RALog.i(TAG, "getCommCentralInstance - " + commCentral);
        return commCentral;
    }

    public RefAppApplianceFactory getApplianceFactory() {
        return applianceFactory;
    }

    private CommCentral createCommCentral(final @NonNull Context context, final @NonNull AppInfraInterface appInfraInterface) {
        final RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(context, appInfraInterface);
        runtimeConfiguration.setTaggingEnabled(true);
        final CloudController cloudController = setupCloudController(context);

        final CloudTransportContext cloudTransportContext = new CloudTransportContext(runtimeConfiguration, cloudController);
        final BleTransportContext bleTransportContext = new BleTransportContext(runtimeConfiguration, true);
        final LanTransportContext lanTransportContext = new LanTransportContext(runtimeConfiguration);

        applianceFactory = new RefAppApplianceFactory(bleTransportContext, lanTransportContext, cloudTransportContext);
        return new CommCentral(applianceFactory, runtimeConfiguration, bleTransportContext, lanTransportContext, cloudTransportContext);
    }

    @NonNull
    private CloudController setupCloudController(final @NonNull Context context) {
        final CloudController cloudController = new DefaultCloudController(context, new RefAppKpsConfigurationInfo());

        String ICPClientVersion = cloudController.getICPClientVersion();
        RALog.d(TAG, "ICPClientVersion - " + ICPClientVersion);

        return cloudController;
    }

    /***
     * Initializar app infra
     *
     * @param appInfraInitializationCallback
     */
    public void initializeAppInfra(AppInitializationCallback.AppInfraInitializationCallback appInfraInitializationCallback) {
        new FireBaseWrapper().initFireBase();
        Log.d("firebase instance id - ", FirebaseInstanceId.getInstance().getToken());
        AbTestingImpl abTestingImpl = new AbTestingImpl();
        AppInfra.Builder builder = new AppInfra.Builder();
        builder.setAbTesting(abTestingImpl);
        appInfra = builder.build(getApplicationContext());
        abTestingImpl.initAbTesting(appInfra);
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
                        UikitLocaleHelper.getUikitLocaleHelper().setFilePath(filePath);
                        RALog.d(LOG, "Success langauge pack activate " + "---" + filePath);
                    }

                    @Override
                    public void onError(AILPActivateResult ailpActivateResult, String s) {
                        RALog.e(LOG, ailpActivateResult.toString() + "---" + s);
                    }
                });
            }
        });
    }

}
