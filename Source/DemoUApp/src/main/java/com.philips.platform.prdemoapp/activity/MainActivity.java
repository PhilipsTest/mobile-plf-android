package com.philips.platform.prdemoapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.uikit.UiKitActivity;
import com.philips.platform.prdemoapp.PRDemoAppuAppInterface;
import com.philips.platform.prdemoapp.fragment.LaunchFragment;
import com.philips.platform.prdemoapp.utils.ThemeHelper;
import com.philips.platform.prdemoapplibrary.R;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;
import com.shamanland.fonticon.FontIconTypefaceHolder;

public class MainActivity extends UIDActivity {

    private FragmentManager fragmentManager;
    private TextView mTitleTextView;
    private Handler mSiteCatListHandler = new Handler();
    private ThemeHelper themeHelper;

    private Runnable mPauseSiteCatalystRunnable = new Runnable() {

        @Override
        public void run() {
            AppTagging.pauseCollectingLifecycleData();
        }
    };

    private Runnable mResumeSiteCatalystRunnable = new Runnable() {

        @Override
        public void run() {
            AppTagging.collectLifecycleData(MainActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // initTheme();
        setTheme(PRDemoAppuAppInterface.DLS_THEME);
        UIDHelper.init(PRDemoAppuAppInterface.THEME_CONFIGURATION);
        super.onCreate(savedInstanceState);
        initCustomActionBar();
        setContentView(R.layout.activity_test_ur);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            LaunchFragment launchFragment = new LaunchFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.parent_layout, launchFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
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

    private void initCustomActionBar() {
        ActionBar mActionBar = this.getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the text view in the ActionBar !
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        View mCustomView = LayoutInflater.from(this).inflate(R.layout.custom_action_bar, null); // layout which contains your button.

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
        arrowImage.setBackground(getResources().getDrawable(R.drawable.prodreg_left_arrow));

        mActionBar.setCustomView(mCustomView, params);
        setTitle(getString(R.string.app_name));
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean backState = false;
        Fragment currentFrag = fragmentManager
                .findFragmentById(R.id.parent_layout);
        if (currentFrag != null && currentFrag instanceof BackEventListener) {
            backState = ((BackEventListener) currentFrag).handleBackEvent();
        }

        if (!backState) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState, final PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("test", true);
    }

    @Override
    public void setTitle(final CharSequence title) {
        super.setTitle(title);
        mTitleTextView.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        if (mTitleTextView != null)
            mTitleTextView.setText(titleId);
        else
            super.setTitle(titleId);
    }

    protected  void initTheme(){
        UIDHelper.injectCalligraphyFonts();
        themeHelper = new ThemeHelper(this);
        ThemeConfiguration config = themeHelper.getThemeConfig();
        setTheme(themeHelper.getThemeResourceId());
        UIDHelper.init(config);
        FontIconTypefaceHolder.init(getAssets(),"digitalcarefonts/CCIcon.ttf");
    }

    protected void changeTheme(){
        themeHelper.changeTheme();
    }
}
