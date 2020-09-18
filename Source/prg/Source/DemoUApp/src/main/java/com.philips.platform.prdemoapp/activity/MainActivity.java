/*
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 *
 */

package com.philips.platform.prdemoapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.StyleRes;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.philips.platform.prdemoapp.fragment.LaunchFragment;
import com.philips.platform.prdemoapp.theme.NavigationController;
import com.philips.platform.prdemoapp.theme.events.AccentColorChangedEvent;
import com.philips.platform.prdemoapp.theme.events.ColorRangeChangedEvent;
import com.philips.platform.prdemoapp.theme.events.ContentTonalRangeChangedEvent;
import com.philips.platform.prdemoapp.theme.events.NavigationColorChangedEvent;
import com.philips.platform.prdemoapp.theme.events.OptionMenuClickedEvent;
import com.philips.platform.prdemoapp.theme.themesettings.ThemeHelper;
import com.philips.platform.prdemoapplibrary.R;
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

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends UIDActivity {

    public static final String TITLE_TEXT = "TITLE_TEXT";
    public static final String THEMESETTINGS_ACTIVITY_RESTART = "THEMESETTINGS_ACTIVITY_RESTART";

    ContentColor contentColor;
    ColorRange colorRange;
    NavigationColor navigationColor;
    private NavigationController navigationController;
    private ViewDataBinding activityMainBinding;
    private SharedPreferences defaultSharedPreferences;
    private AccentRange accentColorRange;

    private FragmentManager fragmentManager;
    private TextView mTitleTextView;
    private Handler mSiteCatListHandler = new Handler();
    private  int  themeResourceId=0;

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
    protected void onCreate(Bundle savedInstanceState) {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initTheme();

        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        EventBus.getDefault().register(this);
        navigationController = new NavigationController(this, getIntent(), activityMainBinding);
        navigationController.init(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            LaunchFragment launchFragment = new LaunchFragment();
            /*FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.parent_layout, launchFragment);
            fragmentTransaction.commitAllowingStateLoss();*/
            navigationController.switchFragment(launchFragment);
        }


    }


    public void initTheme() {
        final ThemeConfiguration themeConfig = getThemeConfig();
         themeResourceId = getThemeResourceId(getResources(), getPackageName(), colorRange, contentColor,navigationColor, accentColorRange);
        themeConfig.add(navigationColor);
        themeConfig.add(accentColorRange);
        setTheme(themeResourceId);
       // UIDLocaleHelper.getInstance().setFilePath(getCatalogAppJSONAssetPath());

        UIDHelper.init(themeConfig);

    }

    public int getThemeResourceId()
    {
        return  themeResourceId;
    }

    public  NavigationColor getNavigationColor(){
        return navigationColor;
    }

    public AccentRange getAccentColorRange(){
        return accentColorRange;
    }

    @StyleRes
    int getThemeResourceId(Resources resources, final String packageName, final ColorRange colorRange, final ContentColor contentColor,NavigationColor navigationColor,AccentRange accentColorRange) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void restartActivity() {
        Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        navigationController.onPrepareOptionsMenu(menu);

        return true;
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
        edit.commit();
    }

    @Override
    public void setTitle(final int titleId) {
        //navigationController.setTitleText(titleId);
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

//        if (navigationController.updateStack()) {
//            super.onBackPressed();
//        }
//        navigationController.processBackButton();
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    @VisibleForTesting
    public void setColorRange(final ColorRange colorRange) {
        this.colorRange = colorRange;
    }

    @VisibleForTesting
    public void setContentColor(final ContentColor contentColor) {
        this.contentColor = contentColor;
    }


}
