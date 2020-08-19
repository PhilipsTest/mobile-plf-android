package com.philips.platform.ecs;

import androidx.annotation.NonNull;

import com.philips.platform.appinfra.AppInfra;

public class MockECSServices extends ECSServices {

    public MockECSServices(@NonNull AppInfra appInfra) {
        super(appInfra);
    }
}
