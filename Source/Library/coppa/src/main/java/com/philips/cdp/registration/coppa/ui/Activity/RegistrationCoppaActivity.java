
package com.philips.cdp.registration.coppa.ui.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.philips.cdp.registration.coppa.R;
import com.philips.cdp.registration.coppa.ui.fragment.RegistrationCoppaFragment;
import com.philips.cdp.registration.coppa.utils.RegistrationCoppaLaunchHelper;
import com.philips.cdp.registration.listener.RegistrationTitleBarListener;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.tagging.Tagging;

public class RegistrationCoppaActivity extends FragmentActivity implements OnClickListener,
        RegistrationTitleBarListener {

    private  boolean isAccountSettings = true;

    private TextView ivBack;
    private Handler mSiteCatalistHandler = new Handler();
    private Runnable mPauseSiteCatalystRunnable = new Runnable() {

        @Override
        public void run() {
            Tagging.pauseCollectingLifecycleData();
        }
    };

    private Runnable mResumeSiteCatalystRunnable = new Runnable() {

        @Override
        public void run() {
            Tagging.collectLifecycleData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isAccountSettings = bundle.getBoolean(RegConstants.ACCOUNT_SETTINGS,true);
            int orientation = bundle.getInt(RegConstants.ORIENTAION,-1);
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }

        setContentView(R.layout.activity_reg_coppa_registration);
        RLog.i(RLog.EVENT_LISTENERS, "RegistrationCoppaActivity  Register: NetworStateListener");
        initUI();
    }

    @Override
    protected void onStart() {
        RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationCoppaActivity : onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationCoppaActivity : onResume");
        mSiteCatalistHandler.removeCallbacksAndMessages(null);
        mSiteCatalistHandler.post(mResumeSiteCatalystRunnable);
        super.onResume();
    }

    @Override
    protected void onPause() {
        RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationCoppaActivity : onPause");
        mSiteCatalistHandler.removeCallbacksAndMessages(null);
        mSiteCatalistHandler.post(mPauseSiteCatalystRunnable);
        super.onPause();
    }

    @Override
    protected void onStop() {
        RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationCoppaActivity : onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationCoppaActivity : onDestroy");
        RLog.i(RLog.EVENT_LISTENERS, "RegistrationCoppaActivity Unregister: NetworStateListener,Context");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (!RegistrationCoppaLaunchHelper.isBackEventConsumedByRegistration(this)) {
            // not consumed vertical code goes here // actual code
            super.onBackPressed();
        }
    }

    private void initUI() {
        ivBack = (TextView) findViewById(R.id.iv_reg_back);
        ivBack.setOnClickListener(this);
        launchRegistrationFragment(isAccountSettings);


    }

    private void launchRegistrationFragment(boolean isAccountSettings) {
        try {
            FragmentManager mFragmentManager = getSupportFragmentManager();
            RegistrationCoppaFragment registrationFragment = new RegistrationCoppaFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(RegConstants.ACCOUNT_SETTINGS,isAccountSettings);
            registrationFragment.setArguments(bundle);
            registrationFragment.setOnUpdateTitleListener(this);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_reg_fragment_container, registrationFragment,
                    RegConstants.REGISTRATION_FRAGMENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            RLog.e(RLog.EXCEPTION,
                    "RegistrationCoppaActivity :FragmentTransaction Exception occured in addFragment  :"
                            + e.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_reg_back) {
            onBackPressed();
        }
    }

    @Override
    public void updateRegistrationTitle(int titleResourceID) {
        // Update title and show hamberger
        //ivBack.setVisibility(View.INVISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        TextView tvTitle = ((TextView) findViewById(R.id.tv_reg_header_title));
        tvTitle.setText(getString(titleResourceID));
    }

    @Override
    public void updateRegistrationTitleWithBack(int titleResourceID) {
        // update title and show back
        ivBack.setVisibility(View.VISIBLE);
        TextView tvTitle = ((TextView) findViewById(R.id.tv_reg_header_title));
        tvTitle.setText(getString(titleResourceID));
    }

    @Override
    public void updateRegistrationTitleWithOutBack(int titleResourceID) {
        // update title and show back
        ivBack.setVisibility(View.INVISIBLE);
        TextView tvTitle = ((TextView) findViewById(R.id.tv_reg_header_title));
        tvTitle.setText(getString(titleResourceID));
    }

}
