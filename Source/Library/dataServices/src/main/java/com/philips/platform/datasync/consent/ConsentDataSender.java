package com.philips.platform.datasync.consent;

import android.support.annotation.NonNull;

import com.philips.platform.core.Eventing;
import com.philips.platform.core.datatypes.ConsentDetail;
import com.philips.platform.core.events.ConsentBackendListSaveResponse;
import com.philips.platform.core.events.ConsentBackendSaveRequest;
import com.philips.platform.core.monitors.EventMonitor;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.datasync.synchronisation.DataSender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ConsentDataSender extends EventMonitor implements DataSender<ConsentDetail> {

    @Inject
    Eventing eventing;

    @NonNull
    final AtomicInteger synchronizationState = new AtomicInteger(0);

    @Inject
    public ConsentDataSender() {
        DataServicesManager.getInstance().getAppComponant().injectConsentsSender(this);
    }

    @Override
    public boolean sendDataToBackend(@NonNull final List<? extends ConsentDetail> dataToSend) {
          if (!dataToSend.isEmpty() && synchronizationState.get() != State.BUSY.getCode()) {
            eventing.post(new ConsentBackendSaveRequest(new ArrayList<>(dataToSend),ConsentBackendSaveRequest.RequestType.SAVE));
        }

        return false;
    }

    public void onEventAsync(@SuppressWarnings("UnusedParameters") ConsentBackendListSaveResponse responseEvent) {
        synchronizationState.set(State.IDLE.getCode());
    }

    @Override
    public Class<ConsentDetail> getClassForSyncData() {
        return ConsentDetail.class;
    }
}