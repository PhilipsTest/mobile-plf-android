package com.philips.platform.dprdemo.utils;

import android.content.Context;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.philips.cdp.registration.User;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.UuidGenerator;
import com.philips.platform.datasync.userprofile.UserRegistrationInterface;
import com.philips.platform.dprdemo.database.DatabaseHelper;
import com.philips.platform.dprdemo.database.ORMSavingInterfaceImpl;
import com.philips.platform.dprdemo.database.ORMUpdatingInterfaceImpl;
import com.philips.platform.dprdemo.database.OrmCreator;
import com.philips.platform.dprdemo.database.OrmDeleting;
import com.philips.platform.dprdemo.database.OrmDeletingInterfaceImpl;
import com.philips.platform.dprdemo.database.OrmFetchingInterfaceImpl;
import com.philips.platform.dprdemo.database.OrmSaving;
import com.philips.platform.dprdemo.database.OrmUpdating;
import com.philips.platform.dprdemo.database.table.BaseAppDateTime;
import com.philips.platform.dprdemo.database.table.OrmCharacteristics;
import com.philips.platform.dprdemo.database.table.OrmConsentDetail;
import com.philips.platform.dprdemo.database.table.OrmDCSync;
import com.philips.platform.dprdemo.database.table.OrmInsight;
import com.philips.platform.dprdemo.database.table.OrmInsightMetaData;
import com.philips.platform.dprdemo.database.table.OrmMeasurement;
import com.philips.platform.dprdemo.database.table.OrmMeasurementDetail;
import com.philips.platform.dprdemo.database.table.OrmMeasurementGroup;
import com.philips.platform.dprdemo.database.table.OrmMeasurementGroupDetail;
import com.philips.platform.dprdemo.database.table.OrmMoment;
import com.philips.platform.dprdemo.database.table.OrmMomentDetail;
import com.philips.platform.dprdemo.database.table.OrmSettings;
import com.philips.platform.dprdemo.database.table.OrmSynchronisationData;
import com.philips.platform.dprdemo.error.ErrorHandlerInterfaceImpl;
import com.philips.platform.dprdemo.registration.UserRegistrationInterfaceImpl;

import java.sql.SQLException;


public class DemoUAppManager {

    public DatabaseHelper databaseHelper;
    public AppInfraInterface mAppInfra;
    public LoggingInterface loggingInterface;
    private Context mContext;
    private AppConfigurationInterface.AppConfigurationError configError;
    DataServicesManager mDataServicesManager;
    UserRegistrationInterfaceImpl userRegImple;
    final String AI = "appinfra";
    private static DemoUAppManager sDemoUAppManager;
    private DemoUAppManager(){

    }

    public static synchronized DemoUAppManager getInstance() {
        if (sDemoUAppManager == null) {
            return sDemoUAppManager = new DemoUAppManager();
        }
        return sDemoUAppManager;
    }
    public AppInfraInterface getAppInfra() {
        return mAppInfra;
    }

    public LoggingInterface getLoggingInterface() {
        return loggingInterface;
    }

    public Context getAppContext() {
        return mContext;
    }

    private void initAppInfra(AppInfraInterface gAppInfra) {
        loggingInterface = gAppInfra.getLogging().createInstanceForComponent("DataSync", "DataSync");
    }

    public UserRegistrationInterfaceImpl getUserRegImple() {
        return userRegImple;
    }

    private void init() {
        mDataServicesManager = DataServicesManager.getInstance();
        OrmCreator creator = new OrmCreator(new UuidGenerator());
        userRegImple = new UserRegistrationInterfaceImpl(mContext, new User(mContext));
        UserRegistrationInterface userRegistrationInterface = userRegImple;
        ErrorHandlerInterfaceImpl errorHandlerInterface = new ErrorHandlerInterfaceImpl();
        mDataServicesManager.initializeDataServices(mContext, creator, userRegistrationInterface, errorHandlerInterface);
        injectDBInterfacesToCore();
        mDataServicesManager.initializeSyncMonitors(mContext, null, null);
    }

    void injectDBInterfacesToCore() {
        try {
            Dao<OrmMoment, Integer> momentDao = databaseHelper.getMomentDao();
            Dao<OrmMomentDetail, Integer> momentDetailDao = databaseHelper.getMomentDetailDao();
            Dao<OrmMeasurement, Integer> measurementDao = databaseHelper.getMeasurementDao();
            Dao<OrmMeasurementDetail, Integer> measurementDetailDao = databaseHelper.getMeasurementDetailDao();
            Dao<OrmSynchronisationData, Integer> synchronisationDataDao = databaseHelper.getSynchronisationDataDao();
            Dao<OrmMeasurementGroup, Integer> measurementGroup = databaseHelper.getMeasurementGroupDao();
            Dao<OrmMeasurementGroupDetail, Integer> measurementGroupDetails = databaseHelper.getMeasurementGroupDetailDao();

            Dao<OrmConsentDetail, Integer> consentDetailsDao = databaseHelper.getConsentDetailsDao();
            Dao<OrmCharacteristics, Integer> characteristicsesDao = databaseHelper.getCharacteristicsDao();

            Dao<OrmSettings, Integer> settingsDao = databaseHelper.getSettingsDao();
            Dao<OrmInsight, Integer> insightsDao = databaseHelper.getInsightDao();
            Dao<OrmInsightMetaData, Integer> insightMetaDataDao = databaseHelper.getInsightMetaDataDao();

            Dao<OrmDCSync, Integer> dcSyncDao = databaseHelper.getDCSyncDao();
            OrmSaving saving = new OrmSaving(momentDao, momentDetailDao, measurementDao, measurementDetailDao,
                    synchronisationDataDao, consentDetailsDao, measurementGroup, measurementGroupDetails,
                    characteristicsesDao, settingsDao, insightsDao, insightMetaDataDao, dcSyncDao);


            OrmUpdating updating = new OrmUpdating(momentDao, momentDetailDao, measurementDao, measurementDetailDao, settingsDao,
                    consentDetailsDao, dcSyncDao, measurementGroup, synchronisationDataDao, measurementGroupDetails);
            OrmFetchingInterfaceImpl fetching = new OrmFetchingInterfaceImpl(momentDao, synchronisationDataDao, consentDetailsDao, characteristicsesDao,
                    settingsDao, dcSyncDao, insightsDao);
            OrmDeleting deleting = new OrmDeleting(momentDao, momentDetailDao, measurementDao,
                    measurementDetailDao, synchronisationDataDao, measurementGroupDetails, measurementGroup, consentDetailsDao, characteristicsesDao, settingsDao, dcSyncDao, insightsDao, insightMetaDataDao);


            BaseAppDateTime uGrowDateTime = new BaseAppDateTime();
            ORMSavingInterfaceImpl ORMSavingInterfaceImpl = new ORMSavingInterfaceImpl(saving, updating, fetching, deleting, uGrowDateTime);
            OrmDeletingInterfaceImpl ORMDeletingInterfaceImpl = new OrmDeletingInterfaceImpl(deleting, saving, fetching);
            ORMUpdatingInterfaceImpl dbInterfaceOrmUpdatingInterface = new ORMUpdatingInterfaceImpl(saving, updating, fetching, deleting);
            OrmFetchingInterfaceImpl dbInterfaceOrmFetchingInterface = new OrmFetchingInterfaceImpl(momentDao, synchronisationDataDao, consentDetailsDao,
                    characteristicsesDao, settingsDao, dcSyncDao, insightsDao);

            mDataServicesManager.initializeDatabaseMonitor(mContext, ORMDeletingInterfaceImpl, dbInterfaceOrmFetchingInterface, ORMSavingInterfaceImpl, dbInterfaceOrmUpdatingInterface);
        } catch (SQLException exception) {
            Toast.makeText(mContext, "db injection failed to dataservices", Toast.LENGTH_SHORT).show();
        }
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public void initPreRequisite(Context context, AppInfraInterface appInfra) {
        this.mContext=context;
        this.mAppInfra =appInfra;
        configError = new
                AppConfigurationInterface.AppConfigurationError();
        databaseHelper=DatabaseHelper.getInstance(context,new UuidGenerator());
        initAppInfra(mAppInfra);
        init();
    }

}