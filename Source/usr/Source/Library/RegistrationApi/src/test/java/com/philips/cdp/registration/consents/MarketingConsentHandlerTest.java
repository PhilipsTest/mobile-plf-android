package com.philips.cdp.registration.consents;

import android.content.Context;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.JanrainErrorEnum;
import com.philips.cdp.registration.handlers.RefreshUserHandler;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.internationalization.InternationalizationInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.FetchConsentTypeStateCallback;
import com.philips.platform.pif.chi.PostConsentTypeCallback;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.philips.platform.pif.chi.datamodel.ConsentStatus;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.philips.cdp.registration.consents.URConsentProvider.USR_MARKETING_CONSENT;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MarketingConsentHandlerTest {

    @Captor
    private ArgumentCaptor<RefreshUserHandler> refreshUserHandler;
    @Mock
    private FetchConsentTypeStateCallback givenCheckConsentCallback;
    @Mock
    private PostConsentTypeCallback givenPostConsentCallback;
    @Mock
    private User mockUser;
    @Mock
    private AppInfraInterface appInfraMock;
    @Captor
    private ArgumentCaptor<MarketingConsentHandler.MarketingUpdateCallback> marketingCallbackCaptor;
    @Captor
    private ArgumentCaptor<ConsentStatus> consentGetCaptor;
    @Captor
    private ArgumentCaptor<ConsentError> errorCaptor;

    private MarketingConsentHandler marketingConsentHandler;

    private ConsentDefinition givenConsentDefinition;

    private boolean givenStatus;
    @Mock
    private Context mockContext;
    @Mock
    private InternationalizationInterface internationalizationInterfaceMock;
    private RestInterfaceMock restInterfaceMock = new RestInterfaceMock();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(appInfraMock.getRestClient()).thenReturn(restInterfaceMock);
    }

    @Test
    public void fetchConsent_0() throws Exception {
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenMarketingConsentIsGiven(true);
        //whenCheckingConsent();
        whenRefreshUser();
        thenMarketingConsentIsRetrieved(ConsentStates.active);
    }

    @Test
    public void fetchConsent_1() throws Exception {
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenMarketingConsentIsGiven(true);
        whenRefreshUser();
        thenMarketingConsentIsRetrieved(ConsentStates.active);
    }


    @Test
    public void fetchConsent_2() throws Exception {
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenMarketingConsentIsGiven(false);
        whenRefreshUser();
//        whenCheckingConsents();
        thenMarketingConsentIsRetrieved(ConsentStates.rejected);
    }

    @Test
    public void fetchConsent_3() throws Exception {
        givenConsentDefinitionTypeNotSame();
        givenMarketingConsentIsGiven(true);
        //whenCheckingConsent();
        whenRefreshUser();
        //thenErrorCallbackIsCalled();
    }

    @Test
    public void fetchConsent_itShouldReportErrorWhenCheckingConsentsThrowsAnException() throws Exception {
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenMarketingConsentIsGiven(false);
        whenRefreshUser();
        whenCheckingConsentsThrowsException();
        //  thenErrorCallbackIsCalled();
        thenMarketingConsentIsRetrieved(ConsentStates.rejected);
    }

    @Test
    public void fetchConsent_itShouldReportErrorWhenCheckingConsentsOnFetchConsentThrowsAnException() throws Exception {
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenMarketingConsentIsGiven(false);
        whenRefreshUserOnFetchConsentStates();
        whenCheckingConsentsThrowsException();
    }

    @Test
    public void fetchConsent_itShouldReportErrorWhenCheckingConsentThrowsAnException() throws Exception {
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenMarketingConsentIsGiven(false);
        whenCheckingConsentThrowsException();
        thenErrorCallbackIsCalled();
    }

    @Test
    public void fetchConsent_callsBackWithNoConnectionErrorCode_WhenThereIsNoInternetConnection() throws Exception {
        givenThereIsNoInternetConnection();
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        whenFetchingConsent(USR_MARKETING_CONSENT);
    }

    @Test
    public void postConsent_itShouldGiveMarketingConsent() throws Exception {
        givenPhoneLanguageIs("nl-NL");
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenStatusToPost(true);
        whenPostingConsentDefinitionSucceeds();
        theMarketingConsentIsReportedToCallback(ConsentStates.active);
    }

    @Test
    public void postConsent_itShouldRejectMarketingConsent() throws Exception {
        givenPhoneLanguageIs("nl-NL");
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenStatusToPost(false);
        whenPostingConsentDefinitionSucceeds();
        theMarketingConsentIsReportedToCallback(ConsentStates.rejected);
    }

    @Test
    public void postConsent_itShouldNotGiveMarketingConsentWhenPostingAcceptedOperationFails() throws Exception {
        givenPhoneLanguageIs("nl-NL");
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenStatusToPost(true);
        whenPostingConsentDefinitionFails(501);
        thenPostConsentCallBackIsCalledWith(501);
    }

    @Test
    public void postConsent_itShouldNotGiveMarketingConsentWhenPostRejectedOperationFails() throws Exception {
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenStatusToPost(false);
        whenPostingConsentDefinitionFails(501);
        thenPostConsentCallBackIsCalledWith(501);
    }

    @Test
    public void postConsent_itShouldGiveMarketingConsentWhenRefreshUserFailed() throws Exception {
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenMarketingConsentIsGiven(false);
        whenRefreshUserFailed();
        thenErrorCallbackIsCalled();
    }

    @Test
    public void postConsent_itShouldGiveMarketingConsentsWhenRefreshUserFailed() throws Exception {
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenMarketingConsentIsGiven(false);
        whenRefreshUserFailedOnFetchConsentState();
        thenErrorCallbackIsCalled();
    }

    @Test
    public void postConsent_callsBackWithNoConnectionErrorCode_WhenThereIsNoInternetConnection() throws Exception {
        givenConsentDefinition(USR_MARKETING_CONSENT, 42);
        givenThereIsNoInternetConnection();
        whenPostingConsentDefinition();
        thenPostConsentCallBackIsCalledWith(ConsentError.CONSENT_ERROR_NO_CONNECTION);
    }

    private void givenThereIsNoInternetConnection() {
        restInterfaceMock.isInternetAvailable = false;
    }

    private void givenConsentDefinitionTypeNotSame() {
        givenConsentDefinition = new ConsentDefinition(0, 0, Collections.singletonList("type"), 42);
        marketingConsentHandler = new TestMarketingConsentHandler(mockContext);
    }

    private void whenFetchingConsent(String consentType) {
        marketingConsentHandler.fetchConsentTypeState(consentType, givenCheckConsentCallback);
    }

    private void whenRefreshUser() {
        List<String> types = URConsentProvider.fetchMarketingConsentDefinition().getTypes();
        marketingConsentHandler.fetchConsentTypeState(types.get(types.indexOf(USR_MARKETING_CONSENT)), givenCheckConsentCallback);
        verify(mockUser).refreshUser(refreshUserHandler.capture());
        refreshUserHandler.getValue().onRefreshUserSuccess();
    }

    private void whenRefreshUserOnFetchConsentStates() {
        marketingConsentHandler.fetchConsentTypeState(null, givenCheckConsentCallback);
        verify(mockUser).refreshUser(refreshUserHandler.capture());
        refreshUserHandler.getValue().onRefreshUserSuccess();
    }


    private void whenCheckingConsentThrowsException() {
        when(mockUser.getReceiveMarketingEmail()).thenThrow(new RuntimeException("error offline"));
        MarketingConsentHandler spy = Mockito.spy(marketingConsentHandler);

        spy.refreshUserOrGetMarketingConsent("moment", givenCheckConsentCallback, false);
        verify(spy).getMarketingConsentDefinition("moment", givenCheckConsentCallback);
    }

    private void whenRefreshUserFailed() {
        List<String> types = URConsentProvider.fetchMarketingConsentDefinition().getTypes();
        marketingConsentHandler.fetchConsentTypeState(types.get(types.indexOf(USR_MARKETING_CONSENT)), givenCheckConsentCallback);
        verify(mockUser).refreshUser(refreshUserHandler.capture());
        refreshUserHandler.getValue().onRefreshUserFailed(anyInt());
    }

    private void whenRefreshUserFailedOnFetchConsentState() {
        marketingConsentHandler.fetchConsentTypeState(null, givenCheckConsentCallback);
        verify(mockUser).refreshUser(refreshUserHandler.capture());
        refreshUserHandler.getValue().onRefreshUserFailed(anyInt());
    }


    private void givenConsentDefinition(String consentType, int version) {
        final ArrayList<String> types = new ArrayList<>();
        types.add(consentType);
        givenConsentDefinition = new ConsentDefinition(0, 0, types, version);
        marketingConsentHandler = new TestMarketingConsentHandler(mockContext);

    }

    private void givenStatusToPost(boolean status) {
        givenStatus = status;
    }

    private void givenMarketingConsentIsGiven(boolean given) {
        when(mockUser.getReceiveMarketingEmail()).thenReturn(given);
    }


    private void whenCheckingConsentsThrowsException() {
        when(mockUser.getReceiveMarketingEmail()).thenThrow(new RuntimeException("error offline"));
        marketingConsentHandler.fetchConsentTypeState(null, givenCheckConsentCallback);
    }

    private void whenPostingConsentDefinition() {
        List<String> types = givenConsentDefinition.getTypes();
        marketingConsentHandler.storeConsentTypeState(types.get(types.indexOf(USR_MARKETING_CONSENT)), givenStatus, givenConsentDefinition.getVersion(), givenPostConsentCallback);
    }

    private void whenPostingConsentDefinitionSucceeds() {
        List<String> types = givenConsentDefinition.getTypes();
        marketingConsentHandler.storeConsentTypeState(types.get(types.indexOf(USR_MARKETING_CONSENT)), givenStatus, givenConsentDefinition.getVersion(), givenPostConsentCallback);
        verify(mockUser).updateReceiveMarketingEmail(marketingCallbackCaptor.capture(), eq(givenStatus));
        marketingCallbackCaptor.getValue().onUpdateSuccess();
    }

    private void whenPostingConsentDefinitionFails(int errorCode) {
        List<String> types = givenConsentDefinition.getTypes();
        marketingConsentHandler.storeConsentTypeState(types.get(types.indexOf(USR_MARKETING_CONSENT)), givenStatus, givenConsentDefinition.getVersion(), givenPostConsentCallback);
        verify(mockUser).updateReceiveMarketingEmail(marketingCallbackCaptor.capture(), eq(givenStatus));
        Error mockError = mock(Error.class);
        when(mockError.getErrCode()).thenReturn(errorCode);
        marketingCallbackCaptor.getValue().onUpdateFailedWithError(mockError);
    }

    private void theMarketingConsentIsReportedToCallback(ConsentStates active) {
        verify(givenPostConsentCallback).onPostConsentSuccess();
    }

    private void thenMarketingConsentIsRetrieved(ConsentStates active) {
        verify(givenCheckConsentCallback).onGetConsentsSuccess(consentGetCaptor.capture());

        assertEquals(active, consentGetCaptor.getValue().getConsentState());
    }

    private void thenPostConsentCallBackIsCalledWith(int errorCode) {
        verify(givenPostConsentCallback).onPostConsentFailed(errorCaptor.capture());
        assertEquals(errorCode, errorCaptor.getValue().getErrorCode());
    }

    private void thenFetchConsentCallBackIsCalledWith(int errorCode) {
        verify(givenCheckConsentCallback).onGetConsentsFailed(errorCaptor.capture());
        assertEquals(errorCode, errorCaptor.getValue().getErrorCode());
    }

    private void thenErrorCallbackIsCalled() {
        verify(givenCheckConsentCallback, never()).onGetConsentsSuccess(any(ConsentStatus.class));
        verify(givenCheckConsentCallback).onGetConsentsFailed(any(ConsentError.class));
    }

    private void givenPhoneLanguageIs(String bcp47UILocale) {
        when(internationalizationInterfaceMock.getBCP47UILocale()).thenReturn(bcp47UILocale);
    }

    private class TestMarketingConsentHandler extends MarketingConsentHandler {

        public TestMarketingConsentHandler(Context context) {
            super(appInfraMock, context);
        }

        @Override
        User getUser() {
            return mockUser;
        }
    }

    @Test
    public void shouldReturnDesiredTimeStampString() throws Exception {
        String desiredTimestampString = "2018-07-30 06:29:05 +0000";

        String desiredTimestampStringWithoutTimeZone = "2018-07-30 06:29:05";
        marketingConsentHandler = new TestMarketingConsentHandler(mockContext);

        String[] anyDate = new String[4];
        anyDate[0] = "2018-07-30 06:29:05.78y88u9717      n   eguefgefuveifu+0000";
        anyDate[1] = "2018-07-30 06:29:05 +0000";
        anyDate[2] = "2018-07-30 06:29:05.493 +0000";
        anyDate[3] = "2018-07-30 06:29:05.4933453434534 +0000";
        anyDate[3] = "2018-07-30 06:29:05 4933453434534 +0000";

        for (String date : anyDate) {
            Assert.assertEquals(desiredTimestampString, marketingConsentHandler.getDesiredFormat(date));
        }

        String[] anyDateWithoutTimeZone = new String[4];
        anyDateWithoutTimeZone[0] = "2018-07-30 06:29:05.78y88u9717      n   eguefgefuveifu";
        anyDateWithoutTimeZone[1] = "2018-07-30 06:29:05   ";
        anyDateWithoutTimeZone[2] = "2018-07-30 06:29:05.493 ";
        anyDateWithoutTimeZone[3] = "2018-07-30 06:29:05.4933453434534 ";
        anyDateWithoutTimeZone[3] = "2018-07-30 06:29:05 4933453434534 ";

        for (String date : anyDateWithoutTimeZone) {
            Assert.assertEquals(desiredTimestampStringWithoutTimeZone, marketingConsentHandler.getDesiredFormat(date));
        }

    }

    @Test
    public void shouldReturnDatePassingNullAsString() throws Exception {
        givenConsentDefinitionTypeNotSame();
        Date date = new Date(0);
        Date aNull = marketingConsentHandler.getTimestamp("null");
        assertEquals(aNull, date);
    }

    @Test
    public void shouldReturnDateIsNull() throws Exception {
        givenConsentDefinitionTypeNotSame();
        Date date = new Date(0);
        Date aNull = marketingConsentHandler.getTimestamp(null);
        assertEquals(aNull, date);
    }

    @Test
    public void shouldReturnDateIsEmpty() throws Exception {
        givenConsentDefinitionTypeNotSame();
        Date date = new Date(0);
        Date aNull = marketingConsentHandler.getTimestamp("");
        assertEquals(aNull, date);
    }
}