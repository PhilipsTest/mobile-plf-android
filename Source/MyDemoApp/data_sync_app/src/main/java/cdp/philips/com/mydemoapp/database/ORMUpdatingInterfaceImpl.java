package cdp.philips.com.mydemoapp.database;

import com.philips.platform.core.datatypes.Consent;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.dbinterfaces.DBUpdatingInterface;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.DSLog;

import java.sql.SQLException;
import java.util.List;

import cdp.philips.com.mydemoapp.database.datatypes.MeasurementDetailType;
import cdp.philips.com.mydemoapp.database.datatypes.MeasurementGroupDetailType;
import cdp.philips.com.mydemoapp.database.datatypes.MeasurementType;
import cdp.philips.com.mydemoapp.database.datatypes.MomentDetailType;
import cdp.philips.com.mydemoapp.database.datatypes.MomentType;
import cdp.philips.com.mydemoapp.database.table.OrmConsent;
import cdp.philips.com.mydemoapp.database.table.OrmConsentDetail;
import cdp.philips.com.mydemoapp.database.table.OrmMoment;
import cdp.philips.com.mydemoapp.temperature.TemperatureMomentHelper;

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
    public void updateFailed(Exception e,DBRequestListener dbRequestListener) {
        dbRequestListener.onFailure(e);
    }

    @Override
    public boolean updateConsent(Consent consent,DBRequestListener dbRequestListener) {
        if(consent==null){
            dbRequestListener.onFailure(new OrmTypeChecking.OrmTypeException("No consent Found on DataCore ."));;
            return false;
        }
        OrmConsent ormConsent = null;
        try {
            ormConsent = OrmTypeChecking.checkOrmType(consent, OrmConsent.class);
            ormConsent=getModifiedConsent(ormConsent);
            saving.saveConsent(ormConsent);
            dbRequestListener.onSuccess(ormConsent);

            return true;
        } catch (OrmTypeChecking.OrmTypeException e) {
            DSLog.e(TAG, "Exception occurred during updateDatabaseWithMoments" + e);
            dbRequestListener.onFailure(e);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            DSLog.e(TAG, "Exception occurred during updateDatabaseWithMoments" + e);
            dbRequestListener.onFailure(e);
            return false;
        }

    }

    private OrmConsent getModifiedConsent(OrmConsent ormConsent) throws SQLException {
        DSLog.d("Creator ID MODI",ormConsent.getCreatorId());
        OrmConsent consentInDatabase = fetching.fetchConsentByCreatorId(ormConsent.getCreatorId());

        if (consentInDatabase != null) {
            int id = consentInDatabase.getId();
            final List<OrmConsentDetail> ormNonSynConsentDetails = fetching.fetchNonSynchronizedConsentDetails();

            for(OrmConsentDetail ormFromBackEndConsentDetail:ormConsent.getConsentDetails()){

                for(OrmConsentDetail ormNonSynConsentDetail:ormNonSynConsentDetails){
                    if(ormFromBackEndConsentDetail.getType() == ormNonSynConsentDetail.getType()){
                        ormFromBackEndConsentDetail.setBackEndSynchronized(ormNonSynConsentDetail.getBackEndSynchronized());
                        ormFromBackEndConsentDetail.setStatus(ormNonSynConsentDetail.getStatus());
                    }
                }
            }
            ormConsent.setId(id);
            for(OrmConsent ormConsentInDB:fetching.fetchAllConsent()) {
                deleting.deleteConsent(ormConsentInDB);
            }
            deleting.deleteConsent(consentInDatabase);
            // updating.updateConsent(consentInDatabase);

        }else{
            saving.saveConsent(ormConsent);
        }
        return ormConsent;
    }

    @Override
    public void updateMoment(final Moment moment, DBRequestListener dbRequestListener) {

        OrmMoment ormMoment = getOrmMoment(moment,dbRequestListener);
        if (ormMoment == null) {
            return;
        }

        try {
            saving.saveMoment(ormMoment);
            updating.updateMoment(ormMoment);
            if(dbRequestListener == null){
                DataServicesManager.getInstance().getDbChangeListener().onSuccess(ormMoment);
            }else{
                dbRequestListener.onSuccess(ormMoment);
            }
        } catch (SQLException e) {
            dbRequestListener.onFailure(e);
        }
    }

    public OrmMoment getOrmMoment(final Moment moment, DBRequestListener dbRequestListener) {
        try {
            return OrmTypeChecking.checkOrmType(moment, OrmMoment.class);
        } catch (OrmTypeChecking.OrmTypeException e) {
            dbRequestListener.onFailure(e);
            DSLog.e(TAG, "Eror while type checking");
        }
        return null;
    }
}
