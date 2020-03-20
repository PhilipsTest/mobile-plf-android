
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.janrain.android.Jump;
import com.philips.cdp.registration.app.infra.ServiceDiscoveryWrapper;
import com.philips.cdp.registration.configuration.Configuration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.events.JumpFlowDownloadStatusListener;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.ThreadUtils;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class UserRegistrationInitializer {

    private String TAG = "UserRegistrationInitializer";

    @Inject
    ServiceDiscoveryInterface serviceDiscoveryInterface;

    private boolean mIsJumpInitializationInProgress;

    private boolean mReceivedDownloadFlowSuccess;

    private boolean mReceivedProviderFlowSuccess;

    private boolean mJanrainIntialized = false;

    private boolean isRefreshUserSessionInProgress = false;

    private String locale;

    private static volatile UserRegistrationInitializer mUserRegistrationInitializer;

    @Inject
    ServiceDiscoveryWrapper serviceDiscoveryWrapper;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private UserRegistrationInitializer() {
        RLog.d(TAG, "UserRegistrationInitializer");
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        mHandler = new Handler();
    }

    public void resetInitializationState() {
        RLog.d(TAG, "resetInitializationState");
        mIsJumpInitializationInProgress = false;
        mReceivedDownloadFlowSuccess = false;
        mReceivedProviderFlowSuccess = false;
    }

    private final int CALL_AFTER_DELAY = 1000;
    private Handler mHandler;

    public boolean isJumpInitializationInProgress() {
        RLog.d(TAG, "isJumpInitializationInProgress");
        return mIsJumpInitializationInProgress;
    }

    public void setJumpInitializationInProgress(boolean isInitializationInProgress) {
        RLog.d(TAG, "setJumpInitializationInProgress : " + isInitializationInProgress);
        this.mIsJumpInitializationInProgress = isInitializationInProgress;
    }


    public JumpFlowDownloadStatusListener getJumpFlowDownloadStatusListener() {
        RLog.d(TAG, "getJumpFlowDownloadStatusListener : " + mJumpFlowDownloadStatusListener);
        return mJumpFlowDownloadStatusListener;
    }


    private JumpFlowDownloadStatusListener mJumpFlowDownloadStatusListener;


    private RegistrationSettings mRegistrationSettings;


    public boolean isJanrainIntialized() {
        RLog.d(TAG, "isJanrainIntialized : " + mJanrainIntialized);
        return mJanrainIntialized;
    }

    public void setJanrainIntialized(boolean janrainIntializationStatus) {
        RLog.d(TAG, "setJanrainIntialized : " + janrainIntializationStatus);
        mJanrainIntialized = janrainIntializationStatus;
    }

    public void registerJumpFlowDownloadListener(JumpFlowDownloadStatusListener pJumpFlowDownloadStatusListener) {
        RLog.d(TAG, "registerJumpFlowDownloadListener ");
        this.mJumpFlowDownloadStatusListener = pJumpFlowDownloadStatusListener;
    }

    public void unregisterJumpFlowDownloadListener() {
        RLog.d(TAG, "unregisterJumpFlowDownloadListener ");
        this.mJumpFlowDownloadStatusListener = null;
    }

    public final BroadcastReceiver janrainStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            RLog.d(TAG, "janrainStatusReceiver :  onReceive");
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null && extras.getString("message").equalsIgnoreCase("Download flow Success!!")) {
                    RLog.d(TAG, "janrainStatusReceiver :  Download flow Success!!");
                    mReceivedDownloadFlowSuccess = true;
                } else if (extras != null && extras.getString("message").equalsIgnoreCase("Provider flow Success!!")) {
                    RLog.d(TAG, "janrainStatusReceiver :  Provider flow Success!!");
                    mReceivedProviderFlowSuccess = true;
                }
                RLog.d(TAG, "janrainStatusReceiver, intent = " + intent.toString());
                if (Jump.JR_DOWNLOAD_FLOW_SUCCESS.equalsIgnoreCase(intent.getAction()) || Jump.JR_PROVIDER_FLOW_SUCCESS.equalsIgnoreCase(intent.getAction())) {

                    if (mReceivedDownloadFlowSuccess && mReceivedProviderFlowSuccess) {
                        RLog.d(TAG, "mReceivedDownloadFlowSuccess : " + mReceivedDownloadFlowSuccess + "and mReceivedProviderFlowSuccess : " + mReceivedProviderFlowSuccess);
                        mJanrainIntialized = true;
                        mIsJumpInitializationInProgress = false;
                        mReceivedDownloadFlowSuccess = false;
                        mReceivedProviderFlowSuccess = false;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mJumpFlowDownloadStatusListener != null) {
                                    mJumpFlowDownloadStatusListener.onFlowDownloadSuccess();
                                }
                                ThreadUtils.postInMainThread(context, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.JANRAIN_INIT_SUCCESS));
                            }
                        }, CALL_AFTER_DELAY);
                    }

                } else if (Jump.JR_FAILED_TO_DOWNLOAD_FLOW.equalsIgnoreCase(intent.getAction())
                        && (extras != null)) {
                    RLog.e(TAG, "janrainStatusReceiver : Janrain flow download failed");
                    mIsJumpInitializationInProgress = false;
                    mJanrainIntialized = false;
                    mReceivedDownloadFlowSuccess = false;
                    mReceivedProviderFlowSuccess = false;
                    if (mJumpFlowDownloadStatusListener != null) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mJumpFlowDownloadStatusListener != null) {
                                    mJumpFlowDownloadStatusListener.onFlowDownloadFailure();
                                }
                            }
                        }, CALL_AFTER_DELAY);

                    }
                    ThreadUtils.postInMainThread(context, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.JANRAIN_INIT_SUCCESS));
                }
            }
        }
    };


    public void initializeConfiguredEnvironment(final Context context, final Configuration registrationType, final String initLocale) {
        RLog.d(TAG, "initializeConfiguredEnvironment");
        mRegistrationSettings = new RegistrationSettingsURL();

        serviceDiscoveryInterface.getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
            @Override
            public void onSuccess(String s, SOURCE source) {
                if (source == null) return;
                RLog.d(TAG, "onSuccess : Service discovry getHomeCountry : " + s + " and SOURCE : " + source.name());
                if (RegUtility.supportedCountryList().contains(s.toUpperCase())) {
                    RegistrationHelper.getInstance().setCountryCode(s);
                    RLog.d(TAG, "onSuccess : setCountryCode(s) supportedCountryList matched" + s);
                } else {
                    String fallbackCountryCode = RegUtility.getFallbackCountryCode();
                    serviceDiscoveryInterface.setHomeCountry(fallbackCountryCode.toUpperCase());
                    RegistrationHelper.getInstance().setCountryCode(fallbackCountryCode.toUpperCase());
                    RLog.d(TAG, "onSuccess : setHomeCountry(s) supportedCountryList not matched" + fallbackCountryCode.toUpperCase());
                }
                RLog.d(TAG, " Country :" + RegistrationHelper.getInstance().getCountryCode());
                getLocaleServiceDiscoveryByCountry(context, registrationType);

            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
                String fallbackCountry = RegUtility.getFallbackCountryCode();
                serviceDiscoveryInterface.setHomeCountry(fallbackCountry);
                RegistrationHelper.getInstance().setCountryCode(fallbackCountry);
                RLog.d(TAG, "onError : setHomeCountry(s)" + fallbackCountry);
                RLog.d(TAG, " Country :" + RegistrationHelper.getInstance().getCountryCode());
                getLocaleServiceDiscoveryByCountry(context, registrationType);

            }
        });
    }

    private void getLocaleServiceDiscovery(Context context, Configuration registrationType) {
        serviceDiscoveryWrapper.getServiceLocaleWithLanguagePreferenceSingle("userreg.janrain.api.v2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String verificationUrl) {
                        if (verificationUrl != null && !verificationUrl.isEmpty()) {
                            RLog.d(TAG, "getLocaleServiceDiscovery : onSuccess : " + verificationUrl);
                            updateAppLocale(verificationUrl, context, registrationType);
                        } else {
                            RLog.d(TAG, "calling getLocaleServiceDiscoveryByCountry from onSuccess as verification url is null or empty: ");
                            getLocaleServiceDiscoveryByCountry(context, registrationType);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        RLog.e(TAG, "getLocaleServiceDiscovery : onError: So calling getLocaleServiceDiscoveryByCountry " + e.getMessage());
                        getLocaleServiceDiscoveryByCountry(context, registrationType);
                    }
                });
    }

    private void getLocaleServiceDiscoveryByCountry(Context context, Configuration registrationType) {
        serviceDiscoveryWrapper.getServiceLocaleWithCountryPreferenceSingle("userreg.janrain.api.v2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String verificationUrl) {
                        RLog.d(TAG, "getLocaleServiceDiscoveryByCountry : onSuccess : " + verificationUrl);
                        updateAppLocale(verificationUrl, context, registrationType);
                    }

                    @Override
                    public void onError(Throwable e) {
                        RLog.e(TAG, "getLocaleServiceDiscovery : onError: So notify JANRAIN_INIT_FAILURE " + e.getMessage());
                        ThreadUtils.postInMainThread(context, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.JANRAIN_INIT_FAILURE));
                    }
                });
    }

    private void updateAppLocale(String localeString, Context context, Configuration registrationType) {
        locale = localeString;
        String localeArr[] = locale.split("_");
        RegistrationHelper.getInstance().setLocale(localeArr[0].trim(), localeArr[1].trim());
        RLog.d(TAG, "updateAppLocale : locale: " + locale);
        RLog.d(TAG, "updateAppLocale : Configuration: " + registrationType.getValue());
        mRegistrationSettings.intializeRegistrationSettings(context,
                RegistrationConfiguration.getInstance().getRegistrationClientId(registrationType),
                RegistrationHelper.getInstance().getLocale().toString());
    }


    public static UserRegistrationInitializer getInstance() {
        if (mUserRegistrationInitializer == null) {
            synchronized (UserRegistrationInitializer.class) {
                if (mUserRegistrationInitializer == null) {
                    mUserRegistrationInitializer = new UserRegistrationInitializer();
                }

            }

        }
        return mUserRegistrationInitializer;
    }

    public RegistrationSettings getRegistrationSettings() {
        return mRegistrationSettings;
    }

    void initializeEnvironment(Context context, Locale locale) {
        registerJumpInitializationListener(context);


        RLog.d(TAG, "initializeEnvironment Mixrosite ID : " + RegistrationConfiguration.getInstance().getMicrositeId());

        String mRegistrationType = RegistrationConfiguration.getInstance()
                .getRegistrationEnvironment();
        RLog.d(TAG, " initializeEnvironment Registration Environment : " + mRegistrationType);

        UserRegistrationInitializer.getInstance().setJanrainIntialized(false);
        UserRegistrationInitializer.getInstance().setJumpInitializationInProgress(true);

        UserRegistrationInitializer.getInstance().initializeConfiguredEnvironment(context, RegUtility.getConfiguration(mRegistrationType), locale.toString());
    }

    private void registerJumpInitializationListener(Context context) {
        IntentFilter flowFilter = new IntentFilter(Jump.JR_DOWNLOAD_FLOW_SUCCESS);
        flowFilter.addAction(Jump.JR_FAILED_TO_DOWNLOAD_FLOW);
        flowFilter.addAction("com.janrain.android.Jump.PROVIDER_FLOW_SUCCESS");
        if (UserRegistrationInitializer.getInstance().janrainStatusReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(UserRegistrationInitializer.getInstance().janrainStatusReceiver);
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(UserRegistrationInitializer.getInstance().janrainStatusReceiver,
                flowFilter);
    }

    public boolean isJumpInitializated() {
        final boolean janrainInitilized = !isJumpInitializationInProgress() && isJanrainIntialized();
        RLog.d(TAG, " isJumpInitializated : " + janrainInitilized);
        return janrainInitilized;
    }

    public boolean isRegInitializationInProgress() {
        final boolean isRegInitializationInProgress = isJumpInitializationInProgress() && !isJanrainIntialized();
        RLog.d(TAG, " isRegInitializationInProgress : " + isRegInitializationInProgress);
        return isRegInitializationInProgress;

    }

    public boolean isRefreshUserSessionInProgress() {
        RLog.d(TAG, " isRefreshUserSessionInProgress : " + isRefreshUserSessionInProgress);
        return isRefreshUserSessionInProgress;
    }

    public void setRefreshUserSessionInProgress(boolean refreshUserSessionInProgress) {
        isRefreshUserSessionInProgress = refreshUserSessionInProgress;
    }
}
