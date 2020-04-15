/*
 * Copyright (c) Koninklijke Philips N.V. 2019
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */

package com.philips.platform.pim;

import androidx.annotation.NonNull;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uappframework.uappinput.UappDependencies;

/**
 * PIMDependancies handles the dependency required for PIM. Now, UR has one dependency i.e AppInfra.
 * So vertical needs to initialize IAPDependencies and set the app infra object. This app infra object will
 * be responsible for logging, tagging and some configuration.
 *
 */
public class PIMDependencies extends UappDependencies {
    /**
     * Constructor for UappDependencies.
     *
     * @param appInfra Requires appInfraInterface object
     *
     * @since TODO: App version
     */
    public PIMDependencies(@NonNull AppInfraInterface appInfra) {
        super(appInfra);
    }
}
