/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.demouapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.StyleRes;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.philips.platform.mya.demouapp.fragment.LaunchFragment;
import com.philips.platform.mya.demouapp.theme.events.AccentColorChangedEvent;
import com.philips.platform.mya.demouapp.theme.events.ColorRangeChangedEvent;
import com.philips.platform.mya.demouapp.theme.events.ContentTonalRangeChangedEvent;
import com.philips.platform.mya.demouapp.theme.events.NavigationColorChangedEvent;
import com.philips.platform.mya.demouapp.theme.events.OptionMenuClickedEvent;
import com.philips.platform.mya.demouapp.theme.themesettings.NavigationController;
import com.philips.platform.mya.demouapp.theme.themesettings.ThemeHelper;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ColorRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.github.inflationx.calligraphy.CalligraphyContextWrapper;


public class DemoAppActivity extends UIDActivity {

    public static final String TITLE_TEXT = "TITLE_TEXT";
    public static final String THEMESETTINGS_ACTIVITY_RESTART = "THEMESETTINGS_ACTIVITY_RESTART";

    private ContentColor contentColor;
    private ColorRange colorRange;
    private NavigationColor navigationColor;
    private NavigationController navigationController;
    private SharedPreferences defaultSharedPreferences;
    private AccentRange accentColorRange;
    private  int themeResourceId=0;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initTheme();
        super.onCreate(savedInstanceState);

        ViewDataBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        EventBus.getDefault().register(this);


        navigationController = new NavigationController(this, getIntent(), activityMainBinding);
        navigationController.init(savedInstanceState);
        if (savedInstanceState == null) {
            LaunchFragment launchFragment = new LaunchFragment();
            navigationController.switchFragment(launchFragment);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.uid_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.uid_toolbar_title);
    }

    public void initTheme() {
        final ThemeConfiguration themeConfig = getThemeConfig();
        themeResourceId = getThemeResourceId(getResources(), getPackageName(), colorRange, contentColor,navigationColor, accentColorRange);
        themeConfig.add(navigationColor);
        themeConfig.add(accentColorRange);
        setTheme(themeResourceId);
        UIDHelper.init(themeConfig);
    }

    @VisibleForTesting
    public void setColorRange(final ColorRange colorRange) {
        this.colorRange = colorRange;
    }

    @VisibleForTesting
    public void setContentColor(final ContentColor contentColor) {
        this.contentColor = contentColor;
    }
    @Override
    protected void attachBaseContext(final Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void saveThemeSettings() {
        saveThemeValues(UIDHelper.COLOR_RANGE, colorRange.name());
        saveThemeValues(UIDHelper.NAVIGATION_RANGE, navigationColor.name());
        saveThemeValues(UIDHelper.CONTENT_TONAL_RANGE, contentColor.name());
        saveThemeValues(UIDHelper.ACCENT_RANGE, accentColorRange.name());
    }
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        navigationController.onSaveInstance(outState);
        super.onSaveInstanceState(outState);
    }

    public ThemeConfiguration getThemeConfig() {
        final ThemeHelper themeHelper = new ThemeHelper(defaultSharedPreferences);
        colorRange = themeHelper.initColorRange();
        navigationColor = themeHelper.initNavigationRange();
        contentColor = themeHelper.initContentTonalRange();
        accentColorRange = themeHelper.initAccentRange();
        return new ThemeConfiguration(this, colorRange, navigationColor, contentColor, accentColorRange);
    }

    @SuppressLint("CommitPrefEdits")
    private void saveThemeValues(final String key, final String name) {
        final SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putString(key, name);
        edit.apply();
    }
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        navigationController.onPrepareOptionsMenu(menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        EventBus.getDefault().post(new OptionMenuClickedEvent(item.toString()));
        int i = item.getItemId();
        if (i == R.id.menu_theme_settings) {
            navigationController.loadThemeSettingsPage();

        } else if (i == R.id.menu_set_theme_settings) {
            saveThemeSettings();
            restartActivity();

        } else if (i == android.R.id.home) {
            if (navigationController.hasBackStack()) {
                onBackPressed();
            }
        }
        return true;
    }


    void restartActivity() {
        Intent intent = new Intent(this, DemoAppActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(THEMESETTINGS_ACTIVITY_RESTART, true);
        startActivity(intent);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationController.initIconState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        navigationController.onCreateOptionsMenu(menu, this);
        return true;
    }

    public int getThemeResourceId()
    {
        return  themeResourceId;
    }

    @StyleRes
    int getThemeResourceId(Resources resources, final String packageName, final ColorRange colorRange, final ContentColor contentColor, NavigationColor navigationColor, AccentRange accentColorRange) {
        final String themeName = String.format("Theme.DLS.%s.%s", toCamelCase(colorRange.name()), toCamelCase(contentColor.name()));

        return resources.getIdentifier(themeName, "style", packageName);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessage(ContentTonalRangeChangedEvent event) {
        contentColor = event.getContentColor();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessage(ColorRangeChangedEvent event) {
        colorRange = event.getColorRange();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessage(NavigationColorChangedEvent event) {
        navigationColor = event.getNavigationColor();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessage(AccentColorChangedEvent event) {
        accentColorRange = event.getAccentRange();
    }
    static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean backState = false;
        Fragment currentFrag = fragmentManager
                .findFragmentById(R.id.mainContainer);
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            if (currentFrag != null && currentFrag instanceof BackEventListener) {
                backState = ((BackEventListener) currentFrag).handleBackEvent();
            }

            if (!backState) {
                super.onBackPressed();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void setTitle(String data) {
        mTitle.setText(data);
    }

    public void setTitle(int resId) {
        mTitle.setText(resId);
    }



}
