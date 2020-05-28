
package com.philips.platform.pimdemo;

import android.app.Application;
import android.content.Context;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.squareup.leakcanary.LeakCanary;

public class PimDemoApplication extends Application {
    private AppInfraInterface mAppInfraInterface;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        UIDHelper.injectCalligraphyFonts();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
//        mAppInfraInterface = new AppInfra.Builder().build(this);

    }


//    public AppInfraInterface getAppInfra() {
//        return mAppInfraInterface;
//    }

}