/**
 * DigitalCareBaseActivity is the parent abstract class for DigitalCare
 * Activity.
 *
 * @author: ritesh.jha@philips.com
 * @since: Dec 5, 2014
 * Copyright (c) 2016 Philips. All rights reserved.
 */
package com.philips.cdp.prodreg.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cdp.prodreg.fragments.InitialFragment;
import com.philips.cdp.prodreg.util.ProdRegConfigManager;
import com.philips.cdp.prodreg.util.ProdRegConstants;
import com.philips.cdp.product_registration_lib.R;

public abstract class ProdRegBaseActivity extends FragmentActivity {
    private static String TAG = ProdRegBaseActivity.class.getSimpleName();

    private RelativeLayout mActionBarlayout = null;
    private ImageView mActionBarMenuIcon = null;
    private ImageView mActionBarArrow = null;
    private TextView mActionBarTitle = null;
    private FragmentManager fragmentManager = null;
    private ProdRegConfigManager prodRegConfigManager = null;
    private OnClickListener actionBarClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            int _id = view.getId();
            if (_id == R.id.action_bar_icon_parent) {
                if (mActionBarMenuIcon.getVisibility() == View.VISIBLE)
                    finish();
                else if (mActionBarArrow.getVisibility() == View.VISIBLE)
                    backStackFragment();
            } else if (_id == R.id.home_icon) {
                finish();
            } else if (_id == R.id.back_to_home_img)
                backStackFragment();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ProdRegConfigManager.getInstance();
        fragmentManager = getSupportFragmentManager();

        try {
            initActionBar();
        } catch (ClassCastException e) {
            Log.e(TAG, "Actionbar: " + e.getMessage());
        }
        animateThisScreen();
        showFragment(new InitialFragment());
        enableActionBarHome();
    }

    protected void initActionBar() throws ClassCastException {
        mActionBarlayout = (RelativeLayout) findViewById(R.id.action_bar_icon_parent);
        mActionBarMenuIcon = (ImageView) findViewById(R.id.home_icon);
        mActionBarArrow = (ImageView) findViewById(R.id.back_to_home_img);
        mActionBarTitle = (TextView) findViewById(R.id.action_bar_title);

        mActionBarMenuIcon.setOnClickListener(actionBarClickListener);
        mActionBarArrow.setOnClickListener(actionBarClickListener);
        mActionBarlayout.setOnClickListener(actionBarClickListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        AnalyticsTracker.startCollectLifecycleData();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        AnalyticsTracker.stopCollectLifecycleData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return backStackFragment();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (prodRegConfigManager != null) {
            prodRegConfigManager = null;
        }
    }

    private boolean backStackFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
//            enableActionBarHome();
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

    private void enableActionBarLeftArrow() {
        mActionBarMenuIcon.setVisibility(View.GONE);
        mActionBarArrow.setVisibility(View.VISIBLE);
        mActionBarArrow.bringToFront();
    }

    protected void enableActionBarHome() {
        mActionBarMenuIcon.setVisibility(View.VISIBLE);
        mActionBarMenuIcon.bringToFront();
        mActionBarArrow.setVisibility(View.GONE);
        mActionBarTitle.setText(getResources().getString(
                R.string.actionbar_title_support));
    }

    protected void showFragment(Fragment fragment) {
        try {
            enableActionBarLeftArrow();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();

            fragmentTransaction.replace(R.id.mainContainer, fragment, ProdRegConstants.PROD_REG_FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow() != null && getWindow().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    private void animateThisScreen() {
        Bundle bundleExtras = getIntent().getExtras();

        String startAnim = null;
        String endAnim = null;

        int startAnimation = bundleExtras.getInt(ProdRegConstants.START_ANIMATION_ID);
        int endAnimation = bundleExtras.getInt(ProdRegConstants.STOP_ANIMATION_ID);
        int orientation = bundleExtras.getInt(ProdRegConstants.SCREEN_ORIENTATION);

        if (startAnimation == 0 && endAnimation == 0) {
            return;
        }

        startAnim = getResources().getResourceName(startAnimation);
        endAnim = getResources().getResourceName(endAnimation);

        String packageName = getPackageName();
        final int mEnterAnimation = getApplicationContext().getResources().getIdentifier(startAnim,
                "anim", packageName);
        final int mExitAnimation = getApplicationContext().getResources().getIdentifier(endAnim, "anim",
                packageName);
        setRequestedOrientation(orientation);
        overridePendingTransition(mEnterAnimation, mExitAnimation);
    }
}
