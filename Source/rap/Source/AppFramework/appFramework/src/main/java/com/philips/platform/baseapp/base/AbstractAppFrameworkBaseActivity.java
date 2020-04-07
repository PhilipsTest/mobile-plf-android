/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.baseapp.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.WindowManager;

import com.philips.platform.appframework.BuildConfig;
import com.philips.platform.appframework.R;
import com.philips.platform.baseapp.screens.homefragment.HomeFragment;
import com.philips.platform.baseapp.screens.utility.BaseAppUtil;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.baseapp.screens.utility.OverlayDialogFragment;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.baseapp.screens.utility.SharedPreferenceUtility;
import com.philips.platform.themesettings.ThemeHelper;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ColorRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * AbstractAppFrameworkBaseActivity is the App level settings class for controlling the behavior of apps.
 */
public abstract class AbstractAppFrameworkBaseActivity extends  UIDActivity implements ActionBarListener {
    private static final String TAG = AbstractAppFrameworkBaseActivity.class.getName();

    public AbstractUIBasePresenter presenter;
    protected ContentColor contentColor;
    protected ColorRange colorRange;
    protected NavigationColor navigationColor;
    protected SharedPreferences defaultSharedPreferences;
    protected AccentRange accentColorRange;
    int containerId;
    private FragmentTransaction fragmentTransaction;
    private ProgressDialog progressDialog;

    public abstract int getContainerId();

    public AbstractAppFrameworkBaseActivity(){
        setLanguagePackNeeded(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTheme();
        if(BuildConfig.BUILD_TYPE=="psraRelease"){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
        super.onCreate(savedInstanceState);
        RALog.d(TAG,"App initalization status:"+AppFrameworkApplication.isAppDataInitialized());
        if(savedInstanceState!=null && !AppFrameworkApplication.isAppDataInitialized()){
            BaseAppUtil.restartApp(getApplicationContext());
            finish();
        }else{
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.RA_Settings_Progress_Title));
            progressDialog.setCancelable(false);
        }
    }

    protected void initTheme() {
        defaultSharedPreferences = new SharedPreferenceUtility(this).getMyPreferences();
        final ThemeConfiguration themeConfig = getThemeConfig();
        final int themeResourceId = getThemeResourceId(getResources(), getPackageName(), colorRange, contentColor);
        themeConfig.add(navigationColor);
        themeConfig.add(accentColorRange);
        UIDHelper.injectCalligraphyFonts();
        setTheme(themeResourceId);
        UIDHelper.init(themeConfig);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @StyleRes
    int getThemeResourceId(Resources resources, final String packageName, final ColorRange colorRange, final ContentColor contentColor) {
        final String themeName = String.format("Theme.DLS.%s.%s", colorRange.getThemeName(), contentColor.getThemeName());

        return resources.getIdentifier(themeName, "style", packageName);
    }

    public ThemeConfiguration getThemeConfig() {
        final ThemeHelper themeHelper = new ThemeHelper(defaultSharedPreferences, this);
        colorRange = themeHelper.initColorRange();
        navigationColor = themeHelper.initNavigationRange();
        contentColor = themeHelper.initContentTonalRange();
        accentColorRange = themeHelper.initAccentRange();
        return new ThemeConfiguration(this, colorRange, navigationColor, contentColor, accentColorRange);
    }

    public void handleFragmentBackStack(Fragment fragment, String fragmentTag, int fragmentAddState) {
        RALog.d(TAG, " handleFragmentBackStack called");
        containerId = getContainerId();
        try {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            switch (fragmentAddState) {
                case Constants.ADD_HOME_FRAGMENT:
                    RALog.d(TAG, " Added as ADD_HOME_FRAGMENT");
                    if (null == getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG)) {
                        addToBackStack(containerId, fragment, fragmentTag);
                    } else {
                        getSupportFragmentManager().popBackStackImmediate(HomeFragment.TAG, 0);
                    }

                    break;
                case Constants.ADD_FROM_HAMBURGER:
                    RALog.d(TAG, " Added as ADD_FROM_HAMBURGER");

                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    addToBackStack(containerId, new HomeFragment(), HomeFragment.TAG);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    addToBackStack(containerId, fragment, fragmentTag);

                    break;
                case Constants.CLEAR_TILL_HOME:
                    RALog.d(TAG, " Added as CLEAR_TILL_HOME");

                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    addToBackStack(containerId, new HomeFragment(), HomeFragment.TAG);

                    break;
                case Constants.ADD_FRAGMENT_WITH_BACKSTACK:
                    addToBackStack(containerId, fragment, fragmentTag);
                    break;
            }
        } catch (Exception e) {
            RALog.e(TAG, e.getMessage());
        }
    }

    public void addFragment(Fragment fragment, String fragmentTag) {
        RALog.d(TAG, " addFragment called");

        containerId = getContainerId();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerId, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void addToBackStack(int containerID, Fragment fragment, String fragmentTag) {
        RALog.d(TAG, " addToBackStack called");

        fragmentTransaction.replace(containerID, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();
    }


    public abstract void updateActionBarIcon(boolean b);

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*
        * Commenting out earlier implementation. Please find the reason below:
        * FragmentManager.getFragments can only be called from within the same
        * library group (groupId=com.android.support). This API has been
        * flagged with a restriction that has not been met. Examples of
        * API restrictions: * Method can only be invoked by a subclass
        * Method can only be accessed from within the same library
        * (defined by the Gradle library group id) .* Method can only
        * be accessed from tests. . You can add your own API restrictions
        * with the `@RestrictTo` annotation.
        *
        * List<Fragment> fragments = getSupportFragmentManager().getFragments();
        */
        ArrayList<Fragment> fragmentList = new ArrayList<>();

        for (int i = getSupportFragmentManager().getBackStackEntryCount() - 1; i >= 0; i--) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(i);
            String tag = backEntry.getName();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            fragmentList.add(fragment);
        }

        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment != null) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        RALog.d(TAG, " onResume called");
        if (((AppFrameworkApplication) getApplicationContext()).getAppInfra() != null) {
            startCollectingLifecycleData();
            AppFrameworkTagging.getInstance().getTagging().trackActionWithInfo("sendData", "appStatus", "ForeGround");
        }
    }

    public void startCollectingLifecycleData() {
        AppFrameworkTagging.getInstance().collectLifecycleData(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RALog.d(TAG, " onPause called");
        if (((AppFrameworkApplication) getApplicationContext()).getAppInfra() != null) {
            AppFrameworkTagging.getInstance().pauseCollectingLifecycleData();
            AppFrameworkTagging.getInstance().getTagging().trackActionWithInfo("sendData", "appStatus", "Background");
        }
    }

    public void showOverlayDialog(@StringRes int overlayTextId, int drawableId, String tag) {
        OverlayDialogFragment ratingDialogFragment = OverlayDialogFragment.newInstance(getString(overlayTextId), drawableId);
        ratingDialogFragment.show(getFragmentManager(), tag);
    }

    public void showProgressBar() {
        progressDialog.show();
    }

    public void hideProgressBar() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
