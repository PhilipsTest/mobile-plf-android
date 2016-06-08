package com.philips.cdp.appframework;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.cdp.appframework.utility.Constants;
import com.philips.cdp.appframework.utility.Logger;
import com.philips.cdp.productselection.utils.ProductSelectionLogger;
import com.philips.cdp.tagging.Tagging;
import com.philips.cdp.uikit.UiKitActivity;
import com.philips.cdp.uikit.drawable.VectorDrawable;
/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/

/*
* This is the base Activity. Each activity has to extend this base activity in order to
* achieve UIKit theme and common utility.
*
* @author : Ritesh.jha@philips.com
* @since : 8 June 2016
*/

public abstract class AppFrameworkBaseActivity extends UiKitActivity {
    public static final String SHARED_PREFERENCES = "SharedPref";
    public static final String DONE_PRESSED = "donePressed";
    private static String TAG = AppFrameworkBaseActivity.class.getSimpleName();
    private FragmentManager fragmentManager = null;
    private SharedPreferences mSharedpreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ProductSelectionLogger.i(Constants.ACTIVITY, "onCreate");
        fragmentManager = getSupportFragmentManager();

        // initActionBar();
    }

    private void initActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.actionbar_productselection, null); // layout which contains your button.

        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.text);

        FrameLayout frameLayout = (FrameLayout) mCustomView.findViewById(R.id.productselection_UpButton);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                backstackFragment();
            }
        });

        ImageView arrowImage = (ImageView) mCustomView
                .findViewById(R.id.productselection_arrow);
        arrowImage.setImageDrawable(VectorDrawable.create(this, R.drawable.uikit_up_arrow));
        arrowImage.bringToFront();
        mActionBar.setCustomView(mCustomView, params);
        mActionBar.setDisplayShowCustomEnabled(true);

        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Logger.i(Constants.ACTIVITY, " onConfigurationChanged ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i(Constants.ACTIVITY, " onResume ");
        Tagging.collectLifecycleData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i(Constants.ACTIVITY, " onPause ");
        Tagging.pauseCollectingLifecycleData();
    }

  /*  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return backstackFragment();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(Constants.ACTIVITY, "onDestroy ");
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

    protected void setIntroScreenDonePressed() {
        mSharedpreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedpreferences.edit();
        editor.putBoolean(DONE_PRESSED, true);
        editor.commit();
    }

    protected Boolean getIntroScreenDonePressed() {
        mSharedpreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return mSharedpreferences.getBoolean(DONE_PRESSED, false);
    }

    protected void showFragment(Fragment fragment) {
        try {
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();

            fragmentTransaction.replace(R.id.mainContainer, fragment, "tagname");
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            ProductSelectionLogger.e(TAG, e.getMessage());
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow() != null && getWindow().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }
}
