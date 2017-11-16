package com.philips.cdp2.ews.demoapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.cdp2.ews.demoapplication.themesettinngs.ThemeHelper;
import com.philips.cdp2.ews.demoapplication.themesettinngs.ThemeSettingsFragment;
import com.philips.cdp2.ews.microapp.EWSActionBarListener;
import com.philips.cdp2.ews.microapp.EWSLauncherInput;
import com.philips.platform.uid.drawable.FontIconDrawable;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ColorRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;

import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class EWSDemoActivity extends UIDActivity implements EWSActionBarListener {

    private SharedPreferences defaultSharedPreferences;
    private OptionSelectionFragment optionSelectionFragment;

    public ColorRange colorRange = ColorRange.GROUP_BLUE;
    public ContentColor contentColor = ContentColor.VERY_DARK;
    public AccentRange accentColorRange = AccentRange.ORANGE;
    public NavigationColor navigationColor = NavigationColor.VERY_DARK;

    private EWSLauncherInput ewsLauncherInput;
    private ThemeHelper themeHelper;
    private ImageView closeImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        themeHelper = new ThemeHelper(defaultSharedPreferences, this);
        updateColorFromPref();
        injectNewTheme(colorRange, contentColor, navigationColor, accentColorRange);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        closeImageView = findViewById(R.id.ic_close);
        ewsLauncherInput = new EWSLauncherInput();
        this.optionSelectionFragment = new OptionSelectionFragment();
        showConfigurationOptScreen();
        setUpToolBar();
    }

    public EWSLauncherInput getEwsLauncherInput() {
        return ewsLauncherInput;
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(com.philips.cdp2.ews.R.id.ews_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        toolbar.inflateMenu(R.menu.option_menu);
        setUpCancelButton();
    }

    private void setUpCancelButton() {
        FontIconDrawable drawable = new FontIconDrawable(this, getResources().getString(R.string.dls_cross_24), TypefaceUtils.load(getAssets(), "fonts/iconfont.ttf"))
                .sizeRes(R.dimen.ews_gs_icon_size);
        closeImageView.setBackground(drawable);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo need to handle close button with current fragment instance
                if (ewsLauncherInput != null) {
                    ewsLauncherInput.handleCloseButtonClick();
                }
            }
        });
    }

    private void hideShowImageView() {
        if (getCurrentFragment() instanceof OptionSelectionFragment || getCurrentFragment() instanceof ThemeSettingsFragment) {
            closeImageView.setVisibility(View.GONE);
        } else {
            closeImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateMenuOption(Menu menu) {
        if (isThemeScreen()) {
            menu.findItem(R.id.menu_set_theme_settings).setVisible(true);
        } else {
            menu.findItem(R.id.menu_set_theme_settings).setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuOption(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.mainContainer);
    }

    private boolean isThemeScreen() {
        if (getCurrentFragment() instanceof ThemeSettingsFragment) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_set_theme_settings:
                saveThemeSettings();
                restartActivity();
                //showConfigurationOptScreen();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showConfigurationOptScreen() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, optionSelectionFragment)
                .addToBackStack(optionSelectionFragment.getClass().getCanonicalName())
                .commit();
        updateActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void updateContentColor(ContentColor contentColor) {
        this.contentColor = contentColor;
    }

    public void updateColorRange(ColorRange colorRange) {
        this.colorRange = colorRange;
    }

    public void updateNavigationColor(NavigationColor navigationColor) {
        this.navigationColor = navigationColor;
    }

    public void updateAccentColor(AccentRange accentColorRange) {
        this.accentColorRange = accentColorRange;
    }

    @SuppressLint("CommitPrefEdits")
    private void saveThemeValues(final String key, final String name) {
        final SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putString(key, name);
        edit.commit();
    }

    public void saveThemeSettings() {
        saveThemeValues(UIDHelper.COLOR_RANGE, colorRange.name());
        saveThemeValues(UIDHelper.NAVIGATION_RANGE, navigationColor.name());
        saveThemeValues(UIDHelper.CONTENT_TONAL_RANGE, contentColor.name());
        saveThemeValues(UIDHelper.ACCENT_RANGE, accentColorRange.name());
    }

    private void updateColorFromPref() {
        colorRange = themeHelper.initColorRange();
        navigationColor = themeHelper.initNavigationRange();
        contentColor = themeHelper.initContentTonalRange();
        accentColorRange = themeHelper.initAccentRange();
    }

    public ThemeConfiguration getThemeConfig() {
        updateColorFromPref();
        return new ThemeConfiguration(this, colorRange, navigationColor, contentColor, accentColorRange);
    }

    public void openThemeScreen() {
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ThemeSettingsFragment()).commit();
        updateActionBar();
    }

    private void updateActionBar() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
                hideShowImageView();
                isThemeScreen();
            }
        }, 500);
    }

    @Override
    public void closeButton(boolean visibility) {
        findViewById(R.id.ic_close).setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setToolbarTitle(String s) {
        Toolbar toolbar = findViewById(R.id.ews_toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(s);
    }

    @Override
    public void updateActionBar(int i, boolean b) {
        setToolbarTitle(getString(i));
    }

    @Override
    public void updateActionBar(String s, boolean b) {
        setToolbarTitle(s);
    }

    void restartActivity() {
        injectNewTheme(colorRange, contentColor, navigationColor, accentColorRange);
        Intent intent = new Intent(this, EWSDemoActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void injectNewTheme(ColorRange colorRange, ContentColor contentColor, NavigationColor navigationColor, AccentRange accentRange) {
        if (colorRange != null) {
            this.colorRange = colorRange;
        }
        if (contentColor != null) {
            this.contentColor = contentColor;
        }
        if (navigationColor != null) {
            this.navigationColor = navigationColor;
        }
        if (accentRange != null) {
            this.accentColorRange = accentRange;
        }

        String themeName = String.format("Theme.DLS.%s.%s", this.colorRange.getThemeName(), this.contentColor.getThemeName());
        getTheme().applyStyle(getResources().getIdentifier(themeName, "style", getPackageName()), true);
        ThemeConfiguration themeConfiguration = new ThemeConfiguration(this, this.colorRange, this.contentColor, this.navigationColor, this.accentColorRange);
        UIDHelper.init(themeConfiguration);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateActionBar();
    }
}