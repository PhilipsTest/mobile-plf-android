
package com.philips.platform.pimdemo;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.squareup.leakcanary.LeakCanary;

public class PimDemoApplication extends Application {

    @NonNull
    private AppInfraInterface appInfraInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        //LeakCanary.install(this);
        appInfraInterface = new AppInfra.Builder().build(this);
    }

    public AppInfraInterface getAppInfra() {
        return appInfraInterface;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        UIDHelper.injectCalligraphyFonts();
    }

}