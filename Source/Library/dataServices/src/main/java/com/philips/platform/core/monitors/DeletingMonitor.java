/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.core.monitors;

import com.philips.platform.core.dbinterfaces.DBDeletingInterface;
import com.philips.platform.core.events.DataClearRequest;
import com.philips.platform.core.events.DeleteAllMomentsRequest;
import com.philips.platform.core.events.DeleteInsightRequest;
import com.philips.platform.core.events.DeleteInsightResponse;
import com.philips.platform.core.events.DeleteInsightFromDB;
import com.philips.platform.core.events.MomentBackendDeleteResponse;
import com.philips.platform.core.events.MomentDeleteRequest;
import com.philips.platform.core.events.MomentsDeleteRequest;
import com.philips.platform.core.listeners.DBRequestListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.SQLException;

import javax.inject.Inject;

public class DeletingMonitor extends EventMonitor {

    private final DBDeletingInterface dbInterface;

    @Inject
    public DeletingMonitor(DBDeletingInterface dbInterface) {
        this.dbInterface = dbInterface;

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackGround(DataClearRequest event) {
        final DBRequestListener dbRequestListener = event.getDbRequestListener();
        try {
            dbInterface.deleteAll(dbRequestListener);
        } catch (SQLException e) {
            dbInterface.deleteFailed(e, dbRequestListener);
        }
        //eventing.post(new DataClearResponse(event.getEventId()));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackGround(DeleteAllMomentsRequest event) {
        final DBRequestListener dbRequestListener = event.getDbRequestListener();
        try {
            dbInterface.deleteAllMoments(dbRequestListener);
        } catch (SQLException e) {
            dbInterface.deleteFailed(e, dbRequestListener);
        }
        //eventing.post(new DataClearResponse(event.getEventId()));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackGround(MomentDeleteRequest event) {
        final DBRequestListener dbRequestListener = event.getDbRequestListener();
        try {
            dbInterface.markAsInActive(event.getMoment(), dbRequestListener);
        } catch (SQLException e) {
            dbInterface.deleteFailed(e, dbRequestListener);
        }
        //   eventing.post(new MomentChangeEvent(event.getEventId(), event.getMoments()));

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackGround(MomentsDeleteRequest event) {
        final DBRequestListener dbRequestListener = event.getDbRequestListener();
        try {
            dbInterface.markMomentsAsInActive(event.getMoments(), dbRequestListener);
        } catch (SQLException e) {
            dbInterface.deleteFailed(e, dbRequestListener);
        }
        //   eventing.post(new MomentChangeEvent(event.getEventId(), event.getMoments()));

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackGround(MomentBackendDeleteResponse backendDeleteResponse) {
        final DBRequestListener dbRequestListener = backendDeleteResponse.getDbRequestListener();
        try {
            dbInterface.deleteMoment(backendDeleteResponse.getMoment(),
                    backendDeleteResponse.getDbRequestListener());
        } catch (SQLException e) {
            dbInterface.deleteFailed(e, dbRequestListener);
        }
    }

    //Insights
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventAsync(DeleteInsightFromDB deleteInsightFromDB) {
        final DBRequestListener dbRequestListener = deleteInsightFromDB.getDbRequestListener();
        try {
            dbInterface.markInsightsAsInActive(deleteInsightFromDB.getInsights(), dbRequestListener);
            eventing.post(new DeleteInsightRequest(deleteInsightFromDB.getInsights())); //is it good to have here?
        } catch (SQLException e) {
            dbInterface.deleteFailed(e, dbRequestListener);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventAsync(DeleteInsightResponse deleteInsightResponse) {
        final DBRequestListener dbRequestListener = deleteInsightResponse.getDBRequestListener();
        try {
            dbInterface.deleteInsight(deleteInsightResponse.getInsight(),
                    dbRequestListener);
        } catch (SQLException e) {
            dbInterface.deleteFailed(e, dbRequestListener);
        }
    }
}

