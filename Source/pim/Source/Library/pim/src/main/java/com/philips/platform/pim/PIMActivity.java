package com.philips.platform.pim;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import java.util.HashMap;

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
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bundle.get(PIMInterface.PIM_KEY_CONSENTS);
            launchASFragment(bundle);
        }
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

    private void launchASFragment(Bundle bundle) {
        PIMFragment pimFragment = new PIMFragment();
        pimFragment.setActionbarListener(this, this);
        HashMap consentParameterMap = (HashMap) bundle.get(PIMInterface.PIM_KEY_CONSENTS);
        bundle.putSerializable(PIMInterface.PIM_KEY_CONSENTS, consentParameterMap);
        pimFragment.setArguments(bundle);
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
