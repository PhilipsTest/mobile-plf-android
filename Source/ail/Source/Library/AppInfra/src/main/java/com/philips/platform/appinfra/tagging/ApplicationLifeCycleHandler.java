package com.philips.platform.appinfra.tagging;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.logging.LoggingInterface;

/**
 * A Wrapper class for Application Life Cycle.
 */

public class ApplicationLifeCycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private static final String TAG = ApplicationLifeCycleHandler.class.getSimpleName();


    private AppInfraInterface mAppInfra;
    private AppTaggingInterface mAppTaggingInterface;

    public ApplicationLifeCycleHandler(AppInfraInterface appInfra) {
        mAppInfra = appInfra;
        mAppTaggingInterface = mAppInfra.getTagging();
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING,"ApplicationLifeCycleHandler Created");

    }

    @Override
    public void onActivityStarted(Activity activity) {
        ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING,"ApplicationLifeCycleHandler Started");

    }

    @Override
    public void onActivityResumed(Activity activity) {



            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                    AppInfraLogEventID.AI_TAGGING,"ApplicationLifeCycleHandler Resumed");

    }

    @Override
    public void onActivityPaused(Activity activity) {
        ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING,"ApplicationLifeCycleHandler Paused");

    }

    @Override
    public void onActivityStopped(Activity activity) {
        ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Stopped");

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler SaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Destroyed");

    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler ConfigurationChanged");
//        Intent i = mAppInfra.getAppInfraContext().getApplicationContext().getPackageManager().getLaunchIntentForPackage(mAppInfra.getAppInfraContext().getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//        mAppInfra.getAppInfraContext().startActivity(i);
    }

    @Override
    public void onLowMemory() {
        ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING,"ApplicationLifeCycleHandler onLowMemory");
    }

    @Override
    public void onTrimMemory(int i) {
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                    AppInfraLogEventID.AI_TAGGING,"ApplicationLifeCycleHandler Background");

            
        }
    }
}