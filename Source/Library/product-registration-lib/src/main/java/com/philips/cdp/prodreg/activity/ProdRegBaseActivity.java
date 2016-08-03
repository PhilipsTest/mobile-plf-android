/**
 * ProdRegBaseActivity is the parent abstract class for Product Registration
 * Activity.
 *
 * @author: ritesh.jha@philips.com
 * @since: Dec 5, 2014
 * Copyright (c) 2016 Philips. All rights reserved.
 */
package com.philips.cdp.prodreg.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.launcher.FragmentLauncher;
import com.philips.cdp.prodreg.launcher.ProdRegConfig;
import com.philips.cdp.prodreg.launcher.ProdRegUiHelper;
import com.philips.cdp.prodreg.listener.ActionbarUpdateListener;
import com.philips.cdp.prodreg.listener.ProdRegBackListener;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.register.Product;
import com.philips.cdp.product_registration_lib.R;
import com.philips.cdp.registration.apptagging.AppTagging;
import com.philips.cdp.uikit.UiKitActivity;

import java.util.ArrayList;

public class ProdRegBaseActivity extends UiKitActivity {
    private static final String TAG = ProdRegBaseActivity.class.getSimpleName();
    private TextView mTitleTextView;
    private Handler mSiteCatListHandler = new Handler();

    private Runnable mPauseSiteCatalystRunnable = new Runnable() {

        @Override
        public void run() {
            AppTagging.pauseCollectingLifecycleData();
        }
    };

    private Runnable mResumeSiteCatalystRunnable = new Runnable() {

        @Override
        public void run() {
            AppTagging.collectLifecycleData(ProdRegBaseActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUiKitThemeIfRequired();
        initCustomActionBar();
        setContentView(R.layout.prodreg_activity);
        animateThisScreen();
        if (savedInstanceState == null) {
            showFragment();
        }
    }

    private void setUiKitThemeIfRequired() {
        final Bundle extras = getIntent().getExtras();
        int theme = extras.getInt(ProdRegConstants.UI_KIT_THEME);
        if (theme != 0)
            setTheme(theme);
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

    protected void showFragment() {
        try {
            boolean isFirstLaunch = false;
            ArrayList<Product> regProdList = null;
            final Bundle extras = getIntent().getExtras();
            if (extras != null) {
                isFirstLaunch = extras.getBoolean(ProdRegConstants.PROD_REG_IS_FIRST_LAUNCH);
                //noinspection unchecked
                regProdList = (ArrayList<Product>) extras.getSerializable(ProdRegConstants.MUL_PROD_REG_CONSTANT);
            }
            FragmentLauncher fragLauncher = new FragmentLauncher(
                    this, R.id.mainContainer, new ActionbarUpdateListener() {
                @Override
                public void updateActionbar(final String var1) {
                    setTitle(var1);
                }
            });
            fragLauncher.setAnimation(0, 0);
            final ProdRegUiHelper prodRegUiHelper = ProdRegUiHelper.getInstance();
            final ProdRegConfig prodRegConfig = new ProdRegConfig(regProdList, isFirstLaunch);
            prodRegUiHelper.invokeProductRegistration(fragLauncher, prodRegConfig, prodRegUiHelper.getProdRegUiListener());
        } catch (IllegalStateException e) {
            ProdRegLogger.e(TAG, e.getMessage());
        }
    }

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
        ActionBar mActionBar = this.getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayShowCustomEnabled(true);
            ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the text view in the ActionBar !
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER);
            View mCustomView = LayoutInflater.from(this).inflate(R.layout.prodreg_home_action_bar, null); // layout which contains your button.

            mTitleTextView = (TextView) mCustomView.findViewById(R.id.text);

            final FrameLayout frameLayout = (FrameLayout) mCustomView.findViewById(R.id.UpButton);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });
            ImageView arrowImage = (ImageView) mCustomView
                    .findViewById(R.id.arrow);
            //noinspection deprecation
            arrowImage.setBackground(getResources().getDrawable(R.drawable.prodreg_left_arrow));
            mActionBar.setCustomView(mCustomView, params);
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
        } else if (currentFrag != null && currentFrag instanceof ProdRegBackListener && !((ProdRegBackListener) currentFrag).onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState, final PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("retain_state", true);
    }

    @Override
    public void setTitle(final CharSequence title) {
        super.setTitle(title);
        mTitleTextView.setText(title);
    }
}
