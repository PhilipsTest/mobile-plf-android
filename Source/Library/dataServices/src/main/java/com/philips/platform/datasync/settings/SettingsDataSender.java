package com.philips.platform.datasync.settings;

import android.support.annotation.NonNull;

import com.philips.platform.core.Eventing;
import com.philips.platform.core.datatypes.Settings;
import com.philips.platform.core.events.SettingsBackendSaveRequest;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.datasync.synchronisation.DataSender;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class SettingsDataSender extends DataSender {

    @Inject
    Eventing eventing;

    @NonNull
    final AtomicInteger synchronizationState = new AtomicInteger(0);

    @Inject
    public SettingsDataSender() {
        DataServicesManager.getInstance().getAppComponant().injectSettingsDataSender(this);
    }

    @Override
    public boolean sendDataToBackend(@NonNull List dataToSend) {

        if (dataToSend!=null && !dataToSend.isEmpty() && synchronizationState.get() != State.BUSY.getCode()) {
            eventing.post(new SettingsBackendSaveRequest((Settings) dataToSend.get(0))); //As dataToSend List alsways contains a single setting Object
        }

        return false;
    }

    @Override
    public Class<? extends Settings> getClassForSyncData() {
        return Settings.class;
    }
}