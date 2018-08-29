/*
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */

package com.philips.platform.ews;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.philips.platform.ews.base.BaseFragment;
import com.philips.platform.ews.configuration.ContentConfiguration;
import com.philips.platform.ews.injections.DaggerEWSComponent;
import com.philips.platform.ews.injections.DependencyHelper;
import com.philips.platform.ews.injections.EWSConfigurationModule;
import com.philips.platform.ews.injections.EWSDependencyProviderModule;
import com.philips.platform.ews.injections.EWSModule;
import com.philips.platform.ews.microapp.EWSActionBarListener;
import com.philips.platform.ews.microapp.EwsResultListener;
import com.philips.platform.ews.navigation.Navigator;
import com.philips.platform.ews.tagging.EWSTagger;
import com.philips.platform.ews.util.BundleUtils;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.view.widget.ActionBarTextView;

import java.util.concurrent.TimeUnit;

public class EWSActivity extends DynamicThemeApplyingActivity implements EWSActionBarListener, EwsResultListener {

    public static final long DEVICE_CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    public static final String EWS_STEPS = "EWS_STEPS";
    public static final String KEY_CONTENT_CONFIGURATION = "contentConfiguration";
    @NonNull
    EWSTagger ewsTagger;
    @NonNull
    private Navigator navigator;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!DependencyHelper.areDependenciesInitialized()) {
            this.finish();
            return;
        }
        setContentView(R.layout.ews_activity_main);
        initEWSComponent(getBundle(savedInstanceState));
        setUpToolBar();
        findViewById(R.id.ic_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCancelButtonClicked();
            }
        });

        if (savedInstanceState == null) {
            navigator.navigateToGettingStartedScreen();
        }
    }

    private void initEWSComponent(@Nullable Bundle savedInstanceState) {
        ContentConfiguration contentConfiguration =
                BundleUtils.extractParcelableFromIntentOrNull(savedInstanceState, KEY_CONTENT_CONFIGURATION);

        if (contentConfiguration == null) {
            contentConfiguration = new ContentConfiguration();
        }

        EWSModule ewsModule = new EWSModule(this
                , this.getSupportFragmentManager()
                , R.id.contentFrame, DependencyHelper.getCommCentral());
        EWSDependencyProviderModule ewsDependencyProviderModule = new EWSDependencyProviderModule(DependencyHelper.getAppInfraInterface(), DependencyHelper.getProductKeyMap());
        EWSConfigurationModule ewsConfigurationModule = new EWSConfigurationModule(this, contentConfiguration);

        DaggerEWSComponent.builder()
                .eWSModule(ewsModule)
                .eWSConfigurationModule(ewsConfigurationModule)
                .eWSDependencyProviderModule(ewsDependencyProviderModule)
                .build();
        navigator = ewsModule.provideNavigator();
        ewsTagger = ewsDependencyProviderModule.provideEWSTagger();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ContentConfiguration contentConfiguration =
                BundleUtils.extractParcelableFromIntentOrNull(getIntent().getExtras(), KEY_CONTENT_CONFIGURATION);
        outState.putParcelable(KEY_CONTENT_CONFIGURATION, contentConfiguration);
    }

    private Bundle getBundle(@Nullable Bundle savedInstanceState) {
        Bundle bundle;
        if (savedInstanceState == null) {
            bundle = getIntent().getExtras();
        } else {
            bundle = savedInstanceState;
        }
        return bundle;
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.ews_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!wasBackHandledByCurrentlyDisplayedFragment()) {
            if (shouldFinish()) {
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    private boolean shouldFinish() {
        return getSupportFragmentManager().getBackStackEntryCount() == 1;
    }

    private boolean wasBackHandledByCurrentlyDisplayedFragment() {
        boolean backHandledByFragment = false;
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (currentFragment instanceof BackEventListener) {
            BackEventListener backEventListener = (BackEventListener) currentFragment;
            backHandledByFragment = backEventListener.handleBackEvent();
        }
        return backHandledByFragment;
    }

    protected void handleCancelButtonClicked() {
        BaseFragment baseFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        baseFragment.handleCancelButtonClicked();
    }

    public void setToolbarTitle(String s) {
        Toolbar toolbar = findViewById(R.id.ews_toolbar);
        ((ActionBarTextView) toolbar.findViewById(R.id.toolbar_title)).setText(s);
    }

    @Override
    public void closeButton(boolean visibility) {
        findViewById(R.id.ic_close).setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateActionBar(int i, boolean b) {
        setToolbarTitle(getString(i));
    }

    @Override
    public void updateActionBar(String s, boolean b) {
        setToolbarTitle(s);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ewsTagger.collectLifecycleInfo(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ewsTagger.pauseLifecycleInfo();
    }

    @Override
    public void onEWSFinishSuccess() {
        setResult(EWS_RESULT_SUCCESS, null);
        finish();
    }

    @Override
    public void onEWSError(int errorCode) {
        Intent failureIntent = new Intent();
        failureIntent.putExtra(EWS_RESULT_FAILURE_DATA, errorCode);
        setResult(EWS_RESULT_FAILURE, failureIntent);
        finish();
    }
}
