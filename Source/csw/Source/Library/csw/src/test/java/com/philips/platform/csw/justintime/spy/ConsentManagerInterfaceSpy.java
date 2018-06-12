package com.philips.platform.csw.justintime.spy;

import com.philips.platform.appinfra.consentmanager.ConsentManagerInterface;
import com.philips.platform.appinfra.consentmanager.FetchConsentCallback;
import com.philips.platform.appinfra.consentmanager.FetchConsentsCallback;
import com.philips.platform.appinfra.consentmanager.PostConsentCallback;
import com.philips.platform.catk.datamodel.ConsentDTO;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.ConsentHandlerInterface;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;

import java.util.List;

public class ConsentManagerInterfaceSpy implements ConsentManagerInterface {

    public ConsentDefinition definition_storeConsentState;
    public boolean status_storeConsentState;
    public PostConsentCallback callback_storeConsentState;
    private ConsentDefinition definition;
    private ConsentError error;
    private ConsentDTO consent;

    public void callsCallback_onPostConsentFailed(ConsentDefinition definition, ConsentError error) {
        this.definition = definition;
        this.error = error;
    }

    public void callsCallback_onPostConsentSuccess(ConsentDTO consent) {
        this.consent = consent;
    }

    private boolean shouldSucceed() {
        return consent != null;
    }

    private boolean shouldFail() {
        return definition != null && error != null;
    }


    @Override
    public void registerHandler(List<String> consentType, ConsentHandlerInterface consentHandlerInterface) {

    }

    @Override
    public void deregisterHandler(List<String> consentType) {

    }

    @Override
    public void registerConsentDefinitions(List<ConsentDefinition> consentDefinitions) {

    }

    @Override
    public void fetchConsentState(ConsentDefinition consentDefinition, FetchConsentCallback callback) {

    }

    @Override
    public void fetchConsentStates(List<ConsentDefinition> consentDefinitions, FetchConsentsCallback callback) {

    }

    @Override
    public void storeConsentState(ConsentDefinition consentDefinition, boolean status, PostConsentCallback callback) {
        this.definition_storeConsentState = consentDefinition;
        this.status_storeConsentState = status;
        this.callback_storeConsentState = callback;
        if (shouldSucceed()) {
            this.callback_storeConsentState.onPostConsentSuccess();
        } else if (shouldFail()) {
            this.callback_storeConsentState.onPostConsentFailed(error);
        }
    }

    @Override
    public void fetchConsentTypeState(String type, FetchConsentCallback callback) {

    }

    @Override
    public ConsentDefinition getConsentDefinitionForType(String consentType) {
        return null;
    }
}
