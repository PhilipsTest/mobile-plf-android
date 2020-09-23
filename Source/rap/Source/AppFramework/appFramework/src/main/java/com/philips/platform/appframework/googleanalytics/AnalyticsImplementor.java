package com.philips.platform.appframework.googleanalytics;

import android.os.Bundle;

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
    public void trackActionWithInfo(String pageName, Map<String, String> paramMap) {
        Bundle bundle = new Bundle();
        FirebaseAnalytics firebaseAnalytics = AppFrameworkApplication.getFirebaseAnalytics();
       /* bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "12345");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "pabitra");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");*/
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            bundle.putString(entry.getKey(),entry.getValue());
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
