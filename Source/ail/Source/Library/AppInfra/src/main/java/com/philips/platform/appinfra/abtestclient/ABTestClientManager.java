/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.abtestclient;

import android.os.Bundle;
import androidx.annotation.NonNull;

/**
 * The ABTest Client Manager is a dummy implementation provided from platform side
 */

public class ABTestClientManager implements ABTestClientInterface {


    private static final long serialVersionUID = 4871907868231020300L;

    @Override
    public CACHESTATUS getCacheStatus() {
        return CACHESTATUS.EXPERIENCE_NOT_UPDATED;
    }

    @Override
    public String getTestValue(@NonNull String requestNameKey, @NonNull String defaultValue, UPDATETYPE updateType) {
        return defaultValue;
    }

    @Override
    public void updateCache(OnRefreshListener listener) {

    }

    @Override
    public void enableDeveloperMode(boolean state) {
    }

    @Override
    public String getAbTestingConsentIdentifier() {
        return "";
    }

    @Override
    public void tagEvent(String eventName, Bundle params) {
    }
}
