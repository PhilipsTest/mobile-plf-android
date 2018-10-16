/* Copyright (c) Koninklijke Philips N.V., 2017
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.core.trackers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.core.BaseAppCore;
import com.philips.platform.core.BaseAppDataCreator;
import com.philips.platform.core.ErrorHandlingInterface;
import com.philips.platform.core.Eventing;
import com.philips.platform.core.datatypes.Characteristics;
import com.philips.platform.core.datatypes.DSPagination;
import com.philips.platform.core.datatypes.Insight;
import com.philips.platform.core.datatypes.Measurement;
import com.philips.platform.core.datatypes.MeasurementDetail;
import com.philips.platform.core.datatypes.MeasurementGroup;
import com.philips.platform.core.datatypes.MeasurementGroupDetail;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.MomentDetail;
import com.philips.platform.core.datatypes.Settings;
import com.philips.platform.core.dbinterfaces.DBDeletingInterface;
import com.philips.platform.core.dbinterfaces.DBFetchingInterface;
import com.philips.platform.core.dbinterfaces.DBSavingInterface;
import com.philips.platform.core.dbinterfaces.DBUpdatingInterface;
import com.philips.platform.core.events.CreateSubjectProfileRequestEvent;
import com.philips.platform.core.events.DataClearRequest;
import com.philips.platform.core.events.DatabaseSettingsSaveRequest;
import com.philips.platform.core.events.DatabaseSettingsUpdateRequest;
import com.philips.platform.core.events.DeleteAllInsights;
import com.philips.platform.core.events.DeleteAllMomentsRequest;
import com.philips.platform.core.events.DeleteExpiredInsightRequest;
import com.philips.platform.core.events.DeleteExpiredMomentRequest;
import com.philips.platform.core.events.DeleteInsightFromDB;
import com.philips.platform.core.events.DeleteSubjectProfileRequestEvent;
import com.philips.platform.core.events.DeleteSyncedMomentsRequest;
import com.philips.platform.core.events.FetchInsightsFromDB;
import com.philips.platform.core.events.GetPairedDeviceRequestEvent;
import com.philips.platform.core.events.GetSubjectProfileListRequestEvent;
import com.philips.platform.core.events.GetSubjectProfileRequestEvent;
import com.philips.platform.core.events.InsightsSaveRequest;
import com.philips.platform.core.events.LoadLatestMomentByTypeRequest;
import com.philips.platform.core.events.LoadMomentsByDate;
import com.philips.platform.core.events.LoadMomentsRequest;
import com.philips.platform.core.events.LoadSettingsRequest;
import com.philips.platform.core.events.LoadUserCharacteristicsRequest;
import com.philips.platform.core.events.MomentDeleteRequest;
import com.philips.platform.core.events.MomentSaveRequest;
import com.philips.platform.core.events.MomentUpdateRequest;
import com.philips.platform.core.events.MomentsDeleteRequest;
import com.philips.platform.core.events.MomentsSaveRequest;
import com.philips.platform.core.events.MomentsUpdateRequest;
import com.philips.platform.core.events.PairDevicesRequestEvent;
import com.philips.platform.core.events.RegisterDeviceToken;
import com.philips.platform.core.events.UnPairDeviceRequestEvent;
import com.philips.platform.core.events.UnRegisterDeviceToken;
import com.philips.platform.core.events.UserCharacteristicsSaveRequest;
import com.philips.platform.core.injection.AppComponent;
import com.philips.platform.core.injection.ApplicationModule;
import com.philips.platform.core.injection.BackendModule;
import com.philips.platform.core.injection.DaggerAppComponent;
import com.philips.platform.core.listeners.DBChangeListener;
import com.philips.platform.core.listeners.DBFetchRequestListner;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.listeners.DevicePairingListener;
import com.philips.platform.core.listeners.RegisterDeviceTokenListener;
import com.philips.platform.core.listeners.SubjectProfileListener;
import com.philips.platform.core.listeners.SynchronisationCompleteListener;
import com.philips.platform.core.utils.DataServicesConstants;
import com.philips.platform.core.utils.DataServicesLogger;
import com.philips.platform.core.utils.EventingImpl;
import com.philips.platform.datasync.UCoreAccessProvider;
import com.philips.platform.datasync.subjectProfile.UCoreCreateSubjectProfileRequest;
import com.philips.platform.datasync.synchronisation.DataFetcher;
import com.philips.platform.datasync.synchronisation.DataSender;
import com.philips.platform.datasync.synchronisation.SynchronisationManager;
import com.philips.platform.datasync.synchronisation.SynchronisationMonitor;
import com.philips.platform.datasync.userprofile.UserRegistrationInterface;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * The Public Interface for the Propositions for initializing and using Data-Services Component
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DataServicesManager {

    private static AppComponent mAppComponent;
    private static final String GDPR_MIGRATION_FLAG_STORAGE = "dsc_gdpr_migration_flag_storage";
    private static final String GDPR_MIGRATION_FLAG = "gdpr_migration_flag";

    private DBDeletingInterface mDeletingInterface;
    private DBFetchingInterface mFetchingInterface;
    private DBSavingInterface mSavingInterface;
    private DBUpdatingInterface mUpdatingInterface;

    private ServiceDiscoveryInterface mServiceDiscoveryInterface;
    private AppInfraInterface mAppInfra;

    private ArrayList<DataFetcher> mCustomFetchers;
    private ArrayList<DataSender> mCustomSenders;
    private Set<String> mSyncDataTypes;
    private List<String> supportedMomentTypes = new ArrayList<>();

    public String mDataServicesBaseUrl;
    public String mDataServicesCoachingServiceUrl;


    private DBChangeListener dbChangeListener;
    SynchronisationCompleteListener mSynchronisationCompleteListener;

    @Inject
    Eventing mEventing;

    @Inject
    BaseAppDataCreator dataCreator;

    @Inject
    UCoreAccessProvider mBackendIdProvider;

    @Inject
    BaseAppCore mCore;

    @Inject
    SynchronisationMonitor mSynchronisationMonitor;

    @Inject
    SynchronisationManager mSynchronisationManager;

    private static DataServicesManager sDataServicesManager;

    @Inject
    UserRegistrationInterface userRegistrationInterface;

    @Inject
    ErrorHandlingInterface errorHandlingInterface;

    SharedPreferences gdprStorage;
    private Context dataServiceContext;

    @Singleton
    private DataServicesManager() {
    }

    public static synchronized DataServicesManager getInstance() {
        if (sDataServicesManager == null) {
            return sDataServicesManager = new DataServicesManager();
        }
        return sDataServicesManager;
    }

    public void initializeDataServices(Context context, BaseAppDataCreator creator,
                                       UserRegistrationInterface userRegistrationInterface,
                                       ErrorHandlingInterface errorHandlingInterface, AppInfraInterface appInfraInterface) {
        this.dataCreator = creator;
        this.userRegistrationInterface = userRegistrationInterface;
        this.errorHandlingInterface = errorHandlingInterface;
        this.mAppInfra = appInfraInterface;
        this.mServiceDiscoveryInterface = mAppInfra.getServiceDiscovery();
        this.dataServiceContext = context;
        this.gdprStorage = context.getSharedPreferences(GDPR_MIGRATION_FLAG_STORAGE, Context.MODE_PRIVATE);
        this.supportedMomentTypes = (ArrayList<String>) appInfraInterface.getConfigInterface().getPropertyForKey("supportedMomentTypes", "dataservices", new AppConfigurationInterface.AppConfigurationError());
        initLogger();
    }

    private void initLogger() {
        DataServicesLogger.init();
        DataServicesLogger.enableLogging();
    }

    @Deprecated
    @SuppressWarnings("rawtypes")
    public void initializeSyncMonitors(Context context, ArrayList<DataFetcher> customFetchers,
                                       ArrayList<DataSender> customSenders, SynchronisationCompleteListener synchronisationCompleteListener) {
        this.mCustomFetchers = customFetchers;
        this.mCustomSenders = customSenders;
        this.mSynchronisationCompleteListener = synchronisationCompleteListener;
        buildDaggerComponent(context);
        startMonitors();
    }

    @SuppressWarnings("rawtypes")
    public void initializeSyncMonitors(Context context, ArrayList<DataFetcher> customFetchers, ArrayList<DataSender> customSenders) {
        this.mCustomFetchers = customFetchers;
        this.mCustomSenders = customSenders;
        buildDaggerComponent(context);
        startMonitors();
    }

    private void buildDaggerComponent(Context context) {
        BackendModule backendModule = new BackendModule(new EventingImpl(new EventBus(), new Handler()), dataCreator, userRegistrationInterface,
                mDeletingInterface, mFetchingInterface, mSavingInterface, mUpdatingInterface,
                mCustomFetchers, mCustomSenders, errorHandlingInterface);
        final ApplicationModule applicationModule = new ApplicationModule(context, mAppInfra);

        mAppComponent = DaggerAppComponent.builder().backendModule(backendModule).applicationModule(applicationModule).build();
        mAppComponent.injectApplication(this);
    }

    @Deprecated
    public void initializeDatabaseMonitor(Context context, DBDeletingInterface deletingInterface,
                                          DBFetchingInterface fetchingInterface, DBSavingInterface savingInterface, DBUpdatingInterface updatingInterface) {
        initializeDatabaseMonitor(deletingInterface, fetchingInterface, savingInterface, updatingInterface);
    }

    public void initializeDatabaseMonitor(DBDeletingInterface deletingInterface,
                                          DBFetchingInterface fetchingInterface, DBSavingInterface savingInterface, DBUpdatingInterface updatingInterface) {
        this.mDeletingInterface = deletingInterface;
        this.mFetchingInterface = fetchingInterface;
        this.mSavingInterface = savingInterface;
        this.mUpdatingInterface = updatingInterface;
    }

    public void synchronize() {
        clearExpiredMoments(null);
        synchronized (this) {
            startMonitors();
            mSynchronisationManager.startSync(mSynchronisationCompleteListener);
        }
    }

    public void synchronizeMomentsByDateRange(DateTime startDate, DateTime endDate, SynchronisationCompleteListener synchronisationCompleteListener) {
        synchronized (this) {
            startMonitors();
            mSynchronisationManager.startSync(startDate, endDate, synchronisationCompleteListener);
        }
    }

    private void startMonitors() {
        if (mCore != null) {
            mCore.start();
        }
        if (mSynchronisationMonitor != null) {
            mSynchronisationMonitor.start(mEventing);
        }
    }

    public void stopCore() {
        synchronized (this) {
            if (mCore != null)
                mCore.stop();
            if (mSynchronisationMonitor != null)
                mSynchronisationMonitor.stop();

            mSynchronisationManager.stopSync();
        }
    }

    @NonNull
    public Moment createMoment(@NonNull final String type) {
        if (!isSupported(type)) {
            throw new UnsupportedMomentTypeException();
        }
        return dataCreator.createMoment(mBackendIdProvider.getUserId(), mBackendIdProvider.getSubjectId(), type, null);
    }

    @NonNull
    public MomentDetail createMomentDetail(@NonNull final String type, String value, @NonNull final Moment moment) {
        MomentDetail momentDetail = dataCreator.createMomentDetail(type, moment);
        moment.addMomentDetail(momentDetail);
        momentDetail.setValue(value);
        return momentDetail;
    }

    @NonNull
    public Measurement createMeasurement(@NonNull final String type, String value, String unit, @NonNull final MeasurementGroup measurementGroup) {
        Measurement measurement = dataCreator.createMeasurement(type, measurementGroup);
        measurement.setValue(value);
        measurement.setUnit(unit);
        measurementGroup.addMeasurement(measurement);
        return measurement;
    }

    @NonNull
    public MeasurementDetail createMeasurementDetail(@NonNull final String type,
                                                     String value, @NonNull final Measurement measurement) {
        MeasurementDetail measurementDetail = dataCreator.createMeasurementDetail(type, measurement);
        measurementDetail.setValue(value);
        measurement.addMeasurementDetail(measurementDetail);
        return measurementDetail;
    }

    @NonNull
    public MeasurementGroup createMeasurementGroup(@NonNull final Moment moment) {
        return dataCreator.createMeasurementGroup(moment);
    }

    @NonNull
    public MeasurementGroup createMeasurementGroup(@NonNull final MeasurementGroup measurementGroup) {
        return dataCreator.createMeasurementGroup(measurementGroup);
    }

    public MeasurementGroupDetail createMeasurementGroupDetail(String type, String value, MeasurementGroup mMeasurementGroup) {
        MeasurementGroupDetail measurementGroupDetail = dataCreator.createMeasurementGroupDetail(type, mMeasurementGroup);
        measurementGroupDetail.setValue(value);
        mMeasurementGroup.addMeasurementGroupDetail(measurementGroupDetail);
        return measurementGroupDetail;
    }

    public void deleteMoment(final Moment moment, DBRequestListener<Moment> dbRequestListener) {
        mEventing.post(new MomentDeleteRequest(moment, dbRequestListener));
    }

    public void deleteMoments(final List<Moment> moments, DBRequestListener<Moment> dbRequestListener) {
        mEventing.post(new MomentsDeleteRequest(moments, dbRequestListener));
    }

    public void deleteAllMoments(DBRequestListener<Moment> dbRequestListener) {
        mEventing.post(new DeleteAllMomentsRequest(dbRequestListener));
    }

    public void clearExpiredMoments(DBRequestListener<Integer> dbRequestListener) {
        mEventing.post(new DeleteExpiredMomentRequest(dbRequestListener));
    }

    public void clearExpiredInsights(DBRequestListener<Insight> dbRequestListener) {
        mEventing.post(new DeleteExpiredInsightRequest(dbRequestListener));
    }

    public void updateMoment(Moment moment, DBRequestListener<Moment> dbRequestListener) {
        mEventing.post((new MomentUpdateRequest(moment, dbRequestListener)));
    }

    public void updateMoments(List<Moment> moments, DBRequestListener<Moment> dbRequestListener) {
        mEventing.post((new MomentsUpdateRequest(moments, dbRequestListener)));
    }

    public void saveMoment(@NonNull final Moment moment, DBRequestListener<Moment> dbRequestListener) {
        if (isSupported(moment.getType())) {
            mEventing.post(new MomentSaveRequest(moment, dbRequestListener));
        }
    }

    public void saveMoments(@NonNull final List<Moment> moments, DBRequestListener<Moment> dbRequestListener) {
        List<Moment> supportedMoments = filterUnsupportedMomentTypes(moments);
        mEventing.post(new MomentsSaveRequest(supportedMoments, dbRequestListener));
    }

    public void fetchMomentWithType(DBFetchRequestListner<Moment> dbFetchRequestListener, final @NonNull String... types) {
        if (!isSupported(types)) {
            throw new UnsupportedMomentTypeException();
        }
        mEventing.post(new LoadMomentsRequest(dbFetchRequestListener, types));
    }

    public void fetchMomentForMomentID(final int momentID, DBFetchRequestListner<Moment> dbFetchRequestListener) {
        mEventing.post(new LoadMomentsRequest(momentID, dbFetchRequestListener));
    }

    public void fetchLatestMomentByType(final @NonNull String type, DBFetchRequestListner<Moment> dbFetchRequestListener) {
        if (!isSupported(type)) {
            throw new UnsupportedMomentTypeException();
        }
        mEventing.post(new LoadLatestMomentByTypeRequest(type, dbFetchRequestListener));
    }

    public void fetchAllMoment(DBFetchRequestListner<Moment> dbFetchRequestListener) {
        mEventing.post(new LoadMomentsRequest(dbFetchRequestListener));
    }

    public void fetchMomentsWithTimeLine(Date startDate, Date endDate, DSPagination dsPagination, DBFetchRequestListner<Moment> dbFetchRequestListener) {
        mEventing.post(new LoadMomentsByDate(startDate, endDate, dsPagination, dbFetchRequestListener));
    }

    public void fetchMomentsWithTypeAndTimeLine(String momentType, Date startDate, Date endDate, DSPagination dsPagination, DBFetchRequestListner<Moment> dbFetchRequestListener) {
        if (!isSupported(momentType)) {
            throw new UnsupportedMomentTypeException();
        }
        mEventing.post(new LoadMomentsByDate(momentType, startDate, endDate, dsPagination, dbFetchRequestListener));
    }

    public Settings createUserSettings(String locale, String unit, String timeZone) {
        return dataCreator.createSettings(unit, locale, timeZone);
    }

    public void saveUserSettings(Settings settings, DBRequestListener<Settings> dbRequestListener) {
        mEventing.post(new DatabaseSettingsSaveRequest(settings, dbRequestListener));
    }

    public void updateUserSettings(Settings settings, DBRequestListener<Settings> dbRequestListener) {
        mEventing.post(new DatabaseSettingsUpdateRequest(settings, dbRequestListener));
    }

    public void fetchUserSettings(DBFetchRequestListner<Settings> dbFetchRequestListener) {
        mEventing.post(new LoadSettingsRequest(dbFetchRequestListener));
    }

    public Characteristics createUserCharacteristics(@NonNull final String detailType, @NonNull final String detailValue, Characteristics characteristics) {
        Characteristics chDetail;
        if (characteristics != null) {
            chDetail = dataCreator.createCharacteristics(detailType, detailValue, characteristics);
        } else {
            chDetail = dataCreator.createCharacteristics(detailType, detailValue);
        }
        return chDetail;
    }

    public void saveUserCharacteristics(List<Characteristics> characteristicses, DBRequestListener<Characteristics> dbRequestListener) {
        mEventing.post(new UserCharacteristicsSaveRequest(characteristicses, dbRequestListener));
    }

    public void updateUserCharacteristics(List<Characteristics> characteristicses, DBRequestListener<Characteristics> dbRequestListener) {
        mEventing.post(new UserCharacteristicsSaveRequest(characteristicses, dbRequestListener));
    }

    public void fetchUserCharacteristics(DBFetchRequestListner<Characteristics> dbFetchRequestListener) {
        mEventing.post(new LoadUserCharacteristicsRequest(dbFetchRequestListener));
    }

    public void fetchInsights(DBFetchRequestListner dbFetchRequestListener) {
        mEventing.post(new FetchInsightsFromDB(dbFetchRequestListener));
    }

    public void deleteInsights(List<? extends Insight> insights, DBRequestListener<Insight> dbRequestListener) {
        mEventing.post(new DeleteInsightFromDB((List<Insight>) insights, dbRequestListener));
    }

    public void deleteAllInsights(DBRequestListener<Insight> dbRequestListener) {
        mEventing.post(new DeleteAllInsights(dbRequestListener));
    }

    public void saveInsights(List<Insight> insights) {
        mEventing.post(new InsightsSaveRequest());
    }

    public void deleteAll(DBRequestListener dbRequestListener) {
        mEventing.post(new DataClearRequest(dbRequestListener));
    }

    public void registerDeviceToken(String deviceToken, String appVariant, String protocolProvider, RegisterDeviceTokenListener registerDeviceTokenListener) {
        mEventing.post(new RegisterDeviceToken(deviceToken, appVariant, protocolProvider, registerDeviceTokenListener));
    }

    public void unRegisterDeviceToken(String appToken, String appVariant, RegisterDeviceTokenListener registerDeviceTokenListener) {
        mEventing.post(new UnRegisterDeviceToken(appToken, appVariant, registerDeviceTokenListener));
    }

    public void handlePushNotificationPayload(JSONObject jsonObject) {
        synchronize();
    }

    public void createSubjectProfile(String firstName, String dateOfBirth, String gender,
                                     double weight, String creationDate, SubjectProfileListener subjectProfileListener) {
        UCoreCreateSubjectProfileRequest uCoreCreateSubjectProfileRequest = new UCoreCreateSubjectProfileRequest();
        uCoreCreateSubjectProfileRequest.setFirstName(firstName);
        uCoreCreateSubjectProfileRequest.setDateOfBirth(dateOfBirth);
        uCoreCreateSubjectProfileRequest.setGender(gender);
        uCoreCreateSubjectProfileRequest.setWeight(weight);
        uCoreCreateSubjectProfileRequest.setCreationDate(creationDate);
        mEventing.post(new CreateSubjectProfileRequestEvent(uCoreCreateSubjectProfileRequest, subjectProfileListener));
    }

    public void getSubjectProfiles(SubjectProfileListener subjectProfileListener) {
        mEventing.post(new GetSubjectProfileListRequestEvent(subjectProfileListener));
    }

    public void getSubjectProfile(String subjectID, SubjectProfileListener subjectProfileListener) {
        mEventing.post(new GetSubjectProfileRequestEvent(subjectID, subjectProfileListener));
    }

    public void deleteSubjectProfile(String subjectID, SubjectProfileListener subjectProfileListener) {
        mEventing.post(new DeleteSubjectProfileRequestEvent(subjectID, subjectProfileListener));
    }

    public void pairDevices(String deviceID, String deviceType, List<String> subjectIds,
                            List<String> standardObservationNames, String relationshipType, DevicePairingListener devicePairingListener) {
        mEventing.post(new PairDevicesRequestEvent(deviceID, deviceType, standardObservationNames, subjectIds, relationshipType, devicePairingListener));
    }

    public void unPairDevice(String deviceID, DevicePairingListener devicePairingListener) {
        mEventing.post(new UnPairDeviceRequestEvent(deviceID, devicePairingListener));
    }

    public void getPairedDevices(DevicePairingListener devicePairingListener) {
        mEventing.post(new GetPairedDeviceRequestEvent(devicePairingListener));
    }

    public String fetchBaseUrlFromServiceDiscovery() {
        if (mServiceDiscoveryInterface == null) {
            throw new RuntimeException("Please initialize appinfra");
        }

        if (mDataServicesBaseUrl != null) {
            return mDataServicesBaseUrl;
        }

        final ConditionVariable fetchingServiceUrl = new ConditionVariable();

        mServiceDiscoveryInterface.getServiceUrlWithCountryPreference(DataServicesConstants.BASE_URL_KEY, new
                ServiceDiscoveryInterface.OnGetServiceUrlListener() {
                    @Override
                    public void onError(ERRORVALUES errorvalues, String s) {
                        errorHandlingInterface.onServiceDiscoveryError(s);
                        fetchingServiceUrl.open();
                    }

                    @Override
                    public void onSuccess(URL url) {
                        mDataServicesBaseUrl = url.toString();
                        fetchingServiceUrl.open();
                    }
                });

        fetchingServiceUrl.block();
        return mDataServicesBaseUrl;
    }

    public String fetchCoachingServiceUrlFromServiceDiscovery() {
        if (mServiceDiscoveryInterface == null) {
            throw new RuntimeException("Please initialize appinfra");
        }

        if (mDataServicesCoachingServiceUrl != null)
            return mDataServicesCoachingServiceUrl;

        final ConditionVariable fetchingServiceUrl = new ConditionVariable();

        mServiceDiscoveryInterface.getServiceUrlWithCountryPreference(DataServicesConstants.COACHING_SERVICE_URL_KEY, new
                ServiceDiscoveryInterface.OnGetServiceUrlListener() {
                    @Override
                    public void onError(ERRORVALUES errorvalues, String s) {
                        errorHandlingInterface.onServiceDiscoveryError(s);
                        fetchingServiceUrl.open();
                    }

                    @Override
                    public void onSuccess(URL url) {
                        mDataServicesCoachingServiceUrl = url.toString();
                        fetchingServiceUrl.open();
                    }
                });

        fetchingServiceUrl.block();
        return mDataServicesCoachingServiceUrl;
    }

    /**
     * Used for setting a Mock ServiceDiscoveryInterface for writing test cases
     * Should not be used by Propositions
     *
     * @param serviceDiscoveryInterface - ServiceDiscoveryInterface
     */
    public void setServiceDiscoveryInterface(final ServiceDiscoveryInterface serviceDiscoveryInterface) {
        this.mServiceDiscoveryInterface = serviceDiscoveryInterface;
    }

    public AppInfraInterface getAppInfra() {
        return mAppInfra;
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public void setAppComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
    }

    public ArrayList<DataFetcher> getCustomFetchers() {
        return mCustomFetchers;
    }

    public ArrayList<DataSender> getCustomSenders() {
        return mCustomSenders;
    }

    public void configureSyncDataType(Set<String> fetchers) {
        mSyncDataTypes = fetchers;
    }

    public Set<String> getSyncTypes() {
        return mSyncDataTypes;
    }

    public void registerDBChangeListener(DBChangeListener dbChangeListener) {
        this.dbChangeListener = dbChangeListener;
    }

    public void unRegisterDBChangeListener() {
        this.dbChangeListener = null;
    }

    public void registerSynchronisationCompleteListener(SynchronisationCompleteListener synchronisationCompleteListener) {
        this.mSynchronisationCompleteListener = synchronisationCompleteListener;
    }

    public void unRegisterSynchronisationCosmpleteListener() {
        this.mSynchronisationCompleteListener = null;
    }

    public DBChangeListener getDbChangeListener() {
        return dbChangeListener;
    }

    public void deleteSyncedMoments(final DBRequestListener<Moment> resultListener) {
        mEventing.post(new DeleteSyncedMomentsRequest(resultListener));
    }

    public void migrateGDPR(final DBRequestListener<Object> resultListener) {
        if (isGdprMigrationDone()) {
            resultListener.onSuccess(Collections.emptyList());
        } else {
            deleteSyncedMoments(new DBRequestListener<Moment>() {
                @Override
                public void onSuccess(final List<? extends Moment> momentData) {
                    deleteAllInsights(new DBRequestListener<Insight>() {
                        @Override
                        public void onSuccess(List<? extends Insight> insightData) {
                            mSynchronisationManager.resetLastExpirationDeletionDateTime();
                            runSync(resultListener);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            resultListener.onFailure(exception);
                        }
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    resultListener.onFailure(exception);
                }
            });
        }
    }

    public boolean isGdprMigrationDone() {
        return gdprStorage.getBoolean(GDPR_MIGRATION_FLAG, false);
    }

    public void resetGDPRMigrationFlag() {
        gdprStorage.edit().putBoolean(GDPR_MIGRATION_FLAG, false).apply();
    }

    public Context getDataServiceContext() {
        return dataServiceContext;
    }

    public void setDataServiceContext(Context dataServiceContext) {
        this.dataServiceContext = dataServiceContext;
    }

    public void clearLastSyncTimeCache() {
        mBackendIdProvider.clearSyncTimeCache();
    }

    public void resetLastSyncTimestampTo(DateTime lastSyncTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        String formattedLastSyncTime = sdf.format(lastSyncTime.toLocalDateTime().toDate());
        mBackendIdProvider.saveLastSyncTime(formattedLastSyncTime, UCoreAccessProvider.MOMENT_LAST_SYNC_URL_KEY);
        mBackendIdProvider.saveLastSyncTime(formattedLastSyncTime, UCoreAccessProvider.INSIGHT_LAST_SYNC_URL_KEY);
    }

    private void storeGdprMigrationFlag() {
        gdprStorage.edit().putBoolean(GDPR_MIGRATION_FLAG, true).apply();
    }

    private void runSync(final DBRequestListener<Object> resultListener) {
        mBackendIdProvider.clearSyncTimeCache();
        mSynchronisationManager.startSync(new SynchronisationCompleteListener() {
            @Override
            public void onSyncComplete() {
                // Post on main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        resultListener.onSuccess(Collections.emptyList());
                    }
                });
            }

            @Override
            public void onSyncFailed(final Exception exception) {
                // Post on main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        resultListener.onFailure(exception);
                    }
                });
            }
        });
        storeGdprMigrationFlag();
    }

    private List<Moment> filterUnsupportedMomentTypes(List<Moment> moments) {
        List<Moment> supportedMoments = new ArrayList<>();
        for (Moment moment : moments) {
            if (isSupported(moment.getType())) {
                supportedMoments.add(moment);
            }
        }
        return supportedMoments;
    }

    private boolean isSupported(String... types) {
        for (String type : types) {
            if (!supportedMomentTypes.isEmpty() && !supportedMomentTypes.contains(type)) {
                return false;
            }
        }
        return true;
    }

    public Insight createInsight(final String type) {
        final Insight insight = dataCreator.createInsight();
        insight.setGUId(UUID.randomUUID().toString());
        insight.setType(type);

        return insight;
    }
}
