package com.philips.platform.datasync.insights;

import android.support.annotation.NonNull;

import com.philips.platform.core.events.FetchInsightRequest;
import com.philips.platform.core.events.DeleteInsightRequest;
import com.philips.platform.core.monitors.EventMonitor;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.datasync.UCoreAccessProvider;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;

import javax.inject.Inject;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class InsightMonitor extends EventMonitor {

    @Inject
    UCoreAccessProvider uCoreAccessProvider;

    @NonNull
    private final InsightDataSender insightDataSender;

    @NonNull
    private final InsightDataFetcher insightDataFetcher;


    @Inject
    public InsightMonitor(@NonNull InsightDataSender insightDataSender, @NonNull InsightDataFetcher insightDataFetcher) {
        this.insightDataSender  =  insightDataSender;
        this.insightDataFetcher = insightDataFetcher;
        DataServicesManager.getInstance().getAppComponant().injectInsightMonitor(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(DeleteInsightRequest event) {
        insightDataSender.sendDataToBackend(event.getInsights());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(FetchInsightRequest event) {
       insightDataFetcher.fetchDataSince(new DateTime());
    }
}