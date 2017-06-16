/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.demo;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.os.Trace;
import android.util.Log;

import com.crittercism.app.Crittercism;
import com.crittercism.app.CrittercismConfig;
import com.facebook.stetho.Stetho;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.tagging.AppTagging;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.appinfra.tagging.ApplicationLifeCycleHandler;
import com.squareup.leakcanary.LeakCanary;

import java.util.Map;

/**
 * Created by deepakpanigrahi on 5/18/16.
 */
public class AppInfraApplication extends Application {
    private static final String CRITTERCISM_APP_ID = "cba7f25561b444e5b0aa29639669532d00555300";
    public static AppTaggingInterface mAIAppTaggingInterface;
    public static AppInfraInterface gAppInfra;
    //SecurDb
    public static String DATABASE_PASSWORD_KEY = "philips@321";
    static SecureStorageInterface mSecureStorage = null;
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor;
    private AppInfra mAppInfra;
    private BroadcastReceiver rec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() == AppTagging.ACTION_TAGGING_DATA) {
                    Log.d("AppInfra APP", "BroadcastReceiver() {...}.onReceive()");
                    Map textExtra = (Map) intent.getSerializableExtra(AppTagging.EXTRA_TAGGING_DATA);
                    Log.d("APPINFRA-TAGGING", textExtra.toString());
                    Crittercism.leaveBreadcrumb(textExtra.toString());
                    /*Toast.makeText(getApplicationContext(),
                            textExtra.toString(), Toast.LENGTH_LONG).show();*/
                }
            }

        }
    };

    @Override
    public void onCreate() {

        Crittercism.initialize(getApplicationContext(), CRITTERCISM_APP_ID);
        Crittercism.didCrashOnLastLoad();
        CrittercismConfig config = new CrittercismConfig();
        config.setLogcatReportingEnabled(true);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        //https://developer.android.com/reference/android/os/StrictMode.html
        // to monitor penaltyLog() log output in logcat for ANR or any other performance issue
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        super.onCreate();
        LeakCanary.install(this);
//        android.os.Debug.startMethodTracing("yourstring");
        Trace.beginSection("NewOne");

        gAppInfra = new AppInfra.Builder().build(getApplicationContext());

        Trace.endSection();

        // android.os.Debug.stopMethodTracing();


        gAppInfra.getTime().refreshTime();
        mAppInfra = (AppInfra)gAppInfra;

        mAIAppTaggingInterface = gAppInfra.getTagging();
        mAIAppTaggingInterface.registerTaggingData(rec);
        mAIAppTaggingInterface.trackPageWithInfo("Main APP" ,"APP " , "APPINFRA");
        mAIAppTaggingInterface.trackVideoEnd("track - demo APP");
        mAIAppTaggingInterface.setPreviousPage("SomePreviousPage");
        ApplicationLifeCycleHandler handler = new ApplicationLifeCycleHandler(mAppInfra);
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
       mAIAppTaggingInterface.unregisterTaggingData(rec);
    }

}
