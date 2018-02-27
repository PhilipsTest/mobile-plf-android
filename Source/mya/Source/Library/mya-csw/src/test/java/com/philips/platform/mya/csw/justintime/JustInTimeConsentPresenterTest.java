package com.philips.platform.mya.csw.justintime;

import com.philips.platform.mya.csw.R;
import com.philips.platform.mya.csw.justintime.spy.ConsentHandlerInterfaceSpy;
import com.philips.platform.mya.csw.justintime.spy.JustInTimeWidgetHandlerSpy;
import com.philips.platform.mya.csw.justintime.spy.ViewSpy;
import com.philips.platform.mya.csw.mock.AppInfraInterfaceMock;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.datamodel.BackendConsent;
import com.philips.platform.pif.chi.datamodel.Consent;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.pif.chi.datamodel.ConsentStatus;

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
    private ConsentHandlerInterfaceSpy consentHandlerInterface;
    private ConsentDefinition consentDefinition;
    private JustInTimeWidgetHandlerSpy completionListener;
    private BackendConsent backendConsent;
    private Consent consent;
    private ConsentError consentError;

    @Before
    public void setup() {
        appInfraMock = new AppInfraInterfaceMock();
        view = new ViewSpy();
        consentHandlerInterface = new ConsentHandlerInterfaceSpy();
        consentDefinition = new ConsentDefinition("", "", Collections.EMPTY_LIST, 0);
        backendConsent = new BackendConsent("", ConsentStatus.active, "", 0);
        consent = new Consent(backendConsent, consentDefinition);
        consentError = new ConsentError("", 0);
        completionListener = new JustInTimeWidgetHandlerSpy();
        presenter = new JustInTimeConsentPresenter(view, appInfraMock, consentHandlerInterface, consentDefinition, completionListener);
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
        whenPostIsSuccessful();
        thenCompletionHandlerIsCalledOnConsentGiven();
    }

    @Test
    public void onConsentGivenCallsCompletionListenerOnSuccessWhenPostIsNotSuccessful() {
        givenUserIsOnline();
        whenGivingConsent();
        whenPostIsNotSuccessful();
        thenCompletionHandlerIsNotCalledOnConsentGiven();
    }

    @Test
    public void onConsentGivenHidesProgressDialogWhenPostIsSuccessful() {
        givenUserIsOnline();
        whenGivingConsent();
        whenPostIsSuccessful();
        thenProgressDialogIsHidden();
    }

    @Test
    public void onConsentGivenHidesProgressDialogWhenPostIsNotSuccessful() {
        givenUserIsOnline();
        whenGivingConsent();
        whenPostIsNotSuccessful();
        thenProgressDialogIsHidden();
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

    private void whenPostIsSuccessful() {
        consentHandlerInterface.callsCallback_onPostConsentSuccess(consent);
    }

    private void whenPostIsNotSuccessful() {
        consentHandlerInterface.callsCallback_onPostConsentFailed(consentDefinition, consentError);
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
        assertEquals(definition, consentHandlerInterface.definition_storeConsentState);
        assertEquals(active, consentHandlerInterface.status_storeConsentState);
        assertNotNull(consentHandlerInterface.callback_storeConsentState);
    }

    private void thenCompletionHandlerIsCalledOnConsentGiven() {
        assertTrue(completionListener.consentGiven);
    }

    private void thenCompletionHandlerIsNotCalledOnConsentGiven() {
        assertFalse(completionListener.consentGiven);
    }

}