package com.mec.demouapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;


public class DemoLauncherActivity extends Activity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_launcher_activity);
    }

    public void launchAsActivity(View v) {
        Intent intent=new Intent(this,LaunchAsActivity.class);
        startActivity(intent);
    }

    public void launchAsFragment(View v) {
        Intent intent=new Intent(this,LaunchAsFragment.class);
        startActivity(intent);
    }
}
