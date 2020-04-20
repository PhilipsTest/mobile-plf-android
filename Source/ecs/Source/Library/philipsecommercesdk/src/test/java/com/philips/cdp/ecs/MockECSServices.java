package com.philips.cdp.ecs;

import androidx.annotation.NonNull;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.ecs.ECSServices;

public class MockECSServices extends ECSServices {

    public MockECSServices(String propositionID, @NonNull AppInfra appInfra) {
        super(propositionID, appInfra);
    }
}
