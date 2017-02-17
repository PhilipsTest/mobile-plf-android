/**
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 */
package com.philips.platform.uid.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.core.deps.guava.annotations.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;

import com.philips.platform.uid.thememanager.ColorRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseTestActivity extends AppCompatActivity implements DelayerCallback {
    public static final String CONTENT_COLOR_KEY = "ContentColor";
    public static final String NAVIGATION_COLOR_KEY = "NavigationColor";

    private Toolbar toolbar;
    @Nullable
    private UidIdlingResource mIdlingResource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int navigationColor = NavigationColor.BRIGHT.ordinal();
        int contentColor = 0;
        if (getIntent() != null && getIntent().getExtras() != null) {
            final Bundle extras = getIntent().getExtras();
            navigationColor = extras.getInt(NAVIGATION_COLOR_KEY, 1);
            contentColor = extras.getInt(CONTENT_COLOR_KEY, 0);
        }
        UIDHelper.injectCalligraphyFonts();
        UIDHelper.init(getThemeConfig(navigationColor, contentColor));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
    }

    private ThemeConfiguration getThemeConfig(final int navigationColor, final int contentColor) {
        return new ThemeConfiguration(ColorRange.GROUP_BLUE, ContentColor.values()[contentColor], NavigationColor.values()[navigationColor], this);
    }

    public void switchTo(final int layout) {
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        setContentView(layout);
                        if (layout == com.philips.platform.uid.test.R.layout.main_layout) {
                            toolbar = (Toolbar) findViewById(com.philips.platform.uid.R.id.uid_toolbar);
                            toolbar.setNavigationContentDescription(getString(com.philips.platform.uid.test.R.string.navigation_content_desc));
                            toolbar.setNavigationIcon(com.philips.platform.uid.test.R.drawable.ic_hamburger_menu);
                            UIDHelper.setupToolbar(BaseTestActivity.this);
                            toolbar.setTitle(getString(com.philips.platform.uid.test.R.string.catalog_app_name));
                        }
                    }
                }
        );
    }

    public void switchFragment(final Fragment fragment) {
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(com.philips.platform.uid.test.R.id.container, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
    }

    @Override
    protected void attachBaseContext(final Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(com.philips.platform.uid.test.R.menu.notificationbar, menu);
        sendMessage();
        return true;
    }

    private void sendMessage() {
        MessageDelayer.sendMessage("Hello", this, mIdlingResource);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        return true;
    }

    public Toolbar getToolbar() {
        return (Toolbar) findViewById(com.philips.platform.uid.test.R.id.uid_toolbar);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new UidIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    public void onDone(final String text) {
        //Do nothing
    }

    static class MessageDelayer {

        private static final long DELAY_MILLIS = 300;

        static void sendMessage(final String message, final DelayerCallback callback,
                                @Nullable final UidIdlingResource idlingResource) {
            if (idlingResource != null) {
                idlingResource.setIdleState(false);
            }

            // Delay the execution, return message via callback.
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onDone(message);
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                }
            }, DELAY_MILLIS);
        }
    }
}
