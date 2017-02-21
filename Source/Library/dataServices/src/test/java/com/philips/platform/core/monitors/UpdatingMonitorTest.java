package com.philips.platform.core.monitors;

import com.philips.platform.core.Eventing;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.Settings;
import com.philips.platform.core.dbinterfaces.DBDeletingInterface;
import com.philips.platform.core.dbinterfaces.DBFetchingInterface;
import com.philips.platform.core.dbinterfaces.DBSavingInterface;
import com.philips.platform.core.dbinterfaces.DBUpdatingInterface;
import com.philips.platform.core.events.BackendDataRequestFailed;
import com.philips.platform.core.events.BackendMomentListSaveRequest;
import com.philips.platform.core.events.BackendResponse;
import com.philips.platform.core.events.ConsentBackendSaveResponse;
import com.philips.platform.core.events.DatabaseConsentUpdateRequest;
import com.philips.platform.core.events.DatabaseSettingsUpdateRequest;
import com.philips.platform.core.events.MomentDataSenderCreatedRequest;
import com.philips.platform.core.events.MomentUpdateRequest;
import com.philips.platform.core.events.ReadDataFromBackendResponse;
import com.philips.platform.core.injection.AppComponent;
import com.philips.platform.core.listeners.DBChangeListener;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.datasync.moments.MomentsSegregator;
import com.philips.testing.verticals.datatyes.MomentType;
import com.philips.testing.verticals.table.OrmMoment;
import com.philips.testing.verticals.table.OrmMomentType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sangamesh on 01/12/16.
 */
public class UpdatingMonitorTest {

    @Mock
    DBUpdatingInterface dbUpdatingInterface;


    @Mock
    DBSavingInterface dbSavingInterface;

    @Mock
    DBDeletingInterface dbDeletingInterface;

    @Mock
    MomentUpdateRequest momentUpdateRequestmock;

    @Mock
    DBRequestListener dbRequestListener;

    @Mock
    DatabaseConsentUpdateRequest consentUpdateRequestmock;

    @Mock
    ReadDataFromBackendResponse readDataFromBackendResponseMock;

    @Mock
    BackendMomentListSaveRequest backendMomentListSaveRequestMock;

    @Mock
    MomentDataSenderCreatedRequest momentDataSenderCreatedRequestMock;

    @Mock
    ConsentBackendSaveResponse consentBackendSaveResponseMock;
    @Mock
    Moment momentMock;

    @Mock
    Settings settingsMock;
    @Mock
    DBFetchingInterface dbFetchingInterface;

    UpdatingMonitor updatingMonitor;
    @Mock
    BackendResponse backendResponseMock;
    @Mock
    BackendDataRequestFailed backendDataRequestFailedMock;
    @Mock
    private Eventing eventingMock;
    @Mock
    MomentsSegregator momentsSegregatorMock;

    @Mock
    private AppComponent appComponantMock;

    @Mock
    private DBChangeListener dbChangeListener;

    @Mock
    private DatabaseSettingsUpdateRequest databaseSettingsUpdateRequestMock;

    @Before
    public void setUp() {
        initMocks(this);
        DataServicesManager.getInstance().setAppComponant(appComponantMock);
        updatingMonitor = new UpdatingMonitor(dbUpdatingInterface, dbDeletingInterface, dbFetchingInterface);
        updatingMonitor.momentsSegregator = momentsSegregatorMock;
        updatingMonitor.start(eventingMock);
    }

    @Test
    public void shouldDeleteUpdateAndPostMoment_whenMomentUpdateRequestIsCalled() throws Exception {
       // when(dbUpdatingInterface.getOrmMoment(momentMock)).thenReturn(momentMock);
        when(momentUpdateRequestmock.getMoment()).thenReturn(momentMock);
        updatingMonitor.onEventAsync(momentUpdateRequestmock);
        verify(momentMock).setSynced(false);
        verify(dbUpdatingInterface).updateMoment(momentMock,momentUpdateRequestmock.getDbRequestListener());
    }

    @Test
    public void shouldUpdateSettings_whenDatabaseSettingsUpdateRequestIsCalled() throws Exception {
        when(databaseSettingsUpdateRequestMock.getSettings()).thenReturn(settingsMock);
        updatingMonitor.onEventAsync(databaseSettingsUpdateRequestMock);
    }


    /*@Test
    public void shouldDeleteUpdateAndPostMoment_whenonEventBackgroundThreadIsCalled() throws Exception {

        when(dbUpdatingInterface.getOrmMoment(momentMock)).thenReturn(momentMock);
        updatingMonitor.onEventBackgroundThread(backendResponseMock);
        verify(dbUpdatingInterface).postRetrofitError(backendResponseMock.getCallException());
    }

    @Test
    public void shouldonEventBackgroundThreadMoment_whenonEventBackgroundThreadIsCalled() throws Exception {

        when(dbUpdatingInterface.getOrmMoment(momentMock)).thenReturn(momentMock);
        updatingMonitor.onEventBackgroundThread(backendMomentRequestFailedMock);
        verify(dbUpdatingInterface).updateFailed(backendMomentRequestFailedMock.getException());
    }*/

    @Test
    public void shouldonEventBackgroundThreadMoment_whenonEventBackgroundThreadWhenReadDataFromBackendResponsePassed() throws Exception {
        updatingMonitor.onEventAsync(readDataFromBackendResponseMock);
        verify(dbFetchingInterface).fetchMoments(readDataFromBackendResponseMock.getDbFetchRequestListner());
    }

    @Test
    public void shouldonEventBackgroundThreadMoment_whenonEventBackgroundThreadWhenBackendMomentListSaveRequestPassed() throws Exception {
        Moment moment1 = new OrmMoment(null, null, new OrmMomentType(-1,MomentType.TEMPERATURE));
        updatingMonitor.onEventAsync(new BackendMomentListSaveRequest(Arrays.asList(moment1), dbChangeListener));
        verify(momentsSegregatorMock).processMomentsReceivedFromBackend(Arrays.asList(moment1),null);
    }

    @Test
    public void shouldonEventBackgroundThreadMoment_whenonEventBackgroundThreadWhenMomentDataSenderCreatedRequestPassed() throws Exception {
        Moment moment1 = new OrmMoment(null, null, new OrmMomentType(-1,MomentType.TEMPERATURE));
        updatingMonitor.onEventAsync(new MomentDataSenderCreatedRequest(Arrays.asList(moment1), dbChangeListener));
      //  List<? extends Moment> moments = momentDataSenderCreatedRequestMock.getList();
         verify(momentsSegregatorMock).processCreatedMoment(Arrays.asList(moment1),null);
    }

}