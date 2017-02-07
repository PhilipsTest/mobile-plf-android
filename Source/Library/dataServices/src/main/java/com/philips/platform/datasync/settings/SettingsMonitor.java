package com.philips.platform.datasync.settings;

import android.support.annotation.NonNull;

import com.philips.platform.core.events.SettingsBackendGetRequest;
import com.philips.platform.core.events.SettingsBackendSaveRequest;
import com.philips.platform.core.monitors.EventMonitor;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.datasync.UCoreAccessProvider;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class SettingsMonitor extends EventMonitor {

    @Inject
    UCoreAccessProvider uCoreAccessProvider;

    @NonNull
    private final SettingsDataSender settingsDataSender;

    @NonNull
    private final SettingsDataFetcher settingsDataFetcher;


    @Inject
    public SettingsMonitor(@NonNull SettingsDataSender settingsDataSender, @NonNull SettingsDataFetcher settingsDataFetcher) {
        this.settingsDataSender = settingsDataSender;
        this.settingsDataFetcher = settingsDataFetcher;
        DataServicesManager.getInstance().getAppComponant().injectUserSettingsMonitor(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(SettingsBackendSaveRequest event) {
        settingsDataSender.sendSettingsToBackend(event.getSettings());
        System.out.println("In onEventAsync(SettingsBackendSaveRequest event) ");
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(SettingsBackendGetRequest event) {
        settingsDataFetcher.getSettings();
    }



}