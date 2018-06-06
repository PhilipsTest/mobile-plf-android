/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.csw;

import android.content.Context;

import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;

import java.util.List;

/**
 * This class is used to provide input parameters and customizations for myaccount.
 */

public class CswLaunchInput extends UappLaunchInput {

    private final Context context;

    private boolean isAddToBackStack;

    private final List<ConsentDefinition> consentDefinitionList;

    public CswLaunchInput(Context context, List<ConsentDefinition> consentDefinitionList) {
        this.context = context;
        this.consentDefinitionList = consentDefinitionList;
    }

    public Context getContext() {
        return context;
    }

    public List<ConsentDefinition> getConsentDefinitionList() {
        return consentDefinitionList;
    }

    /**
     * Get status of is current fragment need to add to backstack or no.
     *
     * @return true if need to add to fragment back stack
     */
    public boolean isAddtoBackStack() {
        return isAddToBackStack;
    }

    /**
     * Enable add to back stack for current fragment.
     *
     * @param isAddToBackStack
     */
    public void addToBackStack(boolean isAddToBackStack) {
        this.isAddToBackStack = isAddToBackStack;
    }
}
