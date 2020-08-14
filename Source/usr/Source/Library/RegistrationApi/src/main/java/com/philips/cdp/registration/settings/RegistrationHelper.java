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

import com.janrain.android.Jump;
import com.philips.cdp.registration.BuildConfig;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.events.NetworkStateHelper;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.events.UserRegistrationHelper;
import com.philips.cdp.registration.listener.HSDPAuthenticationListener;
import com.philips.cdp.registration.listener.UserRegistrationListener;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.ServerTime;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.timesync.TimeInterface;
import com.philips.platform.uappframework.uappinput.UappSettings;
import com.philips.platform.uid.thememanager.ThemeConfiguration;

import java.util.Locale;

import javax.inject.Inject;

/**
 * {@code RegistrationHelper} class represents the entry point for User Registration component.
 * It exposes APIs to be used when User Registration is intended to be integrated by any application.
 */
public class RegistrationHelper {

    private String TAG = "RegistrationHelper";

    @Inject
    NetworkUtility networkUtility;

    @Inject
    TimeInterface timeInterface;

    ThemeConfiguration themeConfiguration;

    int theme;

    private String countryCode;

    private static volatile RegistrationHelper mRegistrationHelper = null;

    private RegistrationSettingsURL registrationSettingsURL = new RegistrationSettingsURL();

    private Locale mLocale;

    public UappSettings getUrSettings() {
        return urSettings;
    }

    public void setUrSettings(UappSettings urSettings) {
        this.urSettings = urSettings;
    }

    private UappSettings urSettings;
    private JanrainDataImpln mJanrainDataImpln;

    private RegistrationHelper() {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        SecureStorageInterface mSecureStorageInterface = RegistrationConfiguration.getInstance().getComponent().getSecureStorageInterface();
        mJanrainDataImpln = new JanrainDataImpln(mSecureStorageInterface);
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

    public void initializeJump(Context context){
        Jump.init(context, mJanrainDataImpln, mJanrainDataImpln);
    }

    /*
     * Initialize Janrain
     * {code @initializeUserRegistration} method represents endpoint for integrating
     * applications. It must be called
     * to initialize User Registration component and use its features.
     *
     */
    public void initializeUserRegistration(final Context context) {
        if (mLocale == null) {
            String languageCode;
            String countryCode;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                languageCode = LocaleList.getDefault().get(0).getLanguage();
                countryCode = LocaleList.getDefault().get(0).getCountry();
            } else {
                languageCode = Locale.getDefault().getLanguage();
                countryCode = Locale.getDefault().getCountry();
            }

            RLog.i(TAG, "initializeUserRegistration : setLocale : "
                    + languageCode + "_" + countryCode);
            setLocale(languageCode, countryCode);

        }
        UserRegistrationInitializer.getInstance().resetInitializationState();
        UserRegistrationInitializer.getInstance().setJanrainIntialized(false);
        //   generateKeyAndMigrateData(context);
        refreshNTPOffset();
        final Runnable runnable = () -> {
            deleteLegacyDIProfileFile(context);
            if (networkUtility.isNetworkAvailable()) {
                RLog.i(TAG, "initializeUserRegistration :" +
                        " initializeEnvironment for Locale: " + mLocale);
                UserRegistrationInitializer.getInstance().initializeEnvironment(context, mLocale);
            } else {
                if (UserRegistrationInitializer.getInstance().
                        getJumpFlowDownloadStatusListener() != null) {
                    UserRegistrationInitializer.getInstance().
                            getJumpFlowDownloadStatusListener().onFlowDownloadFailure();
                    RLog.e(TAG, "initializeUserRegistration: onFlowDownloadFailure due Network is not Available");
                }
            }
        };
        Thread thread = new Thread(() -> {
            android.os.Process.setThreadPriority(android.os.Process.
                    THREAD_PRIORITY_MORE_FAVORABLE);
            runnable.run();
        });
        thread.start();
    }

    private void deleteLegacyDIProfileFile(Context context) {
        RLog.d(TAG, "deleteLegacyDIProfileFile is called");
        context.deleteFile(RegConstants.DI_PROFILE_FILE);
        mJanrainDataImpln.removeValueForKey(RegConstants.DI_PROFILE_FILE);
    }


    private void refreshNTPOffset() {
        RLog.d(TAG, "refreshNTPOffset is called ");
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

    /**
     * {@code registerHSDPAuthenticationListener} method registers a listener in order to listen
     * the callbacks returned by User Registration component. It must be called by
     * integrating applications
     * to be able to listen to User Registration events.
     *
     * @param hsdpAuthenticationListener
     */
    public synchronized void registerHSDPAuthenticationListener(
            HSDPAuthenticationListener hsdpAuthenticationListener) {
        UserRegistrationHelper.getInstance().registerHSDPAuthenticationEventNotification(hsdpAuthenticationListener);
    }

    /**
     * {@code unRegisterHSDPAuthenticationListener} method unregisters the listener registered via
     * {@code registerUserRegistrationListener} method. This will make integrating applications
     * to stop listening to User Registration events.
     *
     * @param hsdpAuthenticationListener
     */
    public synchronized void unRegisterHSDPAuthenticationListener(
            HSDPAuthenticationListener hsdpAuthenticationListener) {
        UserRegistrationHelper.getInstance().unregisterHSDPAuthenticationEventNotification(hsdpAuthenticationListener);
    }

    public synchronized UserRegistrationHelper getUserRegistrationListener() {
        return UserRegistrationHelper.getInstance();
    }

    public synchronized void registerNetworkStateListener(NetworkStateListener networStateListener) {
        NetworkStateHelper.getInstance().registerEventNotification(networStateListener);
    }

    public synchronized void unRegisterNetworkListener(NetworkStateListener networStateListener) {
        NetworkStateHelper.getInstance().unregisterEventNotification(networStateListener);
    }

    public synchronized NetworkStateHelper getNetworkStateListener() {
        return NetworkStateHelper.getInstance();
    }


    public void setLocale(String languageCode, String countryCode) {
        RLog.d(TAG, "setLocale language " + languageCode + " country " + countryCode);
        mLocale = new Locale(languageCode, countryCode);
    }

    public synchronized Locale getLocale() {
        RLog.d(TAG, "Locale getLocale  " + mLocale);
        return mLocale;
    }

    public synchronized String getLocaleWithHyphen() {
        String locale = null;
        if(mLocale.toString().contains("_")){
            locale = (mLocale.toString().replace("_", "-"));
        }
        return locale;
    }

    public synchronized static String getRegistrationApiVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public boolean isMobileFlow() {
        RLog.d(TAG, "isMobileFlow : " + registrationSettingsURL.isMobileFlow());
        return registrationSettingsURL.isMobileFlow();
    }

    public ThemeConfiguration getThemeConfiguration() {
        return themeConfiguration;
    }

    public void setThemeConfiguration(ThemeConfiguration themeConfiguration) {
        this.themeConfiguration = themeConfiguration;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }
}
