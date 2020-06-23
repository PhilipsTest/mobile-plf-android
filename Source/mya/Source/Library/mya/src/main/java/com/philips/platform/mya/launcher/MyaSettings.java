/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.launcher;

import android.content.Context;

import com.philips.platform.uappframework.uappinput.UappSettings;


/**
 * This class is used to provide input settings for My Account.
 * @since 2018.1.0
 */
public class MyaSettings extends UappSettings {

    private static final long serialVersionUID = 8292744327757443050L;

    /**
     * Constructor for Mya settings
     * @since 2018.1.0
     * @param applicationContext pass the application context
     */
    public MyaSettings(Context applicationContext) {
        super(applicationContext);

    }

}
