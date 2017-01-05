/*
 * Copyright (c) 2016. Philips Electronics India Ltd
 * All rights reserved. Reproduction in whole or in part is prohibited without
 * the written consent of the copyright holder.
 */

package cdp.philips.com.mydemoapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.philips.platform.core.utils.DSLog;
import com.philips.platform.core.utils.UuidGenerator;

import java.sql.SQLException;
import java.util.List;

import cdp.philips.com.mydemoapp.database.datatypes.MeasurementDetailType;
import cdp.philips.com.mydemoapp.database.datatypes.MeasurementGroupDetailType;
import cdp.philips.com.mydemoapp.database.datatypes.MeasurementType;
import cdp.philips.com.mydemoapp.database.datatypes.MomentDetailType;
import cdp.philips.com.mydemoapp.database.datatypes.MomentType;
import cdp.philips.com.mydemoapp.database.table.OrmCharacteristics;
import cdp.philips.com.mydemoapp.database.table.OrmCharacteristicsDetail;
import cdp.philips.com.mydemoapp.database.table.OrmConsent;
import cdp.philips.com.mydemoapp.database.table.OrmConsentDetail;
import cdp.philips.com.mydemoapp.database.table.OrmMeasurement;
import cdp.philips.com.mydemoapp.database.table.OrmMeasurementDetail;
import cdp.philips.com.mydemoapp.database.table.OrmMeasurementDetailType;
import cdp.philips.com.mydemoapp.database.table.OrmMeasurementGroup;
import cdp.philips.com.mydemoapp.database.table.OrmMeasurementGroupDetail;
import cdp.philips.com.mydemoapp.database.table.OrmMeasurementGroupDetailType;
import cdp.philips.com.mydemoapp.database.table.OrmMeasurementType;
import cdp.philips.com.mydemoapp.database.table.OrmMoment;
import cdp.philips.com.mydemoapp.database.table.OrmMomentDetail;
import cdp.philips.com.mydemoapp.database.table.OrmMomentDetailType;
import cdp.philips.com.mydemoapp.database.table.OrmMomentType;
import cdp.philips.com.mydemoapp.database.table.OrmSynchronisationData;
import cdp.philips.com.mydemoapp.temperature.TemperatureMomentHelper;

/**
 * Database helper which creates and upgrades the database and provides the DAOs for the app.
 *
 * @author kevingalligan
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "DataService.db";
    private static final int DATABASE_VERSION = 1;
    private final UuidGenerator uuidGenerator;
    private Dao<OrmMoment, Integer> momentDao;
    private Dao<OrmMomentType, Integer> momentTypeDao;
    private Dao<OrmMomentDetail, Integer> momentDetailDao;
    private Dao<OrmMomentDetailType, Integer> momentDetailTypeDao;
    private Dao<OrmMeasurement, Integer> measurementDao;
    private Dao<OrmMeasurementType, Integer> measurementTypeDao;
    private Dao<OrmMeasurementDetail, Integer> measurementDetailDao;
    private Dao<OrmMeasurementDetailType, Integer> measurementDetailTypeDao;
    private Dao<OrmMeasurementGroupDetailType, Integer> measurementGroupDetailTypes;
    private Dao<OrmMeasurementGroup, Integer> measurementGroup;
    private Dao<OrmMeasurementGroupDetail, Integer> measurementGroupDetails;
    private Dao<OrmSynchronisationData, Integer> synchronisationDataDao;
    private TemperatureMomentHelper mTemperatureMomentHelper;
    private Dao<OrmConsent, Integer> consentDao;
    private Dao<OrmConsentDetail, Integer> consentDetailDao;

    private Dao<OrmCharacteristics, Integer> characteristicsesDao;
    private Dao<OrmCharacteristicsDetail, Integer> characteristicsDetailsDao;

    public DatabaseHelper(Context context, final UuidGenerator uuidGenerator) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.uuidGenerator = uuidGenerator;
        mTemperatureMomentHelper = new TemperatureMomentHelper();
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        DSLog.d(TAG, "onCreate DatabaseHelper");
        try {
            createTables(connectionSource);
            insertDictionaries();
        } catch (SQLException e) {
            DSLog.e(TAG, "Error Unable to create databases" + e);
            mTemperatureMomentHelper.notifyAllFailure(e);
        }
    }

    private void insertDictionaries() throws SQLException {
        insertMomentTypes();
        insertMeasurementTypes();
        insertMomentDetailsTypes();
        insertMeasurementDetailTypes();
        insertMeasurementGroupDetailType();
    }

    private void insertMeasurementTypes() throws SQLException {
        final Dao<OrmMeasurementType, Integer> measurementTypeDao = getMeasurementTypeDao();
        List<String> values = MeasurementType.getMeasurementTypes();
        for (final String value : values) {
            measurementTypeDao.createOrUpdate(new OrmMeasurementType(MeasurementType.getIDFromDescription(value),
                    value,
                    MeasurementType.getUnitFromDescription(value)));
        }
    }

    private void insertMomentDetailsTypes() throws SQLException {
        final Dao<OrmMomentDetailType, Integer> momentDetailTypeDao = getMomentDetailTypeDao();
        List<String> values = MomentDetailType.getMomentDetailTypes();
        for (final String value : values) {
            momentDetailTypeDao.createOrUpdate(new OrmMomentDetailType(MomentDetailType.getIDFromDescription(value), value));
        }
    }

    private void insertMeasurementDetailTypes() throws SQLException {
        Dao<OrmMeasurementDetailType, Integer> measurementDetailTypeDao = getMeasurementDetailTypeDao();
        List<String> values = MeasurementDetailType.getMeasurementDetailTypes();
        for (final String value : values) {
            measurementDetailTypeDao.createOrUpdate(new OrmMeasurementDetailType(MeasurementDetailType.getIDFromDescription(value), value));
        }
    }

    private void insertMomentTypes() throws SQLException {
        Dao<OrmMomentType, Integer> momentTypeDao = getMomentTypeDao();
        List<String> values = MomentType.getMomentTypes();
        for (final String value : values) {
            momentTypeDao.createOrUpdate(new OrmMomentType(MomentType.getIDFromDescription(value), value));
        }
    }

    private void insertMeasurementGroupDetailType() throws SQLException {
        Dao<OrmMeasurementGroupDetailType, Integer> measurementGroupDetailTypes = getMeasurementGroupDetailTypeDao();
        // MeasurementGroupDetailType[] values = MeasurementGroupDetailType.values();
        List<String> values = MeasurementGroupDetailType.getMeasurementGroupDetailType();
        for (final String value : values) {
            measurementGroupDetailTypes.createOrUpdate(new OrmMeasurementGroupDetailType(MeasurementGroupDetailType.getIDFromDescription(value), value));
        }
    }


    private void createTables(final ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTable(connectionSource, OrmMoment.class);
        TableUtils.createTable(connectionSource, OrmMomentType.class);
        TableUtils.createTable(connectionSource, OrmMomentDetail.class);
        TableUtils.createTable(connectionSource, OrmMomentDetailType.class);
        TableUtils.createTable(connectionSource, OrmMeasurement.class);
        TableUtils.createTable(connectionSource, OrmMeasurementType.class);
        TableUtils.createTable(connectionSource, OrmMeasurementDetail.class);
        TableUtils.createTable(connectionSource, OrmMeasurementDetailType.class);
        TableUtils.createTable(connectionSource, OrmSynchronisationData.class);
        TableUtils.createTable(connectionSource, OrmConsent.class);
        TableUtils.createTable(connectionSource, OrmConsentDetail.class);
        TableUtils.createTable(connectionSource, OrmMeasurementGroup.class);
        TableUtils.createTable(connectionSource, OrmMeasurementGroupDetail.class);
        TableUtils.createTable(connectionSource, OrmMeasurementGroupDetailType.class);
        TableUtils.createTable(connectionSource, OrmCharacteristics.class);
        TableUtils.createTable(connectionSource, OrmCharacteristicsDetail.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        DSLog.i(TAG + "onUpgrade", "olderVer =" + oldVer + " newerVer =" + newVer);
        if (newVer > oldVer) {
            //Alter your table here...
        }
    }


    private void addMeasurementTypes(MeasurementType... measurementTypes) throws SQLException {
        final Dao<OrmMeasurementType, Integer> measurementTypeDao = getMeasurementTypeDao();
        // for (MeasurementType measurementType : measurementTypes) {
        measurementTypeDao.createOrUpdate(new OrmMeasurementType(41, "TEMPERATURE", "\u2103"));
//        }
    }

    private void addMomentTypes(MomentType... momentTypes) throws SQLException {
        final Dao<OrmMomentType, Integer> ormMomentTypeDao = getMomentTypeDao();
        for (MomentType momentType : momentTypes) {
            ormMomentTypeDao.createOrUpdate(new OrmMomentType(MomentType.getIDFromDescription("TEMPERATURE"),
                    MomentType.getDescriptionFromID(25)));
        }
    }


    private void addNewMomentDetailTypeAndAddedUUIDForTagging() throws SQLException {
        final Dao<OrmMomentDetailType, Integer> momentDetailTypeDao = getMomentDetailTypeDao();
        momentDetailTypeDao.createOrUpdate(new OrmMomentDetailType(MomentDetailType.getIDFromDescription("TAGGING_ID"),
                MomentDetailType.getDescriptionFromID(54)));

        final Dao<OrmMoment, Integer> ormMomentDao = getDao(OrmMoment.class);
        List<OrmMoment> moments = ormMomentDao.queryForAll();
        for (OrmMoment moment : moments) {
            final OrmMomentDetailType detailType = new OrmMomentDetailType(MomentDetailType.getIDFromDescription("TAGGING_ID"),
                    MomentDetailType.getDescriptionFromID(54));
            if (OrmMoment.NO_ID.equals(moment.getAnalyticsId())) {
                OrmMomentDetail detail = new OrmMomentDetail(detailType, moment);
                detail.setValue(uuidGenerator.generateRandomUUID());
                moment.addMomentDetail(detail);
                moment.setSynced(false);
                ormMomentDao.update(moment);
            }
        }
    }


    public void dropTables(final ConnectionSource connectionSource) throws SQLException {
        TableUtils.dropTable(connectionSource, OrmMoment.class, true);
        TableUtils.dropTable(connectionSource, OrmMomentType.class, true);
        TableUtils.dropTable(connectionSource, OrmMomentDetail.class, true);
        TableUtils.dropTable(connectionSource, OrmMomentDetailType.class, true);
        TableUtils.dropTable(connectionSource, OrmMeasurement.class, true);
        TableUtils.dropTable(connectionSource, OrmMeasurementType.class, true);
        TableUtils.dropTable(connectionSource, OrmMeasurementDetail.class, true);
        TableUtils.dropTable(connectionSource, OrmMeasurementDetailType.class, true);
        TableUtils.dropTable(connectionSource, OrmSynchronisationData.class, true);
        TableUtils.dropTable(connectionSource, OrmConsent.class, true);
        TableUtils.dropTable(connectionSource, OrmConsentDetail.class, true);
        TableUtils.dropTable(connectionSource, OrmCharacteristics.class, true);
        TableUtils.dropTable(connectionSource, OrmCharacteristicsDetail.class, true);
    }

    public Dao<OrmMoment, Integer> getMomentDao() throws SQLException {
        if (momentDao == null) {
            momentDao = getDao(OrmMoment.class);
        }
        return momentDao;
    }

    private Dao<OrmMomentType, Integer> getMomentTypeDao() throws SQLException {
        if (momentTypeDao == null) {
            momentTypeDao = getDao(OrmMomentType.class);
        }
        return momentTypeDao;
    }

    public Dao<OrmMeasurementGroupDetailType, Integer> getMeasurementGroupDetailTypeDao() throws SQLException {
        if (measurementGroupDetailTypes == null) {
            measurementGroupDetailTypes = getDao(OrmMeasurementGroupDetailType.class);
        }
        return measurementGroupDetailTypes;
    }

    public Dao<OrmMeasurementGroup, Integer> getMeasurementGroupDao() throws SQLException {
        if (measurementGroup == null) {
            measurementGroup = getDao(OrmMeasurementGroup.class);
        }
        return measurementGroup;
    }

    public Dao<OrmMeasurementGroupDetail, Integer> getMeasurementGroupDetailDao() throws SQLException {
        if (measurementGroupDetails == null) {
            measurementGroupDetails = getDao(OrmMeasurementGroupDetail.class);
        }
        return measurementGroupDetails;
    }

    public Dao<OrmMomentDetail, Integer> getMomentDetailDao() throws SQLException {
        if (momentDetailDao == null) {
            momentDetailDao = getDao(OrmMomentDetail.class);
        }
        return momentDetailDao;
    }

    private Dao<OrmMomentDetailType, Integer> getMomentDetailTypeDao() throws SQLException {
        if (momentDetailTypeDao == null) {
            momentDetailTypeDao = getDao(OrmMomentDetailType.class);
        }
        return momentDetailTypeDao;
    }

    public Dao<OrmMeasurement, Integer> getMeasurementDao() throws SQLException {
        if (measurementDao == null) {
            measurementDao = getDao(OrmMeasurement.class);
        }
        return measurementDao;
    }

    private Dao<OrmMeasurementType, Integer> getMeasurementTypeDao() throws SQLException {
        if (measurementTypeDao == null) {
            measurementTypeDao = getDao(OrmMeasurementType.class);
        }
        return measurementTypeDao;
    }

    public Dao<OrmMeasurementDetail, Integer> getMeasurementDetailDao() throws SQLException {
        if (measurementDetailDao == null) {
            measurementDetailDao = getDao(OrmMeasurementDetail.class);
        }
        return measurementDetailDao;
    }

    private Dao<OrmMeasurementDetailType, Integer> getMeasurementDetailTypeDao() throws SQLException {
        if (measurementDetailTypeDao == null) {
            measurementDetailTypeDao = getDao(OrmMeasurementDetailType.class);
        }
        return measurementDetailTypeDao;
    }

    public Dao<OrmSynchronisationData, Integer> getSynchronisationDataDao() throws SQLException {
        if (synchronisationDataDao == null) {
            synchronisationDataDao = getDao(OrmSynchronisationData.class);
        }
        return synchronisationDataDao;
    }

    public Dao<OrmConsent, Integer> getConsentDao() throws SQLException {
        if (consentDao == null) {
            consentDao = getDao(OrmConsent.class);
        }
        return consentDao;
    }

    public Dao<OrmConsentDetail, Integer> getConsentDetailsDao() throws SQLException {
        if (consentDetailDao == null) {
            consentDetailDao = getDao(OrmConsentDetail.class);
        }
        return consentDetailDao;
    }

    public Dao<OrmCharacteristics, Integer> getCharacteristicsDao() throws SQLException {
        if (characteristicsesDao == null) {
            characteristicsesDao = getDao(OrmCharacteristics.class);
        }
        return characteristicsesDao;
    }

    public Dao<OrmCharacteristicsDetail, Integer> getCharacteristicsDetailsDao() throws SQLException {
        if (characteristicsDetailsDao == null) {
            characteristicsDetailsDao = getDao(OrmCharacteristicsDetail.class);
        }
        return characteristicsDetailsDao;
    }


}
