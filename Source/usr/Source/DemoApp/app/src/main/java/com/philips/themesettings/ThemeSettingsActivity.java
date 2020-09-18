package com.philips.themesettings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ColorRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;
import com.philips.platform.uid.utils.UIDLocaleHelper;
import com.philips.platform.urdemo.URDemoActivity;
import com.philips.platform.urdemo.URDemoApplication;
import com.philips.platform.urdemolibrary.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ThemeSettingsActivity extends UIDActivity {

    static final String THEMESETTINGS_ACTIVITY_RESTART = "THEMESETTINGS_ACTIVITY_RESTART";

    private SharedPreferences defaultSharedPreferences;
    ContentColor contentColor;
    ColorRange colorRange;
    NavigationColor navigationColor;
    private AccentRange accentColorRange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.theme_settings_activity);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.theme_settings_container, new ThemeSettingsFragment())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initTheme() {
        final ThemeConfiguration themeConfig = getThemeConfig();
        final int themeResourceId = getThemeResourceId(getResources(), getPackageName(), colorRange, contentColor);
        themeConfig.add(navigationColor);
        themeConfig.add(accentColorRange);
        setTheme(themeResourceId);
        UIDLocaleHelper.getInstance().setFilePath(getCatalogAppJSONAssetPath());

        UIDHelper.init(themeConfig);
    }

    public ThemeConfiguration getThemeConfig() {
        final ThemeHelper themeHelper = new ThemeHelper(defaultSharedPreferences);
        colorRange = themeHelper.initColorRange();
        navigationColor = themeHelper.initNavigationRange();
        contentColor = themeHelper.initContentTonalRange();
        accentColorRange = themeHelper.initAccentRange();
        return new ThemeConfiguration(this, colorRange, navigationColor, contentColor, accentColorRange);
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

    public String getCatalogAppJSONAssetPath() {
        try {
            File f = new File(getCacheDir() + "/catalogapp.json");
            InputStream is = getAssets().open("catalogapp.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
            return f.getPath();
        } catch (FileNotFoundException e) {
            RLog.e(ThemeSettingsActivity.class.getName(),"getCatalogAppJSONAssetPath: " +e.getMessage());
        } catch (IOException e) {
            RLog.e(ThemeSettingsActivity.class.getName(), "getCatalogAppJSONAssetPath: " +e.getMessage());
        }
        return null;
    }

    @StyleRes
    int getThemeResourceId(Resources resources, final String packageName, final ColorRange colorRange, final ContentColor contentColor) {
        final String themeName = String.format("Theme.DLS.%s.%s", toCamelCase(colorRange.name()), toCamelCase(contentColor.name()));

        return resources.getIdentifier(themeName, "style", packageName);
    }

    public void applyTheme() {
//        saveThemeSettings();
        restartActivity();
    }

    public void saveThemeSettings() {
        saveThemeValues(UIDHelper.COLOR_RANGE, colorRange.name());
        saveThemeValues(UIDHelper.NAVIGATION_RANGE, navigationColor.name());
        saveThemeValues(UIDHelper.CONTENT_TONAL_RANGE, contentColor.name());
        saveThemeValues(UIDHelper.ACCENT_RANGE, accentColorRange.name());
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


    @SuppressLint("CommitPrefEdits")
    private void saveThemeValues(final String key, final String name) {
        final SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putString(key, name);
        edit.commit();
    }

    void restartActivity() {
        ((URDemoApplication) getApplicationContext()).injectNewTheme(colorRange, contentColor, navigationColor, accentColorRange);
        Intent intent = new Intent(this, URDemoActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(THEMESETTINGS_ACTIVITY_RESTART, true);
        startActivity(intent);
    }
}
