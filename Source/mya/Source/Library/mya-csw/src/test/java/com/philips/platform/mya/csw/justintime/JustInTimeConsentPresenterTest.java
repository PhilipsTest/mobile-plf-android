package com.philips.platform.mya.csw.justintime;

import com.philips.platform.mya.catk.datamodel.ConsentDTO;
import com.philips.platform.mya.csw.R;
import com.philips.platform.mya.csw.justintime.spy.ConsentManagerInterfaceSpy;
import com.philips.platform.mya.csw.justintime.spy.JustInTimeWidgetHandlerSpy;
import com.philips.platform.mya.csw.justintime.spy.ViewSpy;
import com.philips.platform.mya.csw.mock.AppInfraInterfaceMock;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.pif.chi.datamodel.ConsentStates;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JustInTimeConsentPresenterTest {

    private JustInTimeConsentPresenter presenter;
    private ViewSpy view;
    private AppInfraInterfaceMock appInfraMock;
    private ConsentManagerInterfaceSpy consentManagerInterface;
    private ConsentDefinition consentDefinition;
    private JustInTimeWidgetHandlerSpy completionListener;
    private ConsentDTO consentDTO;
    private ConsentError consentError;

    @Before
    public void setup() {
        appInfraMock = new AppInfraInterfaceMock();
        view = new ViewSpy();
        consentManagerInterface = new ConsentManagerInterfaceSpy();
        appInfraMock.consentManagerInterface = consentManagerInterface;
        consentDefinition = new ConsentDefinition(0, 0, Collections.EMPTY_LIST, 0);
        consentDTO = new ConsentDTO("", ConsentStates.active, "", 0);
        consentError = new ConsentError("", 1234);
        completionListener = new JustInTimeWidgetHandlerSpy();
        presenter = new JustInTimeConsentPresenter(view, appInfraMock, consentDefinition, completionListener);
    }

    @Test
    public void setsPresenterOnView() {
        assertEquals(presenter, view.presenter);
    }

    @Test
    public void onConsentGivenShowsErrorWhenOffline() {
        givenUserIsOffline();
        whenGivingConsent();
        thenErrorDialogIsShown(R.string.csw_offline_title, R.string.csw_offline_message);
    }

    @Test
    public void onConsentGivenShowsProgressDialogWhenOnline() {
        givenUserIsOnline();
        whenGivingConsent();
        thenProgressDialogIsShown();
    }

    @Test
    public void onConsentGivenStoresActiveConsentStateWhenOnline() {
        givenUserIsOnline();
        whenGivingConsent();
        thenConsentStateIsStored(consentDefinition, true);
    }

    @Test
    public void onConsentRejectedStoresRejectedConsentStateWhenOnline() {
        givenUserIsOnline();
        whenRejectingConsent();
        thenConsentStateIsStored(consentDefinition, false);
    }

    @Test
    public void onConsentGivenCallsCompletionListenerOnSuccessWhenPostIsSuccessful() {
        givenUserIsOnline();
        whenGivingConsent();
        thenCompletionHandlerIsCalledOnConsentGiven();
    }

    @Test
    public void onConsentGivenCallsCompletionListenerOnSuccessWhenPostIsNotSuccessful() {
        givenUserIsOnline();
        givenPostFails();
        whenGivingConsent();
        thenCompletionHandlerIsNotCalledOnConsentGiven();
    }

    @Test
    public void onConsentGivenHidesProgressDialogWhenPostIsSuccessful() {
        givenUserIsOnline();
        whenGivingConsent();
        thenProgressDialogIsHidden();
    }

    @Test
    public void onConsentGivenHidesProgressDialogWhenPostIsNotSuccessful() {
        givenUserIsOnline();
        givenPostFails();
        whenGivingConsent();
        thenProgressDialogIsHidden();
    }

    @Test
    public void onConsentRejectedCallsCompletionHandlerOnFailureWhenPostIsSuccessful() {
        givenUserIsOnline();
        whenRejectingConsent();
        thenCompletionHandlerIsCalledOnConsentRejected();
    }

    @Test
    public void onConsentRejectedCallsCompletionListenerOnSuccessWhenPostIsNotSuccessful() {
        givenUserIsOnline();
        givenPostFails();
        whenRejectingConsent();
        thenCompletionHandlerIsNotCalledOnConsentRejected();
    }

    @Test
    public void onConsentRejectedHidesProgressDialogWhenPostIsSuccessful() {
        givenUserIsOnline();
        whenRejectingConsent();
        thenProgressDialogIsHidden();
    }

    @Test
    public void onConsentRejectedHidesProgressDialogWhenPostIsNotSuccessful() {
        givenUserIsOnline();
        givenPostFails();
        whenRejectingConsent();
        thenProgressDialogIsHidden();
    }

    @Test
    public void onConsentRejectedShowsErrorDialogWhenPostIsNotSuccessful() {
        givenUserIsOnline();
        givenPostFails();
        whenRejectingConsent();
        thenShowsErrorDialog();
    }

    private void givenUserIsOffline() {
        appInfraMock.restInterfaceMock.isInternetAvailable = false;
    }

    private void givenUserIsOnline() {
        appInfraMock.restInterfaceMock.isInternetAvailable = true;
    }

    private void whenGivingConsent() {
        presenter.onConsentGivenButtonClicked();
    }

    private void whenRejectingConsent() {
        presenter.onConsentRejectedButtonClicked();
    }

    private void givenPostFails() {
        consentManagerInterface.callsCallback_onPostConsentFailed(consentDefinition, consentError);
    }

    private void thenErrorDialogIsShown(int expectedTitle, int expectedMessage) {
        assertEquals(expectedTitle, view.errorTitleId_showErrorDialog);
        assertEquals(expectedMessage, view.errorMessageId_showErrorDialog);
    }

    private void thenProgressDialogIsShown() {
        assertTrue(view.progressDialogShown);
    }

    private void thenProgressDialogIsHidden() {
        assertTrue(view.progressDialogHidden);
    }

    private void thenConsentStateIsStored(ConsentDefinition definition, boolean active) {
        assertEquals(definition, consentManagerInterface.definition_storeConsentState);
        assertEquals(active, consentManagerInterface.status_storeConsentState);
        assertNotNull(consentManagerInterface.callback_storeConsentState);
    }

    private void thenCompletionHandlerIsCalledOnConsentGiven() {
        assertTrue(completionListener.consentGiven);
    }

    private void thenCompletionHandlerIsNotCalledOnConsentGiven() {
        assertFalse(completionListener.consentGiven);
    }

    private void thenCompletionHandlerIsCalledOnConsentRejected() {
        assertTrue(completionListener.consentRejected);
    }

    private void thenCompletionHandlerIsNotCalledOnConsentRejected() {
        assertFalse(completionListener.consentRejected);
    }

    private void thenShowsErrorDialog() {
        assertEquals(R.string.csw_problem_occurred_error_title, view.errorTileId_showErrorDialogForCode);
        assertEquals(consentError.getErrorCode(), view.errorCode_showErrorDialogForCode);
    }
}