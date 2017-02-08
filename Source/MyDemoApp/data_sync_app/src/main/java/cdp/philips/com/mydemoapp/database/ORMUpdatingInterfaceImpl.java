package cdp.philips.com.mydemoapp.database;

import com.philips.platform.core.datatypes.Characteristics;
import com.philips.platform.core.datatypes.ConsentDetail;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.Settings;
import com.philips.platform.core.dbinterfaces.DBUpdatingInterface;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.utils.DSLog;

import java.sql.SQLException;
import java.util.List;

import cdp.philips.com.mydemoapp.database.table.OrmCharacteristics;
import cdp.philips.com.mydemoapp.database.table.OrmConsentDetail;
import cdp.philips.com.mydemoapp.database.table.OrmMoment;
import cdp.philips.com.mydemoapp.database.table.OrmSettings;
import cdp.philips.com.mydemoapp.utility.NotifyDBRequestListener;

public class ORMUpdatingInterfaceImpl implements DBUpdatingInterface {
    private static final String TAG = ORMUpdatingInterfaceImpl.class.getSimpleName();
    private final OrmSaving saving;
    private final OrmUpdating updating;
    final private OrmFetchingInterfaceImpl fetching;
    final private OrmDeleting deleting;
    private NotifyDBRequestListener notifyDBRequestListener;

    public ORMUpdatingInterfaceImpl(OrmSaving saving,
                                    OrmUpdating updating,
                                    final OrmFetchingInterfaceImpl fetching,
                                    final OrmDeleting deleting) {
        this.saving = saving;
        this.updating = updating;
        this.fetching = fetching;
        this.deleting = deleting;
        notifyDBRequestListener = new NotifyDBRequestListener();
    }

    @Override
    public void updateFailed(Exception e, DBRequestListener dbRequestListener) {
        notifyDBRequestListener.notifyFailure(e, dbRequestListener);
    }

    @Override
    public void updateSettings(Settings settings, DBRequestListener dbRequestListener) {
        try {
            OrmSettings ormSettings = OrmTypeChecking.checkOrmType(settings, OrmSettings.class);

            Settings existingSettings = fetching.fetchSettings();
            OrmSettings ormExistingSettings = OrmTypeChecking.checkOrmType(existingSettings, OrmSettings.class);

            if(ormExistingSettings==null){
                saving.saveSettings(ormSettings);
                notifyDBRequestListener.notifySuccess(dbRequestListener);
                return;
            }

            ormSettings.setID(ormExistingSettings.getId());
            updating.updateSettings(ormSettings);
            notifyDBRequestListener.notifySuccess(dbRequestListener);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (OrmTypeChecking.OrmTypeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean updateSyncBit(int tableID,boolean isSynced) {
        try {
            updating.updateDCSync(tableID,isSynced);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    @Override
    public boolean updateConsent(final List<? extends ConsentDetail> consents, DBRequestListener dbRequestListener) throws SQLException {

        for(ConsentDetail consentDetail :consents){
            try {
                updating.updateConsentDetails(consentDetail);
            } catch (SQLException e) {
                e.printStackTrace();
                notifyDBRequestListener.notifyFailure(e,dbRequestListener);
                return false;
            }

        }
        notifyDBRequestListener.notifySuccess(consents,dbRequestListener);
        return true;
    }

    @Override
    public void updateMoment(final Moment moment, DBRequestListener dbRequestListener) throws SQLException {

        OrmMoment ormMoment = getOrmMoment(moment, dbRequestListener);
        if (ormMoment == null) {
            return;
        }
        updating.updateMoment(ormMoment);
        updating.refreshMoment(ormMoment);

        notifyDBRequestListener.notifySuccess(dbRequestListener, ormMoment);
    }

    @Override
    public void updateMoments(List<Moment> ormMoments, DBRequestListener dbRequestListener) throws SQLException {
        for(Moment moment : ormMoments){
            moment.setSynced(false);
            updateMoment(moment,dbRequestListener);
        }
    }

    public OrmMoment getOrmMoment(final Moment moment, DBRequestListener dbRequestListener) {
        try {
            return OrmTypeChecking.checkOrmType(moment, OrmMoment.class);
        } catch (OrmTypeChecking.OrmTypeException e) {
            notifyDBRequestListener.notifyOrmTypeCheckingFailure(dbRequestListener, e, "Orm Type check failed");
            DSLog.e(TAG, "Eror while type checking");
        }
        return null;
    }

    //User AppUserCharacteristics
    @Override
    public boolean updateCharacteristics(List<Characteristics> characteristicsList, DBRequestListener dbRequestListener) throws SQLException {

        try {

            deleting.deleteCharacteristics();

            for(Characteristics characteristics:characteristicsList) {
                OrmCharacteristics ormCharacteristics = OrmTypeChecking.checkOrmType(characteristics, OrmCharacteristics.class);
                saving.saveCharacteristics(ormCharacteristics);
            }
            notifyDBRequestListener.notifySuccess(null);
            return true;
        } catch (OrmTypeChecking.OrmTypeException e) {
            e.printStackTrace();
            return false;
        }

    }
}
