package com.philips.platform.appframework.googleanalytics;

import com.philips.platform.appinfra.tagging.AnalyticsInterface;

import java.util.Map;

public class AnalyticsImplementor implements AnalyticsInterface {

    @Override
    public AnalyticsInterface createInstanceForComponent(String componentId, String componentVersion) {
         return new AnalyticsWrapper(componentId, componentVersion);
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
