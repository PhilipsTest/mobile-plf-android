package com.philips.platform.pim.manager;


import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.pim.BuildConfig;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.uappframework.uappinput.UappDependencies;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;


public class PIMSettingManager {
    private static final String COMPONENT_TAGS_ID = "pim";
    private static final PIMSettingManager instance = new PIMSettingManager();
    private AppInfraInterface mAppInfraInterface;
    private LoggingInterface mLoggingInterface;
    private AppTaggingInterface mTaggingInterface;
    private PIMOIDCConfigration mPimoidcConfigration;
    private PIMUserManager pimUserManager;
    private RestInterface mRestInterface;
    private final String TAG = PIMSettingManager.class.getSimpleName();
    private String locale;

    public static PIMSettingManager getInstance() {
        return instance;
    }


    void setPimOidcConfigration(PIMOIDCConfigration pimOidcConfigration) {
        mPimoidcConfigration = pimOidcConfigration;
    }


    public PIMOIDCConfigration getPimOidcConfigration() {
        return mPimoidcConfigration;
    }


    public void init(UappDependencies pimDependencies) {
        mAppInfraInterface = pimDependencies.getAppInfra();
        mLoggingInterface = mAppInfraInterface.getLogging().createInstanceForComponent(COMPONENT_TAGS_ID, BuildConfig.VERSION_NAME);
        mTaggingInterface = mAppInfraInterface.getTagging().createInstanceForComponent(COMPONENT_TAGS_ID, BuildConfig.VERSION_NAME);
        mRestInterface = mAppInfraInterface.getRestClient();
        mLoggingInterface.log(DEBUG,TAG,"PIMSettingManager : dependecies initialized");
    }


    public AppInfraInterface getAppInfraInterface() {
        return mAppInfraInterface;
    }

    public LoggingInterface getLoggingInterface() {
        return mLoggingInterface;
    }

    public AppTaggingInterface getTaggingInterface() {
        return mTaggingInterface;
    }


    public PIMUserManager getPimUserManager() {
        return pimUserManager;
    }

    public void setPimUserManager(PIMUserManager pimUserManager) {
        this.pimUserManager = pimUserManager;
    }

    RestInterface getRestClient() {
        return mRestInterface;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        if(locale.contains("_")){
            String[] splitLocal = locale.split("_");
            locale = splitLocal[0]+"-"+splitLocal[1];
        }
        this.locale = locale;
    }
}
