/*
 * Copyright (c) Koninklijke Philips N.V. 2017
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */
package com.philips.platform.mya.activity;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.platform.mya.MyaHelper;
import com.philips.platform.mya.R;
import com.philips.platform.mya.tabs.MyaTabFragment;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.philips.platform.mya.launcher.MyaInterface.USER_PLUGIN;


public class MyaActivity extends UIDActivity {

    private TextView mTitle;
    private ImageView leftImageView;
    public static final String MYA_DLS_THEME= "Mya_dls_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIDHelper.injectCalligraphyFonts();
        initDLSThemeIfExists();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mya_myaccounts_activity);

        Toolbar toolbar = findViewById(R.id.mya_toolbar);
        mTitle = toolbar.findViewById(R.id.mya_toolbar_title);
        leftImageView = toolbar.findViewById(R.id.mya_toolbar_left_image);
        View leftImageViewLayout = toolbar.findViewById(R.id.mya_toolbar_left_image_layout);
        leftImageViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            launchTabFragment();
        }
    }


    private void handleLeftImage(boolean shouldBackEnable) {
        if (!shouldBackEnable) {
            setLeftImage(R.drawable.mya_cross_icon);
        } else {
            setLeftImage(R.drawable.mya_back_icon);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        mTitle.setText(titleId);
    }

    public void initDLSThemeIfExists() {
        ThemeConfiguration themeConfiguration = MyaHelper.getInstance().getThemeConfiguration();
        if (getIntent().getExtras() != null && themeConfiguration != null) {
            Bundle extras = getIntent().getExtras();
            UIDHelper.init(themeConfiguration);
            getTheme().applyStyle(extras.getInt(MYA_DLS_THEME), true);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean backState = false;
        Fragment currentFrag = fragmentManager
                .findFragmentById(R.id.mainContainer);
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            if (currentFrag != null && currentFrag instanceof BackEventListener) {
                backState = ((BackEventListener) currentFrag).handleBackEvent();
            }

            if (!backState) {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void setLeftImage(int resId) {
        if (resId != 0) {
            leftImageView.setBackgroundResource(resId);
        }
    }

    private void launchTabFragment() {

        FragmentLauncher fragmentLauncher = new FragmentLauncher(this, R.id.mainContainer, new ActionBarListener() {
            @Override
            public void updateActionBar(int i, boolean shouldBackEnable) {
                setTitle(i);
                handleLeftImage(shouldBackEnable);
            }

            @Override
            public void updateActionBar(String s, boolean shouldBackEnable) {
                setTitle(s);
                handleLeftImage(shouldBackEnable);
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putSerializable(USER_PLUGIN, MyaHelper.getInstance().getUserDataInterface());
        MyaTabFragment myaTabFragment = new MyaTabFragment();
        myaTabFragment.setFragmentLauncher(fragmentLauncher);
        myaTabFragment.setActionbarUpdateListener(fragmentLauncher.getActionbarListener());
        myaTabFragment.setArguments(bundle);
        myaTabFragment.showFragment(myaTabFragment);
    }


}
