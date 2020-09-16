/* Copyright (c) Koninklijke Philips N.V., 2017
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.themesettings;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ColorRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDLangPackActivity;
import com.philips.platform.uid.utils.UIDLangPackActivity;

public class ThemeSelectionActivity extends UIDLangPackActivity {
    private Toolbar toolbar;
    ThemeSettingsFragment fragment;
    protected SharedPreferences defaultSharedPreferences;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeHelper helper = new ThemeHelper(PreferenceManager.getDefaultSharedPreferences(this), this);
        setTheme(getThemeResourceId(helper.initColorRange(), helper.initContentTonalRange()));
        UIDHelper.init(new ThemeConfiguration(this, helper.initAccentRange(), helper.initNavigationRange(),helper.initColorRange(), helper.initContentTonalRange()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_activity);
        initToolBar();
        fragment = (ThemeSettingsFragment) getSupportFragmentManager().findFragmentById(R.id.selection_fragment);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
            finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theme_selection_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.set_theme) {
            Intent intent=new Intent();
            intent.putExtra("CR", fragment.contentColor);
            intent.putExtra("NR", fragment.navigationColor);
            intent.putExtra("AR", fragment.accentRange);
            intent.putExtra("CLR", fragment.colorRange);
            setResult(Activity.RESULT_OK,intent);
            saveThemeSettings(fragment.contentColor,fragment.navigationColor,fragment.accentRange,fragment.colorRange);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.uid_toolbar);
        UIDHelper.setupToolbar(this);
        toolbar.setNavigationIcon(VectorDrawableCompat.create(getResources(), R.drawable.back_icon, getTheme()));
        UIDHelper.setTitle(this, "Select theme");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @StyleRes
    int getThemeResourceId(final ColorRange colorRange, final ContentColor contentColor) {
        final String themeName = String.format("Theme.DLS.%s.%s", colorRange.getThemeName(), contentColor.getThemeName());

        return getResources().getIdentifier(themeName, "style", getPackageName());
    }

    public void saveThemeSettings(ContentColor contentColor, NavigationColor navigationColor, AccentRange accentRange, ColorRange colorRange)
    {
        saveThemeValues(UIDHelper.COLOR_RANGE,colorRange.name());
        saveThemeValues(UIDHelper.NAVIGATION_RANGE,navigationColor.name());
        saveThemeValues(UIDHelper.CONTENT_TONAL_RANGE, contentColor.name());
        saveThemeValues(UIDHelper.ACCENT_RANGE,accentRange.name());

    }
    @SuppressLint("CommitPrefEdits")
    private void  saveThemeValues(final String key , final String name)
    {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor edit =defaultSharedPreferences.edit();
        edit.putString(key,name);
        edit.commit();
    }
}