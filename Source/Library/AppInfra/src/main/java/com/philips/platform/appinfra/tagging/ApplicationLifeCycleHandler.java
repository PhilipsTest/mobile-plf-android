package com.philips.platform.appinfra.tagging;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.logging.LoggingInterface;

/**
 * Created by 310200764 on 9/1/2015.
 */

public class ApplicationLifeCycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private static final String TAG = ApplicationLifeCycleHandler.class.getSimpleName();
    public static boolean isInBackground = true;

    private AppInfra mAppInfra;
    private AppTaggingInterface mAppTaggingInterface;

    public ApplicationLifeCycleHandler(AppInfra appInfra) {
        mAppInfra = appInfra;
        mAppTaggingInterface = appInfra.getTagging();
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE,
                "ApplicationLifeCycleHandler", "Created");

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE,
                "ApplicationLifeCycleHandler", "Started");

    }

    @Override
    public void onActivityResumed(Activity activity) {

        if (isInBackground) {

            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE,
                    "ApplicationLifeCycleHandler", "Resumed");
        mAppTaggingInterface.trackActionWithInfo("sendData", "appStatus", "ForeGround");
            isInBackground = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE,
                "ApplicationLifeCycleHandler", "Paused");

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE,
                "ApplicationLifeCycleHandler", "Stopped");

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE,
                "ApplicationLifeCycleHandler", "Destroyed");

    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE,
                "ApplicationLifeCycleHandler", "ConfigurationChanged");
//        Intent i = mAppInfra.getAppInfraContext().getApplicationContext().getPackageManager().getLaunchIntentForPackage(mAppInfra.getAppInfraContext().getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//        mAppInfra.getAppInfraContext().startActivity(i);
    }

    @Override
    public void onLowMemory() {
        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE,
                "ApplicationLifeCycleHandler", "onLowMemory");
    }

    @Override
    public void onTrimMemory(int i) {
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE,
                    "ApplicationLifeCycleHandler", "Background");

            mAppTaggingInterface.trackActionWithInfo("sendData","appStatus", "Background");
            isInBackground = true;
        }
    }
}