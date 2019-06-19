/**
 * ProdRegBaseActivity is the parent abstract class for Product Registration
 * Activity.
 *
 * @author: ritesh.jha@philips.com
 * @since: Dec 5, 2014
 * Copyright (c) 2016 Philips. All rights reserved.
 */
package com.philips.cdp.prodreg.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.launcher.PRInterface;
import com.philips.cdp.prodreg.launcher.PRLaunchInput;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.register.Product;
import com.philips.cdp.prodreg.tagging.ProdRegTagging;
import com.philips.cdp.product_registration_lib.R;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;
import com.philips.platform.uid.view.widget.ActionBarTextView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_FIRST_REG_BTN_TEXT;

public class ProdRegBaseActivity extends UIDActivity {
    private static final String TAG = ProdRegBaseActivity.class.getSimpleName();
    private final int DEFAULT_THEME = R.style.Theme_DLS_GroupBlue_UltraLight;
    private Handler mSiteCatListHandler = new Handler();
    private Toolbar mToolbar;
    private ActionBarTextView mActionBarTextView;

    private Runnable mPauseSiteCatalystRunnable = new Runnable() {

        @Override
        public void run() {
            ProdRegTagging.pauseCollectingLifecycleData();
        }
    };

    private Runnable mResumeSiteCatalystRunnable = new Runnable() {

        @Override
        public void run() {
            ProdRegTagging.collectLifecycleData(ProdRegBaseActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIDHelper.injectCalligraphyFonts();
        if (PRUiHelper.getInstance().getTheme() != 0) {
            setTheme(PRUiHelper.getInstance().getTheme());
        } else {
            getTheme().applyStyle(DEFAULT_THEME, true);
        }

        if (PRUiHelper.getInstance().getThemeConfiguration() != null) {
            UIDHelper.init(PRUiHelper.getInstance().getThemeConfiguration());
        }

        setContentView(R.layout.prodreg_activity);
        mToolbar = (Toolbar) findViewById(R.id.uid_toolbar);
        mActionBarTextView = (ActionBarTextView) findViewById(R.id.uid_toolbar_title);
        initCustomActionBar();
        animateThisScreen();
        if (savedInstanceState == null) {
            showFragment();
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onPause() {
        mSiteCatListHandler.post(mPauseSiteCatalystRunnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mSiteCatListHandler.post(mResumeSiteCatalystRunnable);
        super.onResume();
    }

    @SuppressWarnings({"unchecked", "serial"})
    protected void showFragment() {
        try {
            boolean isFirstLaunch = false;
            boolean isWarrantyMandatory = false;
            String  warrantyMandatoryBtnTxt="";
            int imageResID = 0;
            ArrayList<Product> regProdList = null;
            final Bundle extras = getIntent().getExtras();
            if (extras != null) {
                isFirstLaunch = extras.getBoolean(ProdRegConstants.PROD_REG_IS_FIRST_LAUNCH);
                regProdList = (ArrayList<Product>) extras.getSerializable(ProdRegConstants.MUL_PROD_REG_CONSTANT);
                imageResID = extras.getInt(ProdRegConstants.PROD_REG_FIRST_IMAGE_ID);
                isWarrantyMandatory = extras.getBoolean(ProdRegConstants.PROD_REG_FIRST_NO_THANKS_BTN_VISIBLE);
                warrantyMandatoryBtnTxt= extras.getString(PROD_REG_FIRST_REG_BTN_TEXT);

            }
            FragmentLauncher fragLauncher = new FragmentLauncher(
                    this, R.id.mainContainer, new ActionBarListener() {
                @Override
                public void updateActionBar(@StringRes final int resId, final boolean enableBack) {
                    setTitle(resId);
                }

                @Override
                public void updateActionBar(final String s, final boolean b) {

                }
            });
            fragLauncher.setCustomAnimation(0, 0);
            final PRUiHelper prUiHelper = PRUiHelper.getInstance();
            final PRLaunchInput prLaunchInput = new PRLaunchInput(regProdList, isFirstLaunch);
            prLaunchInput.setProdRegUiListener(prUiHelper.getProdRegUiListener());
            prLaunchInput.setBackgroundImageResourceId(imageResID);
            prLaunchInput.setMandatoryProductRegistration(isWarrantyMandatory);
            prLaunchInput.setMandatoryRegisterButtonText(warrantyMandatoryBtnTxt);
            new PRInterface().launch(fragLauncher, prLaunchInput);
        } catch (IllegalStateException e) {
            ProdRegLogger.e(TAG, e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    private void animateThisScreen() {
        Bundle bundleExtras = getIntent().getExtras();
        int startAnimation = bundleExtras.getInt(ProdRegConstants.START_ANIMATION_ID);
        int endAnimation = bundleExtras.getInt(ProdRegConstants.STOP_ANIMATION_ID);
        int orientation = bundleExtras.getInt(ProdRegConstants.SCREEN_ORIENTATION);

        if (startAnimation == 0 && endAnimation == 0) {
            return;
        }

        final String startAnim = getResources().getResourceName(startAnimation);
        final String endAnim = getResources().getResourceName(endAnimation);

        String packageName = getPackageName();
        final int mEnterAnimation = getApplicationContext().getResources().getIdentifier(startAnim,
                "anim", packageName);
        final int mExitAnimation = getApplicationContext().getResources().getIdentifier(endAnim, "anim",
                packageName);
        overridePendingTransition(mEnterAnimation, mExitAnimation);
        //noinspection WrongConstant
        setRequestedOrientation(orientation);
    }

    private void initCustomActionBar() {
        setSupportActionBar(mToolbar);
        ActionBar mActionBar = this.getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.prodreg_left_arrow);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            setTitle(getString(R.string.app_name));
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFrag = fragmentManager
                .findFragmentById(R.id.mainContainer);
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else if (currentFrag != null && currentFrag instanceof BackEventListener && !((BackEventListener) currentFrag).handleBackEvent()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState, final PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("retain_state", true);
    }

    @Override
    public void setTitle(int titleId) {
        if (mActionBarTextView != null)
            mActionBarTextView.setText(titleId);
        else
            super.setTitle(titleId);
    }
}
