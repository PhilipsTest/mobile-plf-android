package com.philips.platform.appframework.googleanalytics;

/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

/**
 * The Wrapper class for AppTagging.
 */
class AnalyticsWrapper extends AnalyticsImplementor {


    private final String mComponentID;

    public String getmComponentID() {
        return mComponentID;
    }

    public String getmComponentVersion() {
        return mComponentVersion;
    }

    private final String mComponentVersion;

    AnalyticsWrapper(String componentId, String componentVersion) {
        mComponentID = componentId;
        mComponentVersion = componentVersion;
    }
}