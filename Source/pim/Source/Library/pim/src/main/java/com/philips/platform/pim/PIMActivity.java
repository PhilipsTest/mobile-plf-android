package com.philips.platform.pim;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.listeners.UserLoginListener;
import com.philips.platform.pim.fragment.PIMFragment;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

public class PIMActivity extends UIDActivity implements ActionBarListener, UserLoginListener {
    private final int DEFAULT_THEME = R.style.Theme_DLS_Blue_UltraLight;
    private final String TAG = PIMActivity.class.getSimpleName();
    private LoggingInterface mLoggingInterface;
    private UserLoginListener mUserLoginListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim);

        mUserLoginListener = PIMSettingManager.getInstance().getPimUserLoginListener();
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
        launchASFragment();
    }

    @Override
    public void updateActionBar(int resId, boolean enableBackKey) {
        //NOP
    }

    @Override
    public void updateActionBar(String resString, boolean enableBackKey) {
        //NOP
    }

    private void initTheme() {
        int themeIndex = getIntent().getIntExtra(PIMInterface.PIM_KEY_ACTIVITY_THEME, DEFAULT_THEME);
        if (themeIndex <= 0) {
            themeIndex = DEFAULT_THEME;
        }
        getTheme().applyStyle(themeIndex, true);
        UIDHelper.init(new ThemeConfiguration(this, ContentColor.ULTRA_LIGHT, NavigationColor.BRIGHT, AccentRange.ORANGE));
    }

    private void launchASFragment() {
        PIMFragment pimFragment = new PIMFragment();
        pimFragment.setActionbarListener(this, this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_mainFragmentContainer,
                        pimFragment, PIMFragment.class.getSimpleName()).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFrag = fragmentManager.findFragmentById(R.id.fl_mainFragmentContainer);
        boolean backState = false;
        if (currentFrag instanceof BackEventListener) {
            backState = ((BackEventListener) currentFrag).handleBackEvent();
        }
        if (!backState) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            this.finish();
        }
    }

    @Override
    public void onLoginSuccess() {
        mUserLoginListener.onLoginSuccess();
        mLoggingInterface.log(DEBUG, TAG, "Login Success");
        this.finish();
    }

    @Override
    public void onLoginFailed(Error error) {
        mUserLoginListener.onLoginFailed(error);
        mLoggingInterface.log(DEBUG, TAG, "Login Failed : Code " + error.getErrCode() + "and error description " + error.getErrDesc());
        this.finish();
    }
}
