package com.philips.platform.catalogapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.philips.platform.catalogapp.fragments.BaseFragment;
import com.philips.platform.catalogapp.fragments.ComponentListFragment;
import com.philips.platform.catalogapp.themesettings.ThemeSettingsFragment;
import com.philips.platform.uid.thememanager.UIDHelper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NavigationController {
    public static final String ACTIVE_FRAGMENTS = "activeFragments";
    protected static final String HAMBURGER_BUTTON_DISPLAYED = "HAMBURGER_BUTTON_DISPLAYED";
    protected static final String THEMESETTINGS_BUTTON_DISPLAYED = "THEMESETTINGS_BUTTON_DISPLAYED";
    SharedPreferences fragmentPreference;
    boolean hamburgerIconVisible;
    boolean themeSettingsIconVisible;
    private FragmentManager supportFragmentManager;
    private MainActivity mainActivity;
    private MenuItem themeSetting;
    private MenuItem setTheme;
    private ViewDataBinding activityMainBinding;
    private int titleResource;
    private Toolbar toolbar;

    public NavigationController(final MainActivity mainActivity, final Intent intent, final ViewDataBinding activityMainBinding) {
        this.mainActivity = mainActivity;
        fragmentPreference = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        this.activityMainBinding = activityMainBinding;
        toolbar = (Toolbar) activityMainBinding.getRoot().findViewById(R.id.uid_toolbar);
        clearLastFragmentOnFreshLaunch(intent);
    }

    protected void processBackButton() {
        if (hasBackStack()) {
            final Fragment fragmentAtTopOfBackStack = getFragmentAtTopOfBackStack();
            if (!(fragmentAtTopOfBackStack instanceof ThemeSettingsFragment)) {
                toggleHamburgerIcon();
            } else {
                showHamburgerIcon();
                storeFragmentInPreference(null);
            }
        } else if (supportFragmentManager != null && supportFragmentManager.getBackStackEntryCount() == 0) {
            showHamburgerIcon();
            storeFragmentInPreference(null);
        }
    }

    private void clearLastFragmentOnFreshLaunch(Intent intent) {
        if (intent != null && !intent.hasExtra(MainActivity.THEMESETTINGS_ACTIVITY_RESTART)) {
            storeFragmentInPreference(null);
        }
    }

    private void toggleHamburgerIcon() {
        toolbar.setNavigationIcon(VectorDrawableCompat.create(mainActivity.getResources(), R.drawable.ic_back_icon, mainActivity.getTheme()));
        hamburgerIconVisible = false;
    }

    protected void initDemoListFragment() {
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.add(R.id.mainContainer, new ComponentListFragment());
        transaction.commit();

        themeSettingsIconVisible(true);
    }

    private void themeSettingsIconVisible(final boolean visible) {
        themeSettingsIconVisible = visible;
    }

    public void loadThemeSettingsPage() {
        final ThemeSettingsFragment themeSettingsFragment = new ThemeSettingsFragment();
        final boolean switchedFragment = switchFragment(themeSettingsFragment);
        if (switchedFragment) {
            setTitleText(R.string.page_title_theme_settings);

            themeSettingsIconVisible(false);
        }
    }

    protected void showUiFromPreviousState(final Bundle savedInstanceState) {
        initIconState(savedInstanceState);
        processBackButton();
        if (hamburgerIconVisible) {
            showHamburgerIcon();
        }

        final int titleResourceId = savedInstanceState.getInt(MainActivity.TITLE_TEXT, -1);
        initTitle(titleResourceId);
    }

    private void initTitle(final int titleResourceId) {
        if (titleResourceId != -1) {
            setTitleText(titleResourceId);
        }
    }

    public boolean switchFragment(BaseFragment fragment) {

        final List<Fragment> fragments = supportFragmentManager.getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragmentFromList : fragments) {
                if (fragmentFromList instanceof ThemeSettingsFragment && fragment instanceof ThemeSettingsFragment) {
                    return false;
                }
            }
        }

        String tag = fragment.getClass().getName();
        if (!(fragment instanceof ThemeSettingsFragment)) {
            storeFragmentInPreference(tag);
        }

        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.mainContainer, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
        toggleHamburgerIcon();
        return true;
    }

    private void showHamburgerIcon() {
        toolbar.setNavigationIcon(VectorDrawableCompat.create(mainActivity.getResources(), R.drawable.ic_hamburger_icon, mainActivity.getTheme()));
        hamburgerIconVisible = true;
        setTitleText(R.string.catalog_app_name);
        titleResource = R.string.catalog_app_name;
    }

    protected boolean hasBackStack() {
        return supportFragmentManager != null && supportFragmentManager.getBackStackEntryCount() > 0;
    }

    @NonNull
    private Fragment getFragmentAtTopOfBackStack() {
        return getFragmentAtBackStackIndex(supportFragmentManager.getBackStackEntryCount() - 1);
    }

    @NonNull
    private Fragment getFragmentAtBackStackIndex(final int index) {

        final FragmentManager.BackStackEntry backStackEntry = supportFragmentManager.getBackStackEntryAt(index);
        final String name = backStackEntry.getName();
        return supportFragmentManager.findFragmentByTag(name);
    }

    public void init(final Bundle savedInstanceState) {
        this.supportFragmentManager = mainActivity.getSupportFragmentManager();

        UIDHelper.setupToolbar(mainActivity);

        if (savedInstanceState == null) {
            initDemoListFragment();
            showHamburgerIcon();

            restoreLastActiveFragment(getLastActiveFragmentName());
        } else {
            showUiFromPreviousState(savedInstanceState);
        }
    }

    public void onCreateOptionsMenu(final Menu menu, final MainActivity mainActivity) {
        mainActivity.getMenuInflater().inflate(R.menu.catalog_menu, menu);
    }

    public void onPrepareOptionsMenu(final Menu menu) {
        themeSetting = menu.findItem(R.id.menu_theme_settings);
        setTheme = menu.findItem(R.id.menu_set_theme_settings);
    }

    public void setTitleText(final int titleId) {
        titleResource = titleId;
        UIDHelper.setTitle(mainActivity, titleId);
    }


    public void setTitleText(CharSequence title) {
        UIDHelper.setTitle(mainActivity, title);
    }

    //Needed only untill we have hamburger
    public Toolbar getToolbar() {
        return toolbar;
    }

    public void storeFragmentInPreference(String fragmentTag) {

        if (fragmentTag == null) {
            fragmentPreference.edit().putStringSet(ACTIVE_FRAGMENTS, null).apply();
            return;
        }
        Set<String> set = new LinkedHashSet<>();
        final Set<String> stringSet = fragmentPreference.getStringSet(ACTIVE_FRAGMENTS, set);
        stringSet.add(fragmentTag);
        fragmentPreference.edit().putStringSet(ACTIVE_FRAGMENTS, stringSet).apply();
    }

    private Set<String> getLastActiveFragmentName() {
        return fragmentPreference.getStringSet(ACTIVE_FRAGMENTS, null);
    }

    private void restoreLastActiveFragment(final Set<String> lastActiveFragments) {
        if (lastActiveFragments != null) {
            for (String lastFragment : lastActiveFragments) {
                if (lastFragment != null) {
                    try {
                        Class<BaseFragment> fragmentClass = (Class<BaseFragment>) Class.forName(lastFragment);
                        switchFragment(fragmentClass.newInstance());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void onSaveInstance(final Bundle outState) {
        outState.putBoolean(HAMBURGER_BUTTON_DISPLAYED, hamburgerIconVisible);
        outState.putBoolean(THEMESETTINGS_BUTTON_DISPLAYED, themeSettingsIconVisible);
        outState.putInt(MainActivity.TITLE_TEXT, titleResource);
    }

    public void initIconState(final Bundle savedInstanceState) {
        hamburgerIconVisible = savedInstanceState.getBoolean(HAMBURGER_BUTTON_DISPLAYED);
        themeSettingsIconVisible = savedInstanceState.getBoolean(THEMESETTINGS_BUTTON_DISPLAYED);
    }

    public void updateStack() {
        if (hasBackStack()) {
            final List<Fragment> fragments = supportFragmentManager.getFragments();
            final Fragment fragment = fragments.get(fragments.size() - 1);
            if (fragment != null) {
                removeFragmentInPreference(fragment.getClass().getName());
            }
        }
    }

    private void removeFragmentInPreference(String fragmentName) {
        final Set<String> stringSet = fragmentPreference.getStringSet(ACTIVE_FRAGMENTS, new LinkedHashSet<String>());
        if (stringSet != null && !stringSet.isEmpty()) {
            stringSet.remove(fragmentName);
            fragmentPreference.edit().putStringSet(ACTIVE_FRAGMENTS, stringSet).apply();
        }
    }
}
