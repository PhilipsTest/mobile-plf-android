/* Copyright (c) Koninklijke Philips N.V., 2018
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.appinfra.consentmanager;

import com.philips.platform.pif.chi.ConsentHandlerInterface;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;

import java.util.List;

public interface ConsentManagerInterface {

    /**
     * Register consent type to the given handler
     *
     * @param consentType             type of the consent
     * @param consentHandlerInterface Handler which handles the implementation for the given consent type
     * @since 2018.1.0
     */
    void registerHandler(List<String> consentType, ConsentHandlerInterface consentHandlerInterface);

    /**
     * Deregister consent type from the handler
     *
     * @param consentType the type that should be removed from the handler
     * @since 2018.1.0
     */
    void deregisterHandler(List<String> consentType);

    /**
     * Register Consent Definition to the type
     *
     * @param consentDefinitions given list of consent definitions
     * @since 2018.1.0
     */
    void registerConsentDefinitions(List<ConsentDefinition> consentDefinitions);

    /**
     * Fetch the consent status of the given consent definition by delegating to the corresponding handler
     *
     * @param consentDefinition Consent Definition for which the status has to be fetched
     * @param callback          The callback that should be invoked after fetch
     * @since 2018.1.0
     */
    void fetchConsentState(ConsentDefinition consentDefinition, final FetchConsentCallback callback);

    /**
     * Fetch the consents status of the given consent definition by delegating to the corresponding handler
     *
     * @param consentDefinitions Consent Definition for which the status has to be fetched
     * @param callback           The callback that should be invoked after fetch
     * @since 2018.1.0
     */
    void fetchConsentStates(List<ConsentDefinition> consentDefinitions, final FetchConsentsCallback callback);

    /**
     * Store the consents status of the given consent definition by delegating to the corresponding handler
     *
     * @param consentDefinition Consent Definition for which the status has to be stored
     * @param status            Consent status i.e, active, rejected or Inactive
     * @param callback          The callback that should be invoked after store
     * @since 2018.1.0
     */
    void storeConsentState(final ConsentDefinition consentDefinition, boolean status, PostConsentCallback callback);

    /**
     * Fetch the consent status of the give type by delegating to the corresponding handler
     * @param type
     * @param callback
     * @since 2018.1.0
     */
    void fetchConsentTypeState(String type, FetchConsentCallback callback);

    /**
     * Get consent definition for consent type
     * @param consentType
     * @since 2018.2.0
     */
    ConsentDefinition getConsentDefinitionForType(String consentType);
}
