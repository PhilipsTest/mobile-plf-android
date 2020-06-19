/*
 * Copyright (c) Koninklijke Philips N.V. 2019
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */

package com.philips.platform.pim;

import android.content.Context;

import com.philips.platform.uappframework.uappinput.UappSettings;

/**
 * It passes the proposition Application context to PIM component
 */
public class PIMSettings extends UappSettings {

    private static final long serialVersionUID = -7751730940977863505L;

    /**
     * Constructor for Uappsettings
     *
     * @param applicationContext For passing application Context
     * @since 1.0.0
     */
    public PIMSettings(Context applicationContext) {
        super(applicationContext);
    }
}
