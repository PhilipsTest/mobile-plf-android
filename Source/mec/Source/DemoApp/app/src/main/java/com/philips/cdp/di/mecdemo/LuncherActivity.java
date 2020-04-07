package com.philips.cdp.di.mecdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.mec.demouapp.MecDemoAppSettings;
import com.mec.demouapp.MecDemoUAppDependencies;
import com.mec.demouapp.MecDemoUAppInterface;
import com.mec.demouapp.MecLaunchInput;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.uappframework.launcher.ActivityLauncher;


public class LuncherActivity extends Activity {

    private MecDemoUAppInterface mecDemoUAppInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher);
    }

    public void launchMEC(View v) {
        DemoApplication demoApplication = (DemoApplication) getApplicationContext();
        AppInfra appInfra = demoApplication.getAppInfra();
        mecDemoUAppInterface = new MecDemoUAppInterface();
        mecDemoUAppInterface.init(new MecDemoUAppDependencies(appInfra), new MecDemoAppSettings(this));
        mecDemoUAppInterface.launch(new ActivityLauncher(this,ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED,null, 0,null), new MecLaunchInput());
    }
}
