package com.philips.cdp.di.iapdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;


import com.ecs.demouapp.integration.EcsDemoAppSettings;
import com.ecs.demouapp.integration.EcsDemoUAppDependencies;
import com.ecs.demouapp.integration.EcsDemoUAppInterface;
import com.ecs.demouapp.integration.EcsLaunchInput;
import com.philips.cdp.di.pesdemo.R;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.uappframework.launcher.ActivityLauncher;

/**
 * Created by philips on 6/16/17.
 */

public class LuncherActivity extends Activity {

    private EcsDemoUAppInterface iapDemoUAppInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher);
    }

    public void launch(View v) {
        DemoApplication demoApplication = (DemoApplication) getApplicationContext();
        AppInfra appInfra = demoApplication.getAppInfra();
        iapDemoUAppInterface = new EcsDemoUAppInterface();
        iapDemoUAppInterface.init(new EcsDemoUAppDependencies(appInfra), new EcsDemoAppSettings(this));
        iapDemoUAppInterface.launch(new ActivityLauncher(this,ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED,null, 0,null), new EcsLaunchInput());
    }
}
