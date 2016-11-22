package com.philips.platform.baseapp.screens.datasevices.database;

import android.util.Log;

import com.philips.platform.core.datatypes.Consent;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.MomentDetail;
import com.philips.platform.core.datatypes.MomentDetailType;
import com.philips.platform.core.datatypes.MomentType;
import com.philips.platform.core.datatypes.SynchronisationData;
import com.philips.platform.core.dbinterfaces.DBUpdatingInterface;
import com.philips.platform.datasevices.database.table.OrmConsent;
import com.philips.platform.datasevices.database.table.OrmMoment;
import com.philips.platform.datasevices.database.table.OrmSynchronisationData;
import com.philips.platform.datasevices.listener.DBChangeListener;
import com.philips.platform.datasevices.listener.EventHelper;
import com.philips.platform.datasevices.listener.UserRegistrationFailureListener;
import com.philips.platform.datasevices.temperature.TemperatureMomentHelper;

import java.io.File;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit.RetrofitError;

public class ORMUpdatingInterfaceImpl implements DBUpdatingInterface {
    private static final String TAG = ORMUpdatingInterfaceImpl.class.getSimpleName();
    private final OrmSaving saving;
    private final OrmUpdating updating;
    final private OrmFetchingInterfaceImpl fetching;
    final private OrmDeleting deleting;
    private TemperatureMomentHelper mTemperatureMomentHelper;

    public ORMUpdatingInterfaceImpl(OrmSaving saving,
                                    OrmUpdating updating,
                                    final OrmFetchingInterfaceImpl fetching,
                                    final OrmDeleting deleting) {
        this.saving = saving;
        this.updating = updating;
        this.fetching = fetching;
        this.deleting = deleting;
        mTemperatureMomentHelper = new TemperatureMomentHelper();
    }

    @Override
    public int processMomentsReceivedFromBackend(final List<? extends Moment> moments) {
        int updatedCount = 0;
        for (final Moment moment : moments) {
            if (moment.getType() != MomentType.PHOTO || photoFileExistsForPhotoMoments(moment)) {
                updatedCount = processMoment(updatedCount, moment);
            }
        }
        notifyAllSuccess(moments);
        return updatedCount;
    }

    @Override
    public void updateFailed(Exception e) {
        notifyAllFailure(e);
    }

    @Override
    public void postRetrofitError(final Throwable error) {
        notifyAllFailure((Exception) error);
    }

    @Override
    public void updateConsent(Consent consent) {
        try {
            OrmConsent ormConsent = OrmTypeChecking.checkOrmType(consent, OrmConsent.class);
            OrmConsent consentInDatabase = fetching.fetchConsentByCreatorId(ormConsent.getCreatorId());
            Log.d("Creator ID MODI",ormConsent.getCreatorId());

            if (consentInDatabase != null) {

                int id = consentInDatabase.getId();
                deleting.deleteConsent(consentInDatabase);
                ormConsent.setId(id);
                saving.saveConsent(ormConsent);

            }else{
                saving.saveConsent(ormConsent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (OrmTypeChecking.OrmTypeException e) {
            e.printStackTrace();
        }

    }


    private boolean photoFileExistsForPhotoMoments(final Moment moment) {
        final Collection<? extends MomentDetail> momentDetails = moment.getMomentDetails();
        if (momentDetails == null) {
            return false;
        }
        for (final MomentDetail momentDetail : momentDetails) {
            if (momentDetail.getType() == MomentDetailType.PHOTO) {
                final File file = new File(momentDetail.getValue());
                if (file.exists()) {
                    return true;
                }
                break; // Breaking as we show ONLY the first photo in timeline.
            }
        }
        return false;
    }

    @Override
    public int processMoment(int count, final Moment moment) {
        try {
            final OrmMoment momentInDatabase = getOrmMomentFromDatabase(moment);
            if (hasDifferentMomentVersion(moment, momentInDatabase)) {
                final OrmMoment ormMoment = getOrmMoment(moment);
                if (!isActive(ormMoment.getSynchronisationData())) {
                    deleteMomentInDatabaseIfExists(momentInDatabase);
                } else if (isNeverSyncedMomentDeletedLocallyDuringSync(momentInDatabase)) {
                    ormMoment.setSynced(false);
                    ormMoment.getSynchronisationData().setInactive(true);

                    deleteAndSaveMoment(momentInDatabase, ormMoment);
                } else {
                    if (!isMomentModifiedLocallyDuringSync(momentInDatabase, ormMoment)) {
                        ormMoment.setSynced(true);
                    }

                    //This is required for deleting duplicate
                    // measurements, measurementDetails and momentDetails
                    deleteAndSaveMoment(momentInDatabase, ormMoment);
                }
                count++;
            } else {
                // tagRoomTemperatureAndHumidity(moment);
            }
        } catch (SQLException e) {
            notifyAllFailure(e);
        }

        return count;
    }

    @Override
    public void updateOrSaveMomentInDatabase(final Moment ormMoment) {
        try {
            saving.saveMoment((OrmMoment) ormMoment);
            updating.updateMoment((OrmMoment) ormMoment);
            notifyAllSuccess(ormMoment);
        } catch (SQLException e) {
            notifyAllFailure(e);
        }
    }

    private void deleteAndSaveMoment(final OrmMoment momentInDatabase,
                                     final OrmMoment ormMoment) throws SQLException {
        deleteMeasurementAndMomentDetailsAndSetId(momentInDatabase, ormMoment);
        updateOrSaveMomentInDatabase(ormMoment);
    }

    private boolean isNeverSyncedMomentDeletedLocallyDuringSync(final OrmMoment momentInDatabase) {
        if (momentInDatabase != null) {
            final OrmSynchronisationData synchronisationData = momentInDatabase.getSynchronisationData();
            if (synchronisationData != null) {
                return synchronisationData.getGuid().
                        equals(Moment.MOMENT_NEVER_SYNCED_AND_DELETED_GUID);
            }
        }
        return false;
    }

    private void deleteMeasurementAndMomentDetailsAndSetId(final OrmMoment momentInDatabase,
                                                           final OrmMoment ormMoment) throws SQLException {
        if (momentInDatabase != null) {
            ormMoment.setId(momentInDatabase.getId());
            deleting.deleteMomentAndMeasurementDetails(momentInDatabase);
        }
    }

    @Override
    public OrmMoment getOrmMoment(final Moment moment) {
        try {
            return OrmTypeChecking.checkOrmType(moment, OrmMoment.class);
        } catch (OrmTypeChecking.OrmTypeException e) {
            mTemperatureMomentHelper.notifyAllFailure(e);
            Log.e(TAG, "Eror while type checking");
        }
        return null;
    }

    private void deleteMomentInDatabaseIfExists(final OrmMoment momentInDatabase)
            throws SQLException {
        if (momentInDatabase != null) {
            deleting.ormDeleteMoment(momentInDatabase);
        }
    }

    private boolean isMomentModifiedLocallyDuringSync(final OrmMoment momentInDatabase,
                                                      final OrmMoment ormMoment) {
        return momentInDatabase != null &&
                !ormMoment.getDateTime().equals(momentInDatabase.getDateTime());
    }


    private boolean isActive(final OrmSynchronisationData synchronisationData) {
        return synchronisationData == null || !synchronisationData.isInactive();
    }

    private boolean hasDifferentMomentVersion(final Moment moment,
                                              final OrmMoment momentInDatabase) throws SQLException {
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

    private int getVersionInDatabase(final OrmMoment momentInDatabase) {
        if (momentInDatabase != null && momentInDatabase.getSynchronisationData() != null) {
            return momentInDatabase.getSynchronisationData().getVersion();
        }
        return -1;
    }

    private OrmMoment getOrmMomentFromDatabase(Moment moment) throws SQLException {
        OrmMoment momentInDatabase = null;
        final SynchronisationData synchronisationData = moment.getSynchronisationData();

        if (synchronisationData != null) {
            momentInDatabase = (OrmMoment) fetching.fetchMomentByGuid(synchronisationData.getGuid());
            if (momentInDatabase == null) {
                momentInDatabase = (OrmMoment) fetching.fetchMomentById(moment.getId());
            }
        }
        return momentInDatabase;
    }

    //TODO: Spoorti - Already part of Temperature Helper
    private void notifyAllSuccess(Object ormMoments) {
        final Map<Integer, ArrayList<DBChangeListener>> eventMap =
                EventHelper.getInstance().getEventMap();
        final Set<Integer> integers = eventMap.keySet();
        if (integers.contains(EventHelper.MOMENT)) {
            final ArrayList<DBChangeListener> dbChangeListeners =
                    EventHelper.getInstance().getEventMap().get(EventHelper.MOMENT);
            for (final DBChangeListener listener : dbChangeListeners) {
                listener.onSuccess(ormMoments);
            }
        }
    }

    private void notifyAllFailure(Exception e) {
        final RetrofitError error = (RetrofitError) e;
        int status = -1000;
        if (error != null && error.getResponse() != null) {
            status = error.getResponse().getStatus();
        }

        final Map<Integer, ArrayList<UserRegistrationFailureListener>> urMap =
                EventHelper.getInstance().getURMap();
        final Map<Integer, ArrayList<DBChangeListener>> eventMap =
                EventHelper.getInstance().getEventMap();
        Set<Integer> integers;

        if (status == HttpURLConnection.HTTP_UNAUTHORIZED ||
                status == HttpURLConnection.HTTP_FORBIDDEN) {
            integers = urMap.keySet();
            if (integers.contains(EventHelper.UR)) {
                final ArrayList<UserRegistrationFailureListener> dbChangeListeners =
                        EventHelper.getInstance().getURMap().get(EventHelper.UR);
                for (final UserRegistrationFailureListener listener : dbChangeListeners) {
                    listener.onFailure((RetrofitError) e);
                }
            }
        } else {
            integers = eventMap.keySet();
            if (integers.contains(EventHelper.MOMENT)) {
                final ArrayList<DBChangeListener> dbChangeListeners =
                        EventHelper.getInstance().getEventMap().get(EventHelper.MOMENT);
                for (final DBChangeListener listener : dbChangeListeners) {
                    listener.onFailure(e);
                }
            }
        }
    }
}
