/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.analytics;

import android.app.Activity;

import com.philips.cdp.di.iap.integration.IAPDependencies;
import com.philips.platform.appinfra.BuildConfig;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;

import java.util.HashMap;
import java.util.Map;

public class IAPAnalytics {
    public static AppTaggingInterface sAppTaggingInterface;

    public static void initIAPAnalytics(IAPDependencies dependencies) {
        sAppTaggingInterface =
                dependencies.getAppInfra().getTagging().
                        createInstanceForComponent(IAPAnalyticsConstant.COMPONENT_NAME, BuildConfig.VERSION_NAME);
    }

    public static void trackPage(String currentPage) {
        if (sAppTaggingInterface != null) {
            Map<String, String> map = new HashMap<>();
            sAppTaggingInterface.trackPageWithInfo(currentPage, map);
        }
    }

    public static void trackAction(String state, String key, Object value) {
        String valueObject = (String) value;
        if (sAppTaggingInterface != null)
            sAppTaggingInterface.
                    trackActionWithInfo(state, key, valueObject);
    }

    public static void trackMultipleActions(String state, Map<String, String> map) {
        if (sAppTaggingInterface != null)
            sAppTaggingInterface.
                    trackActionWithInfo(state, map);
    }

    public static void pauseCollectingLifecycleData() {
        if (sAppTaggingInterface != null)
            sAppTaggingInterface.pauseLifecycleInfo();
    }

    public static void collectLifecycleData(Activity activity) {
        if (sAppTaggingInterface != null)
            sAppTaggingInterface.collectLifecycleInfo(activity);
    }

    public static void clearAppTaggingInterface() {
        sAppTaggingInterface = null;
    }
}
