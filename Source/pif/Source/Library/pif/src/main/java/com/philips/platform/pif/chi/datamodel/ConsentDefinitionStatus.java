package com.philips.platform.pif.chi.datamodel;

public class ConsentDefinitionStatus {
    private ConsentStates consentState;
    private ConsentVersionStates consentVersionState;
    private ConsentDefinition consentDefinition;

    public ConsentDefinitionStatus() {
    }

    public ConsentDefinitionStatus(ConsentStates consentState, ConsentVersionStates consentVersionState, ConsentDefinition consentDefinition) {
        this.consentState = consentState;
        this.consentVersionState = consentVersionState;
        this.consentDefinition = consentDefinition;
    }

    public ConsentStates getConsentState() {
        return consentState;
    }

    public void setConsentState(ConsentStates consentState) {
        this.consentState = consentState;
    }

    public ConsentVersionStates getConsentVersionState() {
        return consentVersionState;
    }

    public void setConsentVersionState(ConsentVersionStates consentVersionState) {
        this.consentVersionState = consentVersionState;
    }

    public ConsentDefinition getConsentDefinition() {
        return consentDefinition;
    }

    public void setConsentDefinition(ConsentDefinition consentDefinition) {
        this.consentDefinition = consentDefinition;
    }
}
