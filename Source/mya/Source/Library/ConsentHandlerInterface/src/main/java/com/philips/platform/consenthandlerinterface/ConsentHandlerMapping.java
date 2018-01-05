/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.consenthandlerinterface;

import com.philips.platform.consenthandlerinterface.datamodel.ConsentDefinition;

import java.util.List;

public class ConsentHandlerMapping {

    private final List<ConsentDefinition> consentDefinitionList;
    private final ConsentHandlerInterface consentHandlerInterface;

    public ConsentHandlerMapping(List<ConsentDefinition> consentDefinitionList, ConsentHandlerInterface consentHandlerInterface) {
        this.consentDefinitionList = consentDefinitionList;
        this.consentHandlerInterface = consentHandlerInterface;
    }

    public List<ConsentDefinition> getConsentDefinitionList() {
        return consentDefinitionList;
    }

    public ConsentHandlerInterface getHandlerInterface() {
        return consentHandlerInterface;
    }
}
