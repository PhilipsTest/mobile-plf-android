/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.uappframework.uappinput;

import com.philips.platform.appinfra.AppInfraInterface;

import java.io.Serializable;

/**
 * This class needs to be extended for defining the input Type prerequisites for launching micro app.
 *
 * @since 1.0.0
 */
public class UappDependencies implements Serializable {

    private static final long serialVersionUID = -4372956863316605918L;
    /**
     * Instance of AppInfraInterface
     */
    protected AppInfraInterface mAppInfraInterface;

    /**
     * Constructor for UappDependencies.
     *
     * @param appInfra Requires appInfraInterface object
     * @since 1.0.0
     */
    public UappDependencies(AppInfraInterface appInfra) {
        this.mAppInfraInterface = appInfra;
    }

    /**
     * To retrieve AppInfra Interface object.
     *
     * @return AppInfraInterface Object
     * @since 1.0.0
     */
    public AppInfraInterface getAppInfra() {
        return mAppInfraInterface;
    }


}
