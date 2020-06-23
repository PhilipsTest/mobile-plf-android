/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.launcher;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uappframework.uappinput.UappDependencies;

/**
 * This class is used to provide dependencies for My Account.
 * @since 2018.1.0
 */
public class MyaDependencies extends UappDependencies {

    private static final long serialVersionUID = -8516084955010733888L;

    /**
     * Constructor of MyaDependencies
     * @since 2018.1.0
     * @param appInfra Appinfra instance
     */
    public MyaDependencies(AppInfraInterface appInfra) {
        super(appInfra);
    }

}
