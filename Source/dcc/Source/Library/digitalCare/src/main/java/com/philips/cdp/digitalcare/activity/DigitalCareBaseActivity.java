/**
 * DigitalCareBaseActivity is the parent abstract class for DigitalCare
 * Activity.
 *
 * @author: ritesh.jha@philips.com
 * @since: Dec 5, 2014
 * Copyright (c) 2016 Philips. All rights reserved.
 */
package com.philips.cdp.digitalcare.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.listeners.ActivityTitleListener;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.digitalcare.util.DigitalCareConstants;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;

//import com.philips.cdp.productselection.launchertype.ActivityLauncher;
//import com.philips.cdp.productselection.launchertype.UiLauncher;


public abstract class DigitalCareBaseActivity extends UIDActivity implements ActivityTitleListener{
    private static String TAG = DigitalCareBaseActivity.class.getSimpleName();

    protected FragmentManager fragmentManager = null;
    protected DigitalCareConfigManager mDigitalCareConfigManager = null;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        UiLauncher uiLauncher = DigitalCareConfigManager.getInstance().getUiLauncher();
        if (uiLauncher instanceof ActivityLauncher) {
            ActivityLauncher activityLauncher = (ActivityLauncher) uiLauncher;
            this.setTheme(activityLauncher.getUiKitTheme());
        }
        DigitalCareConfigManager.getInstance();
        fragmentManager = getSupportFragmentManager();
    }

    protected void initActionBar() throws ClassCastException {
        UIDHelper.setupToolbar(this);
        toolbar = (Toolbar) findViewById(R.id.uid_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_icon);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backstackFragment();
                return true;
            default:
                break;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        DigitalCareConfigManager.getInstance().getTaggingInterface().collectLifecycleInfo(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DigitalCareConfigManager.getInstance().getTaggingInterface().pauseLifecycleInfo();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return backstackFragment();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mDigitalCareConfigManager != null) {
            mDigitalCareConfigManager = null;
        }
    }

    private boolean backstackFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            fragmentManager.popBackStack();
            removeCurrentFragment();
        }
        return true;
    }

    private void removeCurrentFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment currentFrag = fragmentManager
                .findFragmentById(R.id.mainContainer);

        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }
        transaction.commit();
    }

    protected void showFragment(Fragment fragment) {
        try {
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();

            fragmentTransaction.replace(R.id.mainContainer, fragment, DigitalCareConstants.DIGITALCARE_FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            DigiCareLogger.e(TAG, e.getMessage());
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow() != null && getWindow().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    @Override
    public void setTitle(String title) {
        TextView actionBarTitle =

                ((TextView) findViewById(
                        R.id.action_bar_title));
        actionBarTitle.setText(title);
    }

}
