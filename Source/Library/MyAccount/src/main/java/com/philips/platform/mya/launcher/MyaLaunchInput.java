/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.launcher;

import android.content.Context;

import com.philips.platform.mya.ConsentDefinition;
import com.philips.platform.mya.interfaces.MyaListener;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;

import java.util.List;

/**
 * This class is used to provide input parameters and customizations for myaccount.
 */

public class MyaLaunchInput extends UappLaunchInput {

    private Context context;
    private List<ConsentDefinition> consentDefinition;
    private boolean isAddToBackStack;


    private MyaListener myaListener;

    public MyaLaunchInput(Context context, MyaListener myaListener) {
        this.context = context;
        this.myaListener = myaListener;
    }


    public List<ConsentDefinition> getConsentDefinition() {
        return consentDefinition;
    }

    public void setConsentDefinition(List<ConsentDefinition> consentDefinition) {
        this.consentDefinition = consentDefinition;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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
     * Enable  add to back stack for current fragment.
     *
     * @param isAddToBackStack
     */
    public void addToBackStack(boolean isAddToBackStack) {
        this.isAddToBackStack = isAddToBackStack;
    }

    public MyaListener getMyaListener() {
        return myaListener;
    }

}
