package com.philips.platform.core.trackers;

import android.content.Context;

import com.philips.platform.core.BackendIdProvider;
import com.philips.platform.core.BaseAppCore;
import com.philips.platform.core.BaseAppDataCreator;
import com.philips.platform.core.ErrorHandlingInterface;
import com.philips.platform.core.datatypes.Characteristics;
import com.philips.platform.core.datatypes.ConsentDetail;
import com.philips.platform.core.datatypes.ConsentDetailStatusType;
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
import com.philips.platform.core.events.DatabaseConsentSaveRequest;
import com.philips.platform.core.events.DatabaseSettingsSaveRequest;
import com.philips.platform.core.events.DatabaseSettingsUpdateRequest;
import com.philips.platform.core.events.DeleteExpiredMomentRequest;
import com.philips.platform.core.events.DeleteInsightFromDB;
import com.philips.platform.core.events.DeleteSubjectProfileRequestEvent;
import com.philips.platform.core.events.Event;
import com.philips.platform.core.events.FetchInsightsFromDB;
import com.philips.platform.core.events.GetPairedDeviceRequestEvent;
import com.philips.platform.core.events.GetSubjectProfileListRequestEvent;
import com.philips.platform.core.events.GetSubjectProfileRequestEvent;
import com.philips.platform.core.events.LoadConsentsRequest;
import com.philips.platform.core.events.LoadLatestMomentByTypeRequest;
import com.philips.platform.core.events.LoadMomentsByDate;
import com.philips.platform.core.events.LoadMomentsRequest;
import com.philips.platform.core.events.LoadSettingsRequest;
import com.philips.platform.core.events.MomentDeleteRequest;
import com.philips.platform.core.events.MomentSaveRequest;
import com.philips.platform.core.events.MomentUpdateRequest;
import com.philips.platform.core.events.MomentsDeleteRequest;
import com.philips.platform.core.events.MomentsUpdateRequest;
import com.philips.platform.core.events.PairDevicesRequestEvent;
import com.philips.platform.core.events.RegisterDeviceToken;
import com.philips.platform.core.events.UnPairDeviceRequestEvent;
import com.philips.platform.core.events.UnRegisterDeviceToken;
import com.philips.platform.core.events.UserCharacteristicsSaveRequest;
import com.philips.platform.core.injection.AppComponent;
import com.philips.platform.core.listeners.DBChangeListener;
import com.philips.platform.core.listeners.DBFetchRequestListner;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.listeners.SynchronisationCompleteListener;
import com.philips.platform.datasync.UCoreAccessProvider;
import com.philips.platform.datasync.synchronisation.DataFetcher;
import com.philips.platform.datasync.synchronisation.DataSender;
import com.philips.platform.datasync.synchronisation.SynchronisationManager;
import com.philips.platform.datasync.synchronisation.SynchronisationMonitor;
import com.philips.platform.datasync.userprofile.UserRegistrationInterface;
import com.philips.platform.verticals.VerticalCreater;
import com.philips.platform.verticals.VerticalUCoreAccessProvider;
import com.philips.platform.verticals.VerticalUserRegistrationInterface;
import com.philips.spy.DSPaginationSpy;
import com.philips.spy.EventingSpy;
import com.philips.testing.verticals.datatyes.MomentType;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataServicesManagerTest {

    public static final int TEST_REFERENCE_ID = 111;
    public static final String TEST_USER_ID = "TEST_USER_ID";
    public static final String TEST_BABY_ID = "TEST_BABY_ID";
    private static final String TEST_CONSENT_DETAIL_TYPE = "TEMPERATURE";
    private String TEST_MOMENT_TYPE = "CRYING";
    private String TEST_MOMENT_DETAIL_TYPE = "STICKER";
    private String TEST_MEASUREMENT_TYPE = "DURATION";
    private String TEST_MEASUREMENT_DETAIL_TYPE = "BOTTLE_CONTENTS";

    @Mock
    DataSender dataSenderMock;

    @Mock
    SynchronisationCompleteListener synchronisationCompleteListenerMock;

    @Mock
    DataFetcher dataFetcherMock;

    private EventingSpy eventingSpy;

    @Mock
    JSONObject jsonObject;

    @Mock
    private File fileMock;

    @Mock
    SynchronisationManager synchronisationManagerMock;

    private UserRegistrationInterface userRegistrationInterface;

    @Mock
    SynchronisationCompleteListener mSynchronisationCompleteListener;

    private BaseAppDataCreator baseAppDataCreator;

    @Mock
    private BackendIdProvider backendIdProviderMock;

    @Mock
    private Event requestEventMock;

    @Mock
    Insight insightMock;

    @Mock
    private Moment momentMock;

    @Mock
    private MomentDetail momentDetailMock;

    @Mock
    private MeasurementGroup measurementGroupMock;
    @Mock
    private Measurement measurementMock;

    @Mock
    private MeasurementDetail measurementDetailMock;

    @Mock
    private MeasurementGroupDetail measurementGroupDetailMock;

    @Captor
    private ArgumentCaptor<MomentSaveRequest> momentSaveRequestCaptor;

    @Captor
    private ArgumentCaptor<MomentDeleteRequest> momentDeleteEventCaptor;

    @Captor
    private ArgumentCaptor<MomentUpdateRequest> momentUpdateEventCaptor;
    DataServicesManager tracker;
    @Mock
    DBDeletingInterface deletingInterfaceMock;
    @Mock
    DBFetchingInterface fetchingInterfaceMock;
    @Mock
    DBSavingInterface savingInterfaceMock;
    @Mock
    DBUpdatingInterface updatingInterfaceMock;
    @Spy
    Context mockContext;
    @Mock
    private ConsentDetail consentDetailMock;

    UCoreAccessProvider uCoreAccessProvider;

    @Mock
    BaseAppCore coreMock;

    @Mock
    SynchronisationMonitor synchronisationMonitorMock;

    @Mock
    ErrorHandlingInterface errorHandlingInterfaceMock;

    @Mock
    DBRequestListener dbRequestListener;

    @Mock
    Characteristics CharacteristicsMock;

    @Mock
    DBFetchRequestListner dbFetchRequestListner;

    @Mock
    private AppComponent appComponantMock;

    @Mock
    BaseAppDataCreator dataCreatorMock;

    @Mock
    SynchronisationCompleteListener synchronisationCompleteListener;

    @Mock
    Settings settingsMock;

    DSPaginationSpy mDSPagination;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        eventingSpy = new EventingSpy();

        tracker = DataServicesManager.getInstance();
        tracker.setAppComponant(appComponantMock);

        baseAppDataCreator = new VerticalCreater();
        userRegistrationInterface = new VerticalUserRegistrationInterface();
        uCoreAccessProvider = new VerticalUCoreAccessProvider(userRegistrationInterface);
        mDSPagination = new DSPaginationSpy();
        tracker.mEventing = eventingSpy;
        tracker.mDataCreater = baseAppDataCreator;
        tracker.mBackendIdProvider = uCoreAccessProvider;
        tracker.mCore = coreMock;
        tracker.mSynchronisationMonitor = synchronisationMonitorMock;
        tracker.userRegistrationInterface = userRegistrationInterface;
        tracker.errorHandlingInterface = errorHandlingInterfaceMock;
        tracker.mSynchronisationManager = synchronisationManagerMock;
        when(requestEventMock.getEventId()).thenReturn(TEST_REFERENCE_ID);
    }

    @Test
    public void ShouldPostSaveEvent_WhenSaveIsCalled() throws Exception {
        tracker.saveMoment(momentMock, dbRequestListener);
        verify(eventingSpy).post(any(MomentSaveRequest.class));
    }

    @Test
    public void ShouldPostUpdateEvent_WhenUpdateIsCalled() throws Exception {
        tracker.updateMoment(momentMock, dbRequestListener);
        verify(eventingSpy).post(any(MomentUpdateRequest.class));
    }

    @Test
    public void ShouldPostFetchEvent_WhenFetchIsCalled() throws Exception {
        tracker.fetchMomentWithType(dbFetchRequestListner, MomentType.TEMPERATURE);
        verify(eventingSpy).post(any(LoadMomentsRequest.class));
    }

    @Test
    public void ShouldPostFetchLatestMomentByType_WhenFetchIsCalled() throws Exception {
        tracker.fetchLatestMomentByType(MomentType.TEMPERATURE, dbFetchRequestListner);
        verify(eventingSpy).post(any(LoadLatestMomentByTypeRequest.class));
    }

    @Test
    public void ShouldPostFetchMomentByDateType_WhenFetchIsCalled() throws Exception {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date startDate = sdf.parse("10/11/17");
        Date endDate = sdf.parse("10/23/17");
        tracker.fetchMomentsWithTypeAndTimeLine(MomentType.TEMPERATURE, startDate, endDate, createPagination(), dbFetchRequestListner);
        verify(eventingSpy).post(any(LoadMomentsByDate.class));
    }

    @Test
    public void ShouldPostFetchMomentByDateRange_WhenFetchIsCalled() throws Exception {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date startDate = sdf.parse("10/11/17");
        Date endDate = sdf.parse("10/23/17");
        tracker.fetchMomentsWithTimeLine(startDate, endDate, createPagination(), dbFetchRequestListner);
        verify(eventingSpy).post(any(LoadMomentsByDate.class));
    }

    @Test
    public void ShouldPostFetchMomentByIdEvent_WhenFetchMomentByIdIsCalled() throws Exception {
        tracker.fetchMomentForMomentID(1, dbFetchRequestListner);
        verify(eventingSpy).post(any(LoadMomentsRequest.class));
    }

    @Test
    public void ShouldPostFetchConsentEvent_WhenFetchConsentIsCalled() throws Exception {
        tracker.fetchConsentDetail(dbFetchRequestListner);
        verify(eventingSpy).post(any(LoadConsentsRequest.class));
    }

    @Test
    public void ShouldPostFetchSettingsEvent_WhenFetchSettingsIsCalled() throws Exception {
        tracker.fetchUserSettings(dbFetchRequestListner);
        verify(eventingSpy).post(any(LoadSettingsRequest.class));
    }

    @Test
    public void ShouldCreateConsentDetail_WhenCreateConsentDetailIsCalled() throws Exception {
        tracker.createConsentDetail(TEST_CONSENT_DETAIL_TYPE, ConsentDetailStatusType.ACCEPTED, ConsentDetail.DEFAULT_DOCUMENT_VERSION, "fsdfsdf");
    }

    @Test
    public void ShouldAddConcentDetail_WhenConsentIsNull() throws Exception {
        tracker.createConsentDetail("Phase", ConsentDetailStatusType.ACCEPTED, "2", "fsdfsdf");
    }

    @Test
    public void ShouldPostSaveConsentEvent_WhenSaveConsentIsCalled() throws Exception {
        tracker.saveConsentDetails(anyListOf(ConsentDetail.class), dbRequestListener);
        verify(eventingSpy).post(any(DatabaseConsentSaveRequest.class));
    }

    @Test
    public void ShouldPostUpdateSettingsEvent_WhenUpdateSettingsIsCalled() throws Exception {
        tracker.updateUserSettings(any(Settings.class), dbRequestListener);
        verify(eventingSpy).post(any(DatabaseSettingsUpdateRequest.class));
    }

    @Test
    public void ShouldPostUpdateCharacteristicsRequest_WhenUpdateCharacteristicsIsCalled() throws Exception {
        tracker.updateUserCharacteristics(anyListOf(Characteristics.class), dbRequestListener);
    }

    @Test
    public void ShouldPostFetchCharacteristicsRequest_WhenFetchCharacteristicsIsCalled() throws Exception {
        tracker.fetchUserCharacteristics(dbFetchRequestListner);
    }

    @Test
    public void ShouldPostUpdateConsentEvent_WhenUpdateConsentIsCalled() throws Exception {
        tracker.updateConsentDetails(anyListOf(ConsentDetail.class), dbRequestListener);
        verify(eventingSpy).post(any(DatabaseConsentSaveRequest.class));
    }

    @Test
    public void ShouldPostdeleteAllMomentEvent_WhendeleteAllMomentIsCalled() throws Exception {
        tracker.deleteAllMoments(dbRequestListener);
        verify(eventingSpy).post(any(DataClearRequest.class));
    }

    //TODO: Spoorti - revisit this
    @Test
    public void ShouldCreateMoment_WhenCreateMomentIsCalled() throws Exception {
        tracker.createMoment("jh");
    }

    @Test
    public void ShouldCreateMeasurementGroup_WhenCreateMeasurementGroupIsCalled() throws Exception {
        tracker.createMeasurementGroup(momentMock);
    }

    //TODO: Spoorti - revisit
    @Test
    public void ShouldAddMomentDetail_WhenCreateMomentDetailIsCreated() throws Exception {
        baseAppDataCreator.createMomentDetail(TEST_MEASUREMENT_DETAIL_TYPE, momentMock);
    }

    @Test
    public void ShouldStopCore_WhenStopCoreIsCalled() throws Exception {
        tracker.stopCore();
    }

    @Test(expected = RuntimeException.class)
    public void ShouldinitializeSyncMonitors_WheninitializeSyncMonitorsIsCalled() throws Exception {
        tracker.initializeSyncMonitors(null, new ArrayList<DataFetcher>(), new ArrayList<DataSender>(), synchronisationCompleteListener);
    }

    @Test
    public void ShouldCreateMeasurement_WhenqCreateMeasurementIsCalled() throws Exception {
        tracker.createMeasurementGroup(measurementGroupMock);
    }

    @Test
    public void ShouldInitializeDBMonitors_WhenInitializeDBMonitorsIsCalled() throws Exception {
        tracker.initializeDatabaseMonitor(null, deletingInterfaceMock, fetchingInterfaceMock, savingInterfaceMock, updatingInterfaceMock);
    }

    @Test
    public void ShouldCreateCharacteristicsDetails_WhenCreateCharacteristicsDetailsIsCalled() throws Exception {
        tracker.createUserCharacteristics("TYPE", "VALUE", mock(Characteristics.class));
    }

    @Test
    public void ShouldCreateCharacteristicsDetails_WhenCreateCharacteristicsDetailIsNULL() throws Exception {
        tracker.createUserCharacteristics("TYPE", "VALUE", null);
    }

    @Test
    public void Should_fetchAllMoment_called() throws Exception {
        tracker.fetchAllMoment(dbFetchRequestListner);
        verify(eventingSpy).post(any(LoadMomentsRequest.class));
    }

    @Test
    public void Should_createUserSettings_called() throws Exception {
        Settings settings = tracker.createUserSettings("en_us", "metric");
        assertThat(settings).isNotNull();
        assertThat(settings).isInstanceOf(Settings.class);
    }

    @Test
    public void Should_createsaveUserSettings_called() throws Exception {
        tracker.saveUserSettings(settingsMock, dbRequestListener);
        verify(eventingSpy).post(any(DatabaseSettingsSaveRequest.class));
    }

    @Test
    public void Should_createMomentDetail_called() throws Exception {
        tracker.mDataCreater = dataCreatorMock;
        when(dataCreatorMock.createMomentDetail("Temperature", momentMock)).thenReturn(momentDetailMock);
        MomentDetail detail = tracker.createMomentDetail("Temperature", "23", momentMock);
        //verify(eventingSpy).post(any(DatabaseSettingsSaveRequest.class));
        assertThat(detail).isInstanceOf(MomentDetail.class);
        assertThat(detail).isNotNull();
    }

    @Test
    public void Should_createMeasurement_called() throws Exception {
        tracker.mDataCreater = dataCreatorMock;
        when(dataCreatorMock.createMeasurement("Temperature", measurementGroupMock)).thenReturn(measurementMock);
        Measurement measurement = tracker.createMeasurement("Temperature", "23", "celcius", measurementGroupMock);
        //verify(eventingSpy).post(any(DatabaseSettingsSaveRequest.class));
        assertThat(measurement).isInstanceOf(Measurement.class);
        assertThat(measurement).isNotNull();
    }

    @Test
    public void Should_createMeasurementDetail_called() throws Exception {
        tracker.mDataCreater = dataCreatorMock;
        when(dataCreatorMock.createMeasurementDetail("Temperature", measurementMock)).thenReturn(measurementDetailMock);
        MeasurementDetail measurementDetail = tracker.createMeasurementDetail("Temperature", "23", measurementMock);
        //verify(eventingSpy).post(any(DatabaseSettingsSaveRequest.class));
        assertThat(measurementDetail).isInstanceOf(MeasurementDetail.class);
        assertThat(measurementDetail).isNotNull();
    }

    @Test
    public void Should_deleteMoment_called() throws Exception {
        tracker.deleteMoment(momentMock, dbRequestListener);
        verify(eventingSpy).post(any(MomentDeleteRequest.class));
    }

    @Test
    public void Should_deleteMoments_called() throws Exception {
        List list = new ArrayList();
        list.add(momentMock);
        tracker.deleteMoments(list, dbRequestListener);
        verify(eventingSpy).post(any(MomentsDeleteRequest.class));
    }

    @Test
    public void Should_updateMoments_called() throws Exception {
        List list = new ArrayList();
        list.add(momentMock);
        tracker.updateMoments(list, dbRequestListener);
        verify(eventingSpy).post(any(MomentsUpdateRequest.class));
    }

    @Test
    public void Should_deleteAll_called() throws Exception {
        tracker.deleteAll(dbRequestListener);
        verify(eventingSpy).post(any(DataClearRequest.class));
    }

    @Test
    public void Should_createMeasurementGroupDetail_called() throws Exception {
        tracker.mDataCreater = dataCreatorMock;
        when(dataCreatorMock.createMeasurementGroupDetail("Temperature", measurementGroupMock)).thenReturn(measurementGroupDetailMock);
        MeasurementGroupDetail measurementGroupDetail = tracker.createMeasurementGroupDetail("Temperature", "23", measurementGroupMock);
        //verify(eventingSpy).post(any(DatabaseSettingsSaveRequest.class));
        assertThat(measurementGroupDetail).isInstanceOf(MeasurementGroupDetail.class);
        assertThat(measurementGroupDetail).isNotNull();
    }

    @Test
    public void Should_saveUserCharacteristics_called() throws Exception {
        List list = new ArrayList();
        list.add(consentDetailMock);
        tracker.saveUserCharacteristics(list, dbRequestListener);
        verify(eventingSpy).post(any(UserCharacteristicsSaveRequest.class));
    }

    @Test
    public void Should_unRegisterDBChangeListener_called() throws Exception {
        tracker.unRegisterDBChangeListener();
        DBChangeListener dbChangeListener = DataServicesManager.getInstance().getDbChangeListener();
        assertThat(dbChangeListener).isNull();
    }

    @Test
    public void Should_registerSynchronisationCompleteListener_called() throws Exception {
        tracker.registerSynchronisationCompleteListener(synchronisationCompleteListenerMock);
        assertThat(DataServicesManager.getInstance().mSynchronisationCompleteListener).isInstanceOf(SynchronisationCompleteListener.class);
    }

    @Test
    public void Should_saveMoments_called() throws Exception {
        List list = new ArrayList();
        list.add(momentMock);
        tracker.saveMoments(list, dbRequestListener);
        verify(eventingSpy).post(any(UserCharacteristicsSaveRequest.class));
    }

    @Test
    public void Should_unRegisterSynchronisationCosmpleteListener_called() throws Exception {
        tracker.unRegisterSynchronisationCosmpleteListener();
        assertThat(DataServicesManager.getInstance().mSynchronisationCompleteListener).isNull();
    }

    @Test
    public void Should_fetchInsights_called() throws Exception {
        tracker.fetchInsights(dbFetchRequestListner);
        verify(eventingSpy).post(any(FetchInsightsFromDB.class));
    }

    @Test
    public void Should_deleteInsights_called() throws Exception {
        List list = new ArrayList();
        list.add(insightMock);
        tracker.deleteInsights(list, dbRequestListener);
        verify(eventingSpy).post(any(DeleteInsightFromDB.class));
    }

    @Test
    public void Should_ClearExpiredMoments_called() {
        tracker.clearExpiredMoments(dbRequestListener);
        verify(eventingSpy).post(any(DeleteExpiredMomentRequest.class));
    }

    //Push Notification test
    @Test
    public void unRegisterDeviceTokenTest() throws Exception {
        tracker.unRegisterDeviceToken("token", "variant", null);
        verify(eventingSpy).post(any(UnRegisterDeviceToken.class));
    }

    @Test
    public void registerDeviceTokenTest() throws Exception {
        tracker.registerDeviceToken("token", "variant", "protocol provider", null);
        verify(eventingSpy).post(any(RegisterDeviceToken.class));
    }

    @Test
    public void handlePushNotificationPayloadTest() throws Exception {
        tracker.handlePushNotificationPayload(jsonObject);
    }

    //Subject Profile Test
    @Test
    public void createSubjectProfileTest() throws Exception {
        tracker.createSubjectProfile("test user", "2013-05-05", "female", 78.88, "2015-10-01T12:11:10.123+0100", null);
        verify(eventingSpy).post(any(CreateSubjectProfileRequestEvent.class));
    }

    @Test
    public void getSubjectProfilesTest() throws Exception {
        tracker.getSubjectProfiles(null);
        verify(eventingSpy).post(any(GetSubjectProfileListRequestEvent.class));
    }

    @Test
    public void getSubjectProfileTest() throws Exception {
        tracker.getSubjectProfile("39989890000898989", null);
        verify(eventingSpy).post(any(GetSubjectProfileRequestEvent.class));
    }

    @Test
    public void deleteSubjectProfileTest() throws Exception {
        tracker.deleteSubjectProfile("78798089987868789", null);
        verify(eventingSpy).post(any(DeleteSubjectProfileRequestEvent.class));
    }

    //Device Pairing test
    @Test
    public void pairDevicesTest() throws Exception {
        tracker.pairDevices("77908787878978", "RefNode", null, null, "rxd", null);
        verify(eventingSpy).post(any(PairDevicesRequestEvent.class));
    }

    @Test
    public void unPairDeviceTest() throws Exception {
        tracker.unPairDevice("7867697879787", null);
        verify(eventingSpy).post(any(UnPairDeviceRequestEvent.class));
    }

    @Test
    public void getPairedDevicesTest() throws Exception {
        tracker.getPairedDevices(null);
        verify(eventingSpy).post(any(GetPairedDeviceRequestEvent.class));
    }

    private DSPaginationSpy createPagination() {
        mDSPagination.setOrdering(DSPagination.DSPaginationOrdering.DESCENDING);
        mDSPagination.setPageLimit(1);
        mDSPagination.setPageNumber(1);
        mDSPagination.setOrderBy("timestamp");
        return mDSPagination;
    }


    @Test
    public void pullSyncByDateRange() {
        whenPullSyncIsInvoked();
        thenVerifyMonitorsAreInitialized();
        thenVerifySynchronisationManagerIsCalled();
    }

    private void whenPullSyncIsInvoked() {
        tracker.fetchSync(START_DATE, END_DATE, synchronisationCompleteListenerMock);
    }

    private void thenVerifyMonitorsAreInitialized() {
        verify(coreMock).start();
        verify(synchronisationMonitorMock).start(eventingSpy);
    }


    private void thenVerifySynchronisationManagerIsCalled() {
        verify(synchronisationManagerMock).startFetch(START_DATE.toString(), END_DATE.toString(), synchronisationCompleteListenerMock);
    }

    private static final DateTime START_DATE = new DateTime();
    private static final DateTime END_DATE = new DateTime();

}

