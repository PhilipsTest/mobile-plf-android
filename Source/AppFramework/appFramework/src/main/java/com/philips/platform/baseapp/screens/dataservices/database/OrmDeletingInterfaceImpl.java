package com.philips.platform.baseapp.screens.dataservices.database;

import android.support.annotation.NonNull;

import com.philips.platform.baseapp.screens.dataservices.DataServicesState;
import com.philips.platform.baseapp.screens.dataservices.database.table.OrmMoment;
import com.philips.platform.baseapp.screens.dataservices.database.table.OrmSynchronisationData;
import com.philips.platform.baseapp.screens.dataservices.temperature.TemperatureMomentHelper;
import com.philips.platform.baseapp.screens.dataservices.utility.NotifyDBRequestListener;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.dbinterfaces.DBDeletingInterface;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.utils.DSLog;

import org.joda.time.DateTime;

import java.sql.SQLException;

import javax.inject.Inject;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class OrmDeletingInterfaceImpl implements DBDeletingInterface {

    @NonNull
    private final OrmDeleting ormDeleting;

    @NonNull
    private final OrmSaving ormSaving;

    NotifyDBRequestListener notifyDBRequestListener;

    @Inject
    public OrmDeletingInterfaceImpl(@NonNull final OrmDeleting ormDeleting,
                                    final OrmSaving ormSaving) {
        this.ormDeleting = ormDeleting;
        this.ormSaving = ormSaving;
        notifyDBRequestListener = new NotifyDBRequestListener();
    }

    @Override
    public void deleteAll(DBRequestListener dbRequestListener) throws SQLException {
        ormDeleting.deleteAll();
    }

    @Override
    public void markAsInActive(final Moment moment, DBRequestListener dbRequestListener) throws SQLException {
        if (isMomentSyncedToBackend(moment)) {
            prepareMomentForDeletion(moment, dbRequestListener);
        } else {
            moment.setSynchronisationData(
                    new OrmSynchronisationData(Moment.MOMENT_NEVER_SYNCED_AND_DELETED_GUID, true,
                            DateTime.now(), 0));
            saveMoment(moment, dbRequestListener);
        }
    }

    @Override
    public void deleteMoment(Moment moment, DBRequestListener dbRequestListener) throws SQLException {
        ormDeleting.ormDeleteMoment((OrmMoment) moment);
    }

    @Override
    public void deleteMomentDetail(Moment moment,DBRequestListener dbRequestListener) throws SQLException {
        ormDeleting.deleteMomentDetails(moment.getId());
    }

    @Override
    public void deleteMeasurementGroup(Moment moment, DBRequestListener dbRequestListener) throws SQLException {
        ormDeleting.deleteMeasurementGroups((OrmMoment) moment);
    }

    @Override
    public void deleteFailed(Exception e, DBRequestListener dbRequestListener) {
        notifyDBRequestListener.notifyFailure(e,dbRequestListener);
    }

    private boolean isMomentSyncedToBackend(final Moment moment) {
        return moment.getSynchronisationData() != null;
    }

    private void saveMoment(final Moment moment,DBRequestListener dbRequestListener) throws SQLException {
        ormSaving.saveMoment(getOrmMoment(moment,dbRequestListener));
    }

    private OrmMoment getOrmMoment(final Moment moment,DBRequestListener dbRequestListener) {
        try {
            return OrmTypeChecking.checkOrmType(moment, OrmMoment.class);
        } catch (OrmTypeChecking.OrmTypeException e) {
            notifyDBRequestListener.notifyOrmTypeCheckingFailure(dbRequestListener, e,"type check failed!");
            if (e.getMessage() != null) {
                DSLog.i("***SPO***", "Exception = " + e.getMessage());
            }
        }
        return null;
    }

    private void prepareMomentForDeletion(final Moment moment,DBRequestListener dbRequestListener) throws SQLException {
        moment.setSynced(false);
        moment.getSynchronisationData().setInactive(true);
        saveMoment(moment, dbRequestListener);
    }
}
