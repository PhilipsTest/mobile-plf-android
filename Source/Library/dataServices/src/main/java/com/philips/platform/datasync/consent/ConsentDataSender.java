package com.philips.platform.datasync.consent;

import android.support.annotation.NonNull;

import com.philips.platform.core.Eventing;
import com.philips.platform.core.datatypes.ConsentDetail;
import com.philips.platform.core.datatypes.SyncType;
import com.philips.platform.core.events.BackendResponse;
import com.philips.platform.core.events.SyncBitUpdateRequest;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.DSLog;
import com.philips.platform.datasync.UCoreAccessProvider;
import com.philips.platform.datasync.UCoreAdapter;
import com.philips.platform.datasync.synchronisation.DataSender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ConsentDataSender extends DataSender {

    @Inject
    Eventing eventing;

    @NonNull
    final AtomicInteger synchronizationState = new AtomicInteger(0);

    @NonNull
    private final UCoreAdapter uCoreAdapter;

    @Inject
    UCoreAccessProvider uCoreAccessProvider;


    @NonNull
    private final GsonConverter gsonConverter;

    @NonNull
    private final ConsentsConverter consentsConverter;


    @Inject
    public ConsentDataSender(@NonNull UCoreAdapter uCoreAdapter,
                             @NonNull GsonConverter gsonConverter,
                             @NonNull ConsentsConverter consentsConverter) {
        this.uCoreAdapter = uCoreAdapter;
        this.gsonConverter = gsonConverter;
        this.consentsConverter = consentsConverter;
        DataServicesManager.getInstance().getAppComponant().injectConsentsSender(this);
    }

    @Override
    public boolean sendDataToBackend(@NonNull final List dataToSend) {
          if (dataToSend!=null && !dataToSend.isEmpty()) {
              sendToBackend(new ArrayList<>(dataToSend));
        }

        return false;
    }


    @Override
    public Class<ConsentDetail> getClassForSyncData() {
        return ConsentDetail.class;
    }


    private void sendToBackend(List<ConsentDetail> consentDetails) {

        if(consentDetails==null) return;

        if (isUserInvalid()) {
            postError(1, getNonLoggedInError());
            return;
        }
        if (uCoreAccessProvider == null) {
            return;
        }
        ConsentsClient client = uCoreAdapter.getAppFrameworkClient(ConsentsClient.class, uCoreAccessProvider.getAccessToken(), gsonConverter);
        try {
            List<UCoreConsentDetail> consentDetailList = consentsConverter.convertToUCoreConsentDetails(consentDetails);

            if (consentDetailList.isEmpty()) {
                return;
            }
            client.saveConsent(uCoreAccessProvider.getUserId(), consentDetailList);
            eventing.post(new SyncBitUpdateRequest(SyncType.CONSENT, true));

        } catch (RetrofitError error) {
            onError(error);
            postError(1, error);
        }
    }

    public boolean isUserInvalid() {
        if (uCoreAccessProvider != null) {
            String accessToken = uCoreAccessProvider.getAccessToken();
            return !uCoreAccessProvider.isLoggedIn() || accessToken == null || accessToken.isEmpty();
        }
        return false;
    }

    private void postError(int referenceId, final RetrofitError error) {
        DSLog.i(DSLog.LOG, "Error In ConsentsMonitor - posterror");
        eventing.post(new BackendResponse(referenceId, error));
    }

    private RetrofitError getNonLoggedInError() {
        return RetrofitError.unexpectedError("", new IllegalStateException("you're not logged in"));
    }

}