/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.appframework;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.philips.cdp.localematch.PILLocaleManager;
import com.philips.cdp.prodreg.register.ProdRegHelper;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.AppInfraSingleton;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.modularui.statecontroller.UIFlowManager;

import java.util.Locale;

public class AppFrameworkApplication extends Application {
    public UIFlowManager flowManager;
    private static Context mContext;
    public static AppInfraInterface gAppInfra;
    public static LoggingInterface loggingInterface;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        mContext = getApplicationContext();
        flowManager = new UIFlowManager();
        AppInfraSingleton.setInstance(gAppInfra = new AppInfra.Builder().build(getApplicationContext()));
        gAppInfra = AppInfraSingleton.getInstance();
        loggingInterface = gAppInfra.getLogging().createInstanceForComponent(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME);
        loggingInterface.enableConsoleLog(true);
        loggingInterface.enableFileLog(true);
        setLocale();
        initializeUserRegistrationLibrary();
        initializeProductRegistrationLibrary();
    }

    private void setLocale() {
        String languageCode = Locale.getDefault().getLanguage();
        String countryCode = Locale.getDefault().getCountry();

        PILLocaleManager localeManager = new PILLocaleManager(this);
        localeManager.setInputLocale(languageCode, countryCode);
    }

    private void initializeProductRegistrationLibrary() {
        ProdRegHelper prodRegHelper = new ProdRegHelper();
        prodRegHelper.init(this);

    }

    public UIFlowManager getFlowManager() {
        return flowManager;
    }

    public static Context getContext() {
        return mContext;
    }

    private void initializeUserRegistrationLibrary() {
        AppTaggingInterface aiAppTaggingInterface = gAppInfra.getTagging();
        aiAppTaggingInterface.createInstanceForComponent("User Registration",
                RegistrationHelper.getRegistrationApiVersion());
        aiAppTaggingInterface.setPreviousPage("demoapp:home");
        aiAppTaggingInterface.setPrivacyConsent(AppTaggingInterface.PrivacyStatus.OPTIN);

        RegistrationConfiguration.getInstance().
                setPrioritisedFunction(RegistrationFunction.Registration);
        RLog.init(this);
        RegistrationHelper.getInstance().initializeUserRegistration(this);
    }
}
