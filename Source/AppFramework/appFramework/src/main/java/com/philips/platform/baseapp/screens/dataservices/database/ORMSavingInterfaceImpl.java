/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.platform.baseapp.screens.dataservices.database;

import com.philips.platform.baseapp.screens.dataservices.consents.ConsentHelper;
import com.philips.platform.baseapp.screens.dataservices.database.table.BaseAppDateTime;
import com.philips.platform.baseapp.screens.dataservices.database.table.OrmConsent;
import com.philips.platform.baseapp.screens.dataservices.database.table.OrmMoment;
import com.philips.platform.baseapp.screens.dataservices.temperature.TemperatureMomentHelper;
import com.philips.platform.core.datatypes.Consent;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.dbinterfaces.DBSavingInterface;
import com.philips.platform.core.utils.DSLog;

import java.sql.SQLException;

public class ORMSavingInterfaceImpl implements DBSavingInterface {

    private static final String TAG = ORMSavingInterfaceImpl.class.getSimpleName();
    private final OrmSaving saving;
    private final OrmUpdating updating;
    private OrmFetchingInterfaceImpl fetching;
    private OrmDeleting deleting;
    private TemperatureMomentHelper mTemperatureMomentHelper;

    public ORMSavingInterfaceImpl(OrmSaving saving, OrmUpdating updating, final OrmFetchingInterfaceImpl fetching, final OrmDeleting deleting, final BaseAppDateTime baseAppDateTime) {
        this.saving = saving;
        this.updating = updating;
        this.fetching = fetching;
        this.deleting = deleting;
        mTemperatureMomentHelper = new TemperatureMomentHelper();
    }

    @Override
    public boolean saveMoment(final Moment moment) throws SQLException {
        OrmMoment ormMoment = null;
        try {
            ormMoment = OrmTypeChecking.checkOrmType(moment, OrmMoment.class);
            saving.saveMoment(ormMoment);
            updating.updateMoment(ormMoment);

            mTemperatureMomentHelper.notifyAllSuccess(ormMoment);

            return true;
        } catch (OrmTypeChecking.OrmTypeException e) {
            DSLog.e(TAG, "Exception occurred during updateDatabaseWithMoments" + e);
            mTemperatureMomentHelper.notifyAllFailure(e);
            return false;
        }

    }
    @Override
    public boolean saveConsent(Consent consent) throws SQLException {
        OrmConsent ormConsent = null;
        try {
            ormConsent = OrmTypeChecking.checkOrmType(consent, OrmConsent.class);
            updateConsentAndSetIdIfConsentExists(ormConsent);
            new ConsentHelper().notifyAllSuccess(ormConsent);
            return true;
        } catch (OrmTypeChecking.OrmTypeException e) {
            DSLog.e(TAG, "Exception occurred during updateDatabaseWithMoments" + e);
            new ConsentHelper().notifyFailConsent(e);
            return false;
        }

    }

    @Override
    public void postError(Exception e) {
        mTemperatureMomentHelper.notifyAllFailure(e);
    }

    private void updateConsentAndSetIdIfConsentExists(OrmConsent ormConsent) throws SQLException {
        OrmConsent consentInDatabase = fetching.fetchConsentByCreatorId(ormConsent.getCreatorId());
        DSLog.d("Creator ID MODI",ormConsent.getCreatorId());
        if (consentInDatabase != null) {
            int id = consentInDatabase.getId();
            for(OrmConsent ormConsentInDB:fetching.fetchAllConsent()) {
                deleting.deleteConsent(ormConsentInDB);
            }
            ormConsent.setId(id);
            saving.saveConsent(ormConsent);
        }else{
            saving.saveConsent(ormConsent);
        }
    }


}
