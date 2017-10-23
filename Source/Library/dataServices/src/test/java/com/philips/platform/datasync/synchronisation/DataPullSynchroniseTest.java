package com.philips.platform.datasync.synchronisation;

import com.philips.platform.core.injection.AppComponent;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.datasync.spy.EventingSpy;
import com.philips.platform.datasync.spy.UserAccessProviderSpy;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import retrofit.RetrofitError;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class DataPullSynchroniseTest {
    private static final int EVENT_ID = 2344;

    private static final DateTime NOW = DateTime.now(DateTimeZone.UTC);

    private UserAccessProviderSpy userAccessProviderSpy;

    private EventingSpy eventingSpy;

    private DataPullSynchronise synchronise;

    @Mock
    private DataFetcher secondFetcherMock;

    @Mock
    private DataFetcher firstFetcherMock;

    @Mock
    SynchronisationManager synchronisationManagerMock;

    @Mock
    private AppComponent appComponentMock;


    @Before
    public void setUp() {
        initMocks(this);

        userAccessProviderSpy = new UserAccessProviderSpy();
        eventingSpy = new EventingSpy();

        DataServicesManager.getInstance().setAppComponant(appComponentMock);

        Set<String> set = new HashSet<>();
        set.add("moment");
        set.add("Settings");
        set.add("characteristics");
        set.add("consent");
        set.add("insight");
        DataServicesManager.getInstance().configureSyncDataType(set);

        synchronise = new DataPullSynchronise(
                Arrays.asList(firstFetcherMock, secondFetcherMock)
        );

        synchronise.userAccessProvider = userAccessProviderSpy;
        synchronise.eventing = eventingSpy;
        synchronise.synchronisationManager = synchronisationManagerMock;
    }

    @Test
    public void postErrorWhenUserIsNotLoggedIn() {
        givenUserIsNotLoggedIn();
        whenSynchronisationIsStarted(EVENT_ID);
        thenAnErrorWasPostedWithReferenceId(EVENT_ID);
    }

    @Test
    public void postSyncCompleteWhenNoFetchers() {
        givenUserIsLoggedIn();
        givenNoFetchers();
        whenSynchronisationIsStarted(EVENT_ID);
        Mockito.verify(synchronisationManagerMock).dataSyncComplete();
    }

    @Test
    public void postOkWhenDataPullSuccess() {
        givenUserIsLoggedIn();
        givenFetcherList();
        whenSynchronisationIsStarted(EVENT_ID);
        Mockito.verify(synchronisationManagerMock).dataPullSuccess();
    }

    @Test
    public void postErrorWhenDataPullFails() {
        givenUserIsLoggedIn();
        givenFetcherList();
        RetrofitError error = mock(RetrofitError.class);
        Mockito.when(secondFetcherMock.fetchDataSince(NOW)).thenReturn(error);
        whenSynchronisationIsStarted(EVENT_ID);
        Mockito.verify(synchronisationManagerMock).dataPullFail(error);
        thenAnErrorWasPostedWithReferenceId(EVENT_ID);
    }

    private void givenUserIsLoggedIn() {
        userAccessProviderSpy.isLoggedIn = true;
    }

    private void givenUserIsNotLoggedIn() {
        userAccessProviderSpy.isLoggedIn = false;
    }


    private void whenSynchronisationIsStarted(final int eventId) {
        synchronise.startSynchronise(NOW, eventId);
    }

    private void thenAnErrorWasPostedWithReferenceId(final int expectedEventId) {
        assertEquals(expectedEventId, eventingSpy.postedEvent.getReferenceId());
    }

    private void givenFetcherList(){
        ArrayList<DataFetcher> fetcherArrayList = new ArrayList<>();
        fetcherArrayList.add(firstFetcherMock);
        fetcherArrayList.add(secondFetcherMock);
        synchronise.fetchers = fetcherArrayList;
        synchronise.configurableFetchers = fetcherArrayList;
    }

    private void givenNoFetchers(){
        ArrayList<DataFetcher> fetcherArrayList = new ArrayList<>();
        synchronise.fetchers = fetcherArrayList;
        synchronise.configurableFetchers = fetcherArrayList;
    }
}