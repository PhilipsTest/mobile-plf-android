/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.demoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.philips.cdp2.demouapp.CommlibUapp;
import com.philips.cdp2.demouapp.DefaultCommlibUappDependencies;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.uappinput.UappSettings;

public class MainActivity extends AppCompatActivity {

    private CommlibUapp commlibUapp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_launch_uapp_as_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                launchUappAsFragment();
            }
        });

        findViewById(R.id.btn_launch_uapp_as_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                launchUappAsActivity();
            }
        });

        commlibUapp = CommlibUapp.get();
        commlibUapp.init(new DefaultCommlibUappDependencies(), new UappSettings(getApplicationContext()));

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            AboutFragment.newInstance().show(getSupportFragmentManager(), "AboutFragment");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchUappAsFragment() {
        FragmentLauncher launcher = new FragmentLauncher(this, R.id.activity_main, null);
        commlibUapp.launch(launcher, null);
    }

    private void launchUappAsActivity() {
        ActivityLauncher launcher = new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, 0);
        commlibUapp.launch(launcher, null);
    }
}
