/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.settings;

import android.content.Context;
import android.os.LocaleList;

import com.philips.cdp.localematch.PILLocaleManager;
import com.philips.cdp.registration.BuildConfig;
import com.philips.cdp.registration.datamigration.DataMigration;
import com.philips.cdp.registration.events.NetworStateListener;
import com.philips.cdp.registration.events.NetworkStateHelper;
import com.philips.cdp.registration.events.UserRegistrationHelper;
import com.philips.cdp.registration.listener.UserRegistrationListener;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.security.SecureStorage;
import com.philips.ntputils.ServerTime;
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.appinfra.timesync.TimeInterface;
import com.philips.platform.uappframework.uappinput.UappSettings;

import java.util.Locale;

import javax.inject.Inject;

/**
 * {@code RegistrationHelper} class represents the entry point for User Registration component.
 * It exposes APIs to be used when User Registration is intended to be integrated by any application.
 */
@Deprecated
public class RegistrationHelper {

    @Inject
    NetworkUtility networkUtility;

    @Inject
    TimeInterface timeInterface;

    @Inject
    ABTestClientInterface abTestClientInterface;

    private String countryCode;

    private static volatile RegistrationHelper mRegistrationHelper = null;

    private  RegistrationSettingsURL registrationSettingsURL = new RegistrationSettingsURL();

    private Locale mLocale;

    public UappSettings getUrSettings() {
        return urSettings;
    }

    public void setUrSettings(UappSettings urSettings) {
        this.urSettings = urSettings;
    }

    private UappSettings urSettings;

    private RegistrationHelper() {
        URInterface.getComponent().inject(this);
    }
    /**
     * @return instance of this class
     */
    public synchronized static RegistrationHelper getInstance() {
        if (mRegistrationHelper == null) {
            synchronized (RegistrationHelper.class) {
                if (mRegistrationHelper == null) {
                    mRegistrationHelper = new RegistrationHelper();
                }
            }
        }
        return mRegistrationHelper;
    }

    /*
     * Initialize Janrain
     * {code @initializeUserRegistration} method represents endpoint for integrating
     * applications. It must be called
      * to initialize User Registration component and use its features.
     *
     */
    public synchronized void initializeUserRegistration(final Context context) {
        RLog.init();
        PILLocaleManager localeManager = new PILLocaleManager(context);
        if (localeManager.getLanguageCode() != null && localeManager.getCountryCode() != null) {
            mLocale = new Locale(localeManager.getLanguageCode(), localeManager.getCountryCode());
        }
        if (mLocale == null) {
            throw new RuntimeException("Please set the locale in LocaleMatch");
        }

        countryCode = mLocale.getCountry();

        UserRegistrationInitializer.getInstance().resetInitializationState();
        UserRegistrationInitializer.getInstance().setJanrainIntialized(false);
        generateKeyAndMigrateData(context);
        refreshNTPOffset();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if (networkUtility.isNetworkAvailable()) {

                    UserRegistrationInitializer.getInstance().initializeEnvironment(context, mLocale);
                    //AB Testing initialization
                    abTestClientInterface.updateCache(new ABTestClientInterface.
                            OnRefreshListener() {
                        @Override
                        public void onSuccess() {
                            RLog.d(RLog.AB_TESTING, "SUCESS ");
                        }

                        @Override
                        public void onError(ERRORVALUES error, String message) {
                            RLog.d(RLog.AB_TESTING, "ERROR AB : " + message);
                        }
                    });
                } else {
                    if (UserRegistrationInitializer.getInstance().
                            getJumpFlowDownloadStatusListener() != null) {
                        UserRegistrationInitializer.getInstance().
                                getJumpFlowDownloadStatusListener().onFlowDownloadFailure();
                    }
                }
            }
        };
        Thread thread = new Thread(new Runnable() {
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.
                        THREAD_PRIORITY_MORE_FAVORABLE);
                runnable.run();
            }
        });
        thread.start();
    }
    private void generateKeyAndMigrateData(final Context context) {
        SecureStorage.generateSecretKey();
        new DataMigration(context).checkFileEncryptionStatus();
    }

    private void refreshNTPOffset() {
        ServerTime.init(timeInterface);
        ServerTime.refreshOffset();
    }

    public synchronized String getCountryCode() {
        return countryCode;
    }

    public synchronized void setCountryCode(String country) {
        countryCode = country;
    }

    /**
     * {@code registerUserRegistrationListener} method registers a listener in order to listen
     * the callbacks returned by User Registration component. It must be called by
     * integrating applications
     * to be able to listen to User Registration events.
     *
     * @param userRegistrationListener
     */
    public synchronized void registerUserRegistrationListener(
            UserRegistrationListener userRegistrationListener) {
        UserRegistrationHelper.getInstance().registerEventNotification(userRegistrationListener);
    }

    /**
     * {@code unRegisterUserRegistrationListener} method unregisters the listener registered via
     * {@code registerUserRegistrationListener} method. This will make integrating applications
     * to stop listening to User Registration events.
     *
     * @param userRegistrationListener
     */
    public synchronized void unRegisterUserRegistrationListener(
            UserRegistrationListener userRegistrationListener) {
        UserRegistrationHelper.getInstance().unregisterEventNotification(userRegistrationListener);
    }

    public synchronized UserRegistrationHelper getUserRegistrationListener() {
        return UserRegistrationHelper.getInstance();
    }


    private UserRegistrationListener userRegistrationListener;

    public synchronized UserRegistrationListener getUserRegistrationEventListener() {
        return userRegistrationListener;
    }

    public synchronized UserRegistrationListener setUserRegistrationEventListener
            (UserRegistrationListener userRegistrationListener) {
        return this.userRegistrationListener = userRegistrationListener;
    }


    public synchronized void registerNetworkStateListener(NetworStateListener networStateListener) {
        NetworkStateHelper.getInstance().registerEventNotification(networStateListener);
    }

    public synchronized void unRegisterNetworkListener(NetworStateListener networStateListener) {
        NetworkStateHelper.getInstance().unregisterEventNotification(networStateListener);
    }

    public synchronized NetworkStateHelper getNetworkStateListener() {
        return NetworkStateHelper.getInstance();
    }


    public synchronized Locale getLocale(Context context) {
        RLog.i("Locale", "Locale locale  " + mLocale);
        if (null != mLocale) {
            return mLocale;
        }
        String locale = (new PILLocaleManager(context)).getInputLocale();
        RLog.i("Locale", "Locale from LOcale match" + locale);
        if (locale == null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                return LocaleList.getDefault().get(0);
            }else{
                return Locale.getDefault();
            }
        }
        return new Locale(locale);
    }

    public synchronized static String getRegistrationApiVersion() {
        return BuildConfig.VERSION_NAME;
    }
    public boolean isChinaFlow(){
        return registrationSettingsURL.isChinaFlow();
    }
}
