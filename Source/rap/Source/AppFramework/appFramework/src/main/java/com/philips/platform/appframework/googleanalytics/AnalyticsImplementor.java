package com.philips.platform.appframework.googleanalytics;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.philips.platform.appinfra.tagging.AnalyticsInterface;
import com.philips.platform.baseapp.base.AppFrameworkApplication;

import java.util.Map;

public class AnalyticsImplementor implements AnalyticsInterface {


    @Override
    public void trackPageWithInfo(String pageName, String key, String value) {
        FirebaseAnalytics firebaseAnalytics = AppFrameworkApplication.getFirebaseAnalytics();
        //firebaseAnalytics.setCurrentScreen(pageName);
    }

    @Override
    public void trackActionWithInfo(String pageName, Map<String, String> paramDict) {

    }
}
