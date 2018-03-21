/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegistrationActivity extends UIDActivity implements
        ActionBarListener {

    private RegistrationLaunchMode mRegistrationLaunchMode;
    private RegistrationContentConfiguration registrationContentConfiguration;

    private UIFlow uiFlow;
    private Handler mSiteCatalistHandler = new Handler();
    private Runnable mPauseSiteCatalystRunnable = new Runnable() {

        @Override
        public void run() {
            AppTagging.pauseCollectingLifecycleData();
        }
    };

    private Runnable mResumeSiteCatalystRunnable = new Runnable() {

        @Override
        public void run() {
            AppTagging.collectLifecycleData(RegistrationActivity.this);
        }
    };

    private Toolbar toolbar;
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (RegistrationHelper.getInstance().getThemeConfiguration() != null) {
            UIDHelper.init(RegistrationHelper.getInstance().getThemeConfiguration());
        }
        if (RegistrationHelper.getInstance().getTheme() != 0) {
            setTheme(RegistrationHelper.getInstance().getTheme());
        }
        super.onCreate(savedInstanceState);

        //Handle launch by dynamic permission change
        if (savedInstanceState != null) {
            RegUtility.handleDynamicPermissionChange(this);
            return;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            mRegistrationLaunchMode = (RegistrationLaunchMode) bundle.get(RegConstants.REGISTRATION_LAUNCH_MODE);
            int orientation = bundle.getInt(RegConstants.ORIENTAION, -1);
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            setRegistrationContentConfiguration((RegistrationContentConfiguration) bundle.get(RegConstants.REGISTRATION_CONTENT_CONFIG));

            uiFlow = (UIFlow) bundle.get(RegConstants.REGISTRATION_UI_FLOW);

        }

        int alwaysFinishActivity = 0;
        try {
            alwaysFinishActivity = savedInstanceState.getInt("ALWAYS_FINISH_ACTIVITIES");
        } catch (NullPointerException e) {
        }

        setContentView(R.layout.reg_activity_registration_dls);

        mTvTitle = (TextView) findViewById(R.id.uid_toolbar_title);

        toolbar = findViewById(R.id.uid_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.reg_ic_cross_icon);

        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (alwaysFinishActivity == 0) {
            initUI();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        RLog.d("Exception ", " RegistrationActivity protected onSaveInstanceState");
        @SuppressWarnings("deprecation") int alwaysFinishActivity = Settings.System.
                getInt(getContentResolver(),
                        Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
        bundle.putInt("ALWAYS_FINISH_ACTIVITIES", alwaysFinishActivity);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        @SuppressWarnings("deprecation") int alwaysFinishActivity = Settings.System.
                getInt(getContentResolver(),
                        Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
        savedInstanceState.putInt("ALWAYS_FINISH_ACTIVITIES", alwaysFinishActivity);
    }

    @Override
    protected void onResume() {
        mSiteCatalistHandler.removeCallbacksAndMessages(null);
        mSiteCatalistHandler.post(mResumeSiteCatalystRunnable);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSiteCatalistHandler.removeCallbacksAndMessages(null);
        mSiteCatalistHandler.post(mPauseSiteCatalystRunnable);
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager
                .findFragmentById(R.id.frame_container);
        if (fragment != null && fragment instanceof BackEventListener) {
            boolean isConsumed = ((BackEventListener) fragment).handleBackEvent();
            if (isConsumed)
                return;

            super.onBackPressed();
        }


    }

    private void initUI() {
        launchRegistrationFragment(mRegistrationLaunchMode);
    }

    private void launchRegistrationFragment(RegistrationLaunchMode registrationLaunchMode) {
        URLaunchInput urLaunchInput = new URLaunchInput();
        urLaunchInput.setEndPointScreen(registrationLaunchMode);
        urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);
        urLaunchInput.setRegistrationContentConfiguration(getRegistrationContentConfiguration());
        urLaunchInput.setUserRegistrationUIEventListener(RegistrationConfiguration.getInstance().getUserRegistrationUIEventListener());
        urLaunchInput.setUIFlow(uiFlow);
        FragmentLauncher fragmentLauncher = new FragmentLauncher
                (this, R.id.frame_container, this);
        URInterface urInterface = new URInterface();
        urInterface.launch(fragmentLauncher, urLaunchInput);
    }

    @Override
    public void updateActionBar(int titleResourceID, boolean isShowBack) {
        if (titleResourceID == R.string.reg_DLS_StratScreen_Nav_Title_Txt) {
            toolbar.setNavigationIcon(R.drawable.reg_ic_cross_icon);
            isShowBack = true;
        } else {
            toolbar.setNavigationIcon(R.drawable.reg_back_icon);
        }

        if (isShowBack) {
            mTvTitle.setText(getResources().getString(titleResourceID));
        } else {
            toolbar.setNavigationIcon(null);
            mTvTitle.setText(getResources().getString(titleResourceID));
        }
    }

    @Override
    public void updateActionBar(String titleResourceText, boolean isShowBack) {

        mTvTitle.setText(titleResourceText);
        if (getString(R.string.reg_DLS_StratScreen_Nav_Title_Txt).equals(titleResourceText)) {
            toolbar.setNavigationIcon(R.drawable.reg_ic_cross_icon);
            isShowBack = true;
        } else {
            toolbar.setNavigationIcon(R.drawable.reg_back_icon);
        }

        if (!isShowBack) {
            toolbar.setNavigationIcon(null);
        }
    }


    private void setRegistrationContentConfiguration(RegistrationContentConfiguration regContentConfig) {
        registrationContentConfiguration = regContentConfig;
    }

    private RegistrationContentConfiguration getRegistrationContentConfiguration() {

        return registrationContentConfiguration;
    }


}
