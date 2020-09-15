package com.philips.platform.appframework.flowmanager;

import com.philips.platform.appinfra.tagging.AnalyticsInterface;

import java.util.Map;

class AnalyticsImplementor implements AnalyticsInterface {

    @Override
    public AnalyticsInterface createInstanceForComponent(String componentId, String componentVersion) {
        return null;
    }

    @Override
    public void trackPageWithInfo(String pageName, String key, String value) {

    }

    @Override
    public void trackPageWithInfo(String pageName, Map<String, String> paramDict) {

    }

    @Override
    public void trackActionWithInfo(String pageName, String key, String value) {

    }

    @Override
    public void trackActionWithInfo(String pageName, Map<String, String> paramDict) {

    }
}
