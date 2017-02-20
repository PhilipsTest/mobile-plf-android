package com.philips.platform.datasync.moments;

import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.SynchronisationData;
import com.philips.platform.core.dbinterfaces.DBDeletingInterface;
import com.philips.platform.core.dbinterfaces.DBFetchingInterface;
import com.philips.platform.core.dbinterfaces.DBSavingInterface;
import com.philips.platform.core.dbinterfaces.DBUpdatingInterface;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.DSLog;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MomentsSegregator {

    @Inject
    DBUpdatingInterface updatingInterface;
    @Inject
    DBFetchingInterface dbFetchingInterface;
    @Inject
    DBDeletingInterface dbDeletingInterface;
    @Inject
    DBSavingInterface dbSavingInterface;


    public MomentsSegregator(){
        DataServicesManager.getInstance().getAppComponant().injectMomentsSegregator(this);
    }

    public int processMomentsReceivedFromBackend(final List<? extends Moment> moments,DBRequestListener dbRequestListener) {
        int updatedCount = 0;
        for (final Moment moment : moments) {
                updatedCount = processMoment(updatedCount, moment ,dbRequestListener);
        }
        return updatedCount;
    }

    private Moment getOrmMomentFromDatabase(Moment moment,DBRequestListener dbRequestListener) throws SQLException {
        Moment momentInDatabase = null;
        final SynchronisationData synchronisationData = moment.getSynchronisationData();

        if (synchronisationData != null) {
            momentInDatabase = (Moment) dbFetchingInterface.fetchMomentByGuid(synchronisationData.getGuid());
            if (momentInDatabase == null) {
                momentInDatabase = (Moment) dbFetchingInterface.fetchMomentById(moment.getId(),dbRequestListener);
            }
        }
        return momentInDatabase;
    }

    private int getVersionInDatabase(final Moment momentInDatabase) {
        if (momentInDatabase != null && momentInDatabase.getSynchronisationData() != null) {
            return momentInDatabase.getSynchronisationData().getVersion();
        }
        return -1;
    }

    private boolean hasDifferentMomentVersion(final Moment moment,
                                              final Moment momentInDatabase) throws SQLException {
        boolean isVersionDifferent = true;
        final SynchronisationData synchronisationData = moment.getSynchronisationData();

        if (synchronisationData != null) {
            final int versionInDatabase = getVersionInDatabase(momentInDatabase);
            if (versionInDatabase != -1) {
                isVersionDifferent = versionInDatabase != synchronisationData.getVersion();
            }
        }
        return isVersionDifferent;
    }

    private boolean isActive(final SynchronisationData synchronisationData) {
        return synchronisationData == null || !synchronisationData.isInactive();
    }

    private void deleteMomentInDatabaseIfExists(final Moment momentInDatabase,DBRequestListener dbRequestListener)
            throws SQLException {
        if (momentInDatabase != null) {
            dbDeletingInterface.deleteMoment(momentInDatabase,dbRequestListener);
        }
    }

    protected boolean MomentDeletedLocallyDuringSync(final Moment momentInDatabase) {
        if (momentInDatabase != null) {
            final SynchronisationData synchronisationData = momentInDatabase.getSynchronisationData();
            if (synchronisationData != null) {
                return synchronisationData.getGuid().
                        equals(Moment.MOMENT_NEVER_SYNCED_AND_DELETED_GUID);
            }
        }
        return false;
    }

    public int processMoment(int count, final Moment moment, DBRequestListener dbRequestListener) {
        try {
            final Moment momentInDatabase = getOrmMomentFromDatabase(moment,dbRequestListener);
            if (hasDifferentMomentVersion(moment, momentInDatabase)) {
                if (!isActive(moment.getSynchronisationData())) {
                    deleteMomentInDatabaseIfExists(momentInDatabase,dbRequestListener);
                } else if (MomentDeletedLocallyDuringSync(momentInDatabase)) {
                    moment.setSynced(false);
                    moment.getSynchronisationData().setInactive(true);

                    deleteAndSaveMoment(momentInDatabase, moment,dbRequestListener);
                } else {
                    if (!isMomentModifiedLocallyDuringSync(momentInDatabase, moment)) {
                        moment.setSynced(true);
                    }
                    //This is required for deleting duplicate
                    // measurements, measurementDetails and momentDetails
                    deleteAndSaveMoment(momentInDatabase, moment,dbRequestListener);
                }
                count++;
            } else {

            }
        } catch (SQLException e) {
            updatingInterface.updateFailed(e,dbRequestListener);
        }

        return count;
    }

    private boolean isMomentModifiedLocallyDuringSync(final Moment momentInDatabase,
                                                      final Moment ormMoment) {
        return momentInDatabase != null &&
                !ormMoment.getDateTime().equals(momentInDatabase.getDateTime());
    }

    private void deleteMeasurementAndMomentDetailsAndSetId(final Moment momentInDatabase,Moment ormMoment,DBRequestListener dbRequestListener) throws SQLException {
        if (momentInDatabase != null) {
            dbDeletingInterface.deleteMomentDetail(momentInDatabase,dbRequestListener);
            dbDeletingInterface.deleteMeasurementGroup(momentInDatabase,dbRequestListener);
        }
    }

    private void deleteAndSaveMoment(final Moment momentInDatabase,
                                     final Moment ormMoment,DBRequestListener dbRequestListener) throws SQLException {
        if (momentInDatabase != null) {
            ormMoment.setId(momentInDatabase.getId());
        }
        deleteMeasurementAndMomentDetailsAndSetId(momentInDatabase,ormMoment,dbRequestListener);
        dbSavingInterface.saveMoment(ormMoment,dbRequestListener);
    }

    public void processCreatedMoment(List<? extends Moment> moments,DBRequestListener dbRequestListener) {
        for (final Moment moment : moments) {
                moment.setSynced(true);
            try {
                dbSavingInterface.saveMoment(moment,dbRequestListener);
            } catch (SQLException e) {
                updatingInterface.updateFailed(e,dbRequestListener);
                e.printStackTrace();
            }
        }
    }

    public Map<Class, List<?>> putMomentsForSync(final Map<Class, List<?>> dataToSync) throws SQLException {
        DSLog.i("***SPO***", "In OrmFetchingInterfaceImpl before fetchNonSynchronizedMoments");
        List<? extends Moment> ormMomentList = (List<? extends Moment>) dbFetchingInterface.fetchNonSynchronizedMoments();
        DSLog.i("***SPO***", "In OrmFetchingInterfaceImpl dataToSync.put");
        dataToSync.put(Moment.class, ormMomentList);
        return dataToSync;
    }

}
