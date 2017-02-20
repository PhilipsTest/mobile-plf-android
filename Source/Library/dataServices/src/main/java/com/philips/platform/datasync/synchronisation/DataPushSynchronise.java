/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.platform.datasync.synchronisation;

import android.support.annotation.NonNull;

import com.philips.platform.core.Eventing;
import com.philips.platform.core.events.BackendResponse;
import com.philips.platform.core.events.GetNonSynchronizedDataRequest;
import com.philips.platform.core.events.GetNonSynchronizedDataResponse;
import com.philips.platform.core.monitors.EventMonitor;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.DSLog;
import com.philips.platform.datasync.UCoreAccessProvider;
import com.philips.platform.datasync.characteristics.UserCharacteristicsSender;
import com.philips.platform.datasync.consent.ConsentDataSender;
import com.philips.platform.datasync.moments.MomentsDataSender;
import com.philips.platform.datasync.settings.SettingsDataSender;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RetrofitError;


@Singleton
@SuppressWarnings({"rawtypes", "unchecked"})
public class DataPushSynchronise extends EventMonitor {

    @Inject
    UCoreAccessProvider accessProvider;

    @NonNull
    private final List<? extends DataSender> senders;

    @NonNull
    private Executor executor;

    @Inject
    Eventing eventing;

    @Inject
    SynchronisationManager synchronisationManager;

    @NonNull
    private final AtomicInteger numberOfRunningSenders = new AtomicInteger(0);

    @Inject
    MomentsDataSender momentsDataSender;

    @Inject
    ConsentDataSender consentsDataSender;

    @Inject
    SettingsDataSender settingsDataSender;

    @Inject
    UserCharacteristicsSender userCharacteristicsSender;


    DataServicesManager mDataServicesManager;

    public DataPushSynchronise(@NonNull final List<? extends DataSender> senders) {
        mDataServicesManager = DataServicesManager.getInstance();
        mDataServicesManager.getAppComponant().injectDataPushSynchronize(this);
        this.senders = senders;
    }

    public void startSynchronise(final int eventId) {
        DSLog.i("***SPO***", "In startSynchronise - DataPushSynchronize");

        if (isSyncStarted()) {
            return;
        }

        boolean isLoggedIn = accessProvider.isLoggedIn();

        if (isLoggedIn) {
            DSLog.i("***SPO***", "DataPushSynchronize isLogged-in is true");
            registerEvent();
            fetchNonSynchronizedData(eventId);
        } else {
            DSLog.i("***SPO***", "DataPushSynchronize isLogged-in is false");
            eventing.post(new BackendResponse(eventId, RetrofitError.unexpectedError("", new IllegalStateException("You're not logged in"))));
        }
    }

    public void registerEvent() {
        if (!eventing.isRegistered(this)) {
            eventing.register(this);
        }
    }

    public void unRegisterEvent() {
        if (eventing.isRegistered(this)) {
            eventing.unregister(this);
        }
    }

    private void fetchNonSynchronizedData(int eventId) {
        DSLog.i("***SPO***", "DataPushSynchronize fetchNonSynchronizedData before calling GetNonSynchronizedDataRequest");
        eventing.post(new GetNonSynchronizedDataRequest(eventId));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(GetNonSynchronizedDataResponse response) {
        DSLog.i("***SPO***", "DataPushSynchronize GetNonSynchronizedDataResponse");
        synchronized (this) {

                startAllSenders(response);

        }
    }

    private void startAllSenders(final GetNonSynchronizedDataResponse nonSynchronizedData) {
        DSLog.i("***SPO***", "DataPushSynchronize startAllSenders");

        List<? extends DataSender> configurableSenders = getSenders();

        if(configurableSenders.size()<=0){
            DSLog.i("**SPO**", "In Data Push synchronize Zero Senders configured");
            synchronisationManager.dataSyncComplete();
            return;
        }

        initPush(configurableSenders.size());
        executor = Executors.newFixedThreadPool(20);
        for (final DataSender sender : configurableSenders) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    sender.sendDataToBackend(nonSynchronizedData.getDataToSync(sender.getClassForSyncData()));
                    int jobsRunning = numberOfRunningSenders.decrementAndGet();
                    DSLog.i("**SPO**","In Data Push synchronize preformFetch and jobsRunning = " + jobsRunning);

                    if (jobsRunning <= 0) {
                        DSLog.i("**SPO**","In Data Push synchronize preformPush and jobsRunning = " + jobsRunning + "calling report result");
                        postPushComplete();
                    }
                }
            });
        }
    }

    private void postPushComplete() {
        DSLog.i("***SPO***","DataPushSynchronize set Push complete");
        synchronisationManager.dataSyncComplete();
        synchronisationManager.shutdownAndAwaitTermination((ExecutorService)executor);
    }

    private boolean isSyncStarted() {
        return numberOfRunningSenders.get() > 0;
    }

    private void initPush(int size) {
        DSLog.i("**SPO**","In Data Push synchronize initPush");
        numberOfRunningSenders.set(size);
    }

    private List<? extends DataSender> getSenders(){
        Set<String> configurableSenders = mDataServicesManager.getSyncTypes();

        if(configurableSenders == null){
            return senders;
        }

        ArrayList<DataSender> dataSenders = new ArrayList<>();

        for (String sender : configurableSenders){
            switch (sender){
                case "moment":
                    dataSenders.add(momentsDataSender);
                    break;
                case "Settings":
                    dataSenders.add(settingsDataSender);
                    break;
                case "characteristics":
                    dataSenders.add(userCharacteristicsSender);
                    break;
                case "consent":
                    dataSenders.add(consentsDataSender);
                    break;
            }
        }
        return dataSenders;
    }
}