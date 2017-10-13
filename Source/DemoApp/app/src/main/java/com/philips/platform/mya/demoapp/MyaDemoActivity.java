package com.philips.platform.mya.demoapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.philips.cdp.registration.User;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.mya.demouapp.MyaDemouAppInterface;
import com.philips.platform.mya.demouapp.MyaDemouAppLaunchInput;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.utils.UIDActivity;
import com.philips.platform.urdemo.R;

import com.philips.platform.urdemo.URDemouAppDependencies;
import com.philips.platform.urdemo.URDemouAppInterface;
import com.philips.platform.urdemo.URDemouAppSettings;
import com.philips.themesettings.ThemeSettingsActivity;

public class MyaDemoActivity extends UIDActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myademo);

        Button changeTheme = (Button) findViewById(R.id.change_theme);
        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyaDemoActivity.this, ThemeSettingsActivity.class);
                startActivity(intent);
            }
        });
        setStandardFlow();
        setMyaccountFlow();
    }

    private void setStandardFlow() {
        TextView standardFlow = (TextView) findViewById(R.id.usrdemo_mainScreen_standard_text);
        standardFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URDemouAppInterface uAppInterface;
                uAppInterface = new URDemouAppInterface();
                AppInfraInterface appInfraInterface = MyaDemoApplication.getInstance().getAppInfra();
                uAppInterface.init(new URDemouAppDependencies(appInfraInterface), new URDemouAppSettings(MyaDemoActivity.this.getApplicationContext()));
                uAppInterface.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, 0), null);
            }
        });
    }

    private void setMyaccountFlow() {
        TextView myAccountFlow = (TextView) findViewById(R.id.myademo_mainScreen_text);
        myAccountFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLoggedIn()) {
                    launchMyAccount();
                } else {
                    Toast.makeText(MyaDemoActivity.this, "please login before launching My account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void launchMyAccount() {
        MyaDemouAppInterface uAppInterface;
        uAppInterface = new MyaDemouAppInterface();
        AppInfraInterface appInfraInterface = MyaDemoApplication.getInstance().getAppInfra();
        uAppInterface.init(new URDemouAppDependencies(appInfraInterface), new URDemouAppSettings(MyaDemoActivity.this.getApplicationContext()));
        MyaDemouAppLaunchInput myaDemouAppLaunchInput = new MyaDemouAppLaunchInput();
        myaDemouAppLaunchInput.setContext(this.getApplicationContext());
        uAppInterface.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, 0), myaDemouAppLaunchInput);
    }

    public boolean isUserLoggedIn() {
        return new User(this).isUserSignIn();
    }
}
