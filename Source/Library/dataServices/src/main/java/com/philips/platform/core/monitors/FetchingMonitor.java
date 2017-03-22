/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.core.monitors;

import android.support.annotation.NonNull;

import com.philips.platform.core.datatypes.Characteristics;
import com.philips.platform.core.datatypes.ConsentDetail;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.dbinterfaces.DBFetchingInterface;
import com.philips.platform.core.events.GetNonSynchronizedDataRequest;
import com.philips.platform.core.events.GetNonSynchronizedDataResponse;
import com.philips.platform.core.events.GetNonSynchronizedMomentsRequest;
import com.philips.platform.core.events.GetNonSynchronizedMomentsResponse;
import com.philips.platform.core.events.LoadConsentsRequest;
import com.philips.platform.core.events.FetchInsightsFromDB;
import com.philips.platform.core.events.LoadLastMomentRequest;
import com.philips.platform.core.events.LoadMomentsRequest;
import com.philips.platform.core.events.LoadSettingsRequest;
import com.philips.platform.core.events.LoadUserCharacteristicsRequest;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.DSLog;
import com.philips.platform.datasync.consent.ConsentsSegregator;
import com.philips.platform.datasync.insights.InsightSegregator;
import com.philips.platform.datasync.moments.MomentsSegregator;
import com.philips.platform.datasync.settings.SettingsSegregator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class FetchingMonitor extends EventMonitor {

    @NonNull
    private DBFetchingInterface dbInterface;

    @Inject
    MomentsSegregator momentsSegregator;

    @Inject
    ConsentsSegregator consentsSegregator;

    @Inject
    SettingsSegregator settingsSegregator;

    @Inject
    InsightSegregator insightSegregator;

    public FetchingMonitor(DBFetchingInterface dbInterface) {
        this.dbInterface = dbInterface;
        DataServicesManager.getInstance().getAppComponant().injectFetchingMonitor(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(LoadLastMomentRequest event) {
        try {
            dbInterface.fetchLastMoment(event.getType(), event.getDbFetchRequestListner());
        } catch (SQLException e) {
            dbInterface.postError(e, event.getDbFetchRequestListner());
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(GetNonSynchronizedDataRequest event) {
        DSLog.i(DSLog.LOG, "In Fetching Monitor GetNonSynchronizedDataRequest");

        Map<Class, List<?>> dataToSync = new HashMap<>();

        DSLog.i(DSLog.LOG, "In Fetching Monitor before putMomentsForSync");
        dataToSync = momentsSegregator.putMomentsForSync(dataToSync);

        DSLog.i(DSLog.LOG, "In Fetching Monitor before sending GetNonSynchronizedDataResponse");
        dataToSync = consentsSegregator.putConsentForSync(dataToSync);

        DSLog.i(DSLog.LOG, "In Fetching Monitor before sending GetNonSynchronizedDataResponse for UC");

        try {
            dataToSync = dbInterface.putUserCharacteristicsForSync(dataToSync);
        } catch (SQLException e) {
            e.printStackTrace();
            dataToSync.put(Characteristics.class, null);
        }

        DSLog.i(DSLog.LOG, "In Fetching Monitor before sending GetNonSynchronizedDataResponse for UC");
        dataToSync = settingsSegregator.putSettingsForSync(dataToSync);

        dataToSync = insightSegregator.putInsightForSync(dataToSync);

        eventing.post(new GetNonSynchronizedDataResponse(event.getEventId(), dataToSync));

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(LoadMomentsRequest event) {
        try {
            if (event.hasType()) {
                DSLog.i(DSLog.LOG, "pabitra LoadMomentsRequest monitor fetchMomentWithType");
                dbInterface.fetchMoments(event.getDbFetchRequestListener(), event.getTypes());
            } else if (event.hasID()) {
                dbInterface.fetchMomentById(event.getMomentID(), event.getDbFetchRequestListener());
            } else {
                dbInterface.fetchMoments(event.getDbFetchRequestListener());
            }
        } catch (SQLException e) {
            dbInterface.postError(e, event.getDbFetchRequestListener());
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(LoadConsentsRequest event) {
        try {
            dbInterface.fetchConsentDetails(event.getDbFetchRequestListner());
        } catch (SQLException e) {
            dbInterface.postError(e, event.getDbFetchRequestListner());
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    public void onEventAsync(GetNonSynchronizedMomentsRequest event) {
        DSLog.i(DSLog.LOG, "in Fetching Monitor GetNonSynchronizedMomentsRequest");

        List<Moment> ormMomentList = null;
        List<ConsentDetail> consentDetails = null;
        try {
            ormMomentList = (List<Moment>) dbInterface.fetchNonSynchronizedMoments();
        } catch (SQLException e) {
            //dbInterface.postError(e, event.getDbFetchRequestListener());
        }

        try {
            consentDetails = (List<ConsentDetail>) dbInterface.fetchConsentDetails();
        } catch (SQLException e) {
            //dbInterface.postError(e, event.getDbFetchRequestListener());
        }

        eventing.post(new GetNonSynchronizedMomentsResponse(ormMomentList, consentDetails));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(LoadUserCharacteristicsRequest loadUserCharacteristicsRequest) {
        try {
            dbInterface.fetchCharacteristics(loadUserCharacteristicsRequest.getDbFetchRequestListner());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(LoadSettingsRequest loadSettingsRequest) {

        try {
            dbInterface.fetchSettings(loadSettingsRequest.getDbFetchRequestListner());
        } catch (SQLException e) {
            dbInterface.postError(e, loadSettingsRequest.getDbFetchRequestListner());
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventAsync(FetchInsightsFromDB fetchInsightsFromDB) {
        try {
            dbInterface.fetchActiveInsights(fetchInsightsFromDB.getDbFetchRequestListner());
        } catch (SQLException e) {
            dbInterface.postError(e, fetchInsightsFromDB.getDbFetchRequestListner());
        }
    }

}
