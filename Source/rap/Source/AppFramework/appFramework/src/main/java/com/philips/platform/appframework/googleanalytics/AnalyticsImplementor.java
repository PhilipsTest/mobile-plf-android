package com.philips.platform.appframework.googleanalytics;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.philips.platform.appinfra.tagging.AnalyticsInterface;
import com.philips.platform.baseapp.base.AppFrameworkApplication;

import java.util.Map;

public class AnalyticsImplementor implements AnalyticsInterface {


    @Override
    public void trackPage(String pageName) {
        FirebaseAnalytics firebaseAnalytics = AppFrameworkApplication.getFirebaseAnalytics();
        Bundle bundle = new Bundle();
        firebaseAnalytics.logEvent(pageName,bundle);
        Log.d("Pabitra","track page");
    }

    @Override
    public void trackEventWithInfo(String eventName, Map<String, String> paramMap) {
        Bundle bundle = new Bundle();
        FirebaseAnalytics firebaseAnalytics = AppFrameworkApplication.getFirebaseAnalytics();
       /* bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "12345");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "pabitra");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");*/
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            bundle.putString(entry.getKey(),entry.getValue());
        }
        eventName = FirebaseAnalytics.Event.SELECT_CONTENT;
        firebaseAnalytics.logEvent(eventName, bundle);
        Log.d("Pabitra","track event");
    }
}
