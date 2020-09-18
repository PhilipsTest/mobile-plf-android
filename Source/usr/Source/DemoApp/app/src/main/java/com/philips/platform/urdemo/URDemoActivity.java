package com.philips.platform.urdemo;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.utils.UIDActivity;
import com.philips.themesettings.ThemeSettingsActivity;

public class URDemoActivity extends UIDActivity {

    private URDemouAppInterface uAppInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urdemo);
        uAppInterface = new URDemouAppInterface();
        Button changeTheme = (Button) findViewById(R.id.change_theme);
        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(URDemoActivity.this, ThemeSettingsActivity.class);
                startActivity(intent);
            }
        });
        AppInfraInterface appInfraInterface = URDemoApplication.getInstance().getAppInfra();
        uAppInterface.init(new URDemouAppDependencies(appInfraInterface), new URDemouAppSettings(this.getApplicationContext()));
        setStandardFlow();
    }

    private void setStandardFlow() {
        TextView standardFlow = (TextView) findViewById(R.id.usrdemo_mainScreen_standard_text);
        standardFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uAppInterface.launch(new ActivityLauncher(URDemoActivity.this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, null,0,null), null);
            }
        });
    }

}
