/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.appframework;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.philips.platform.appframework.R;
import com.philips.platform.appframework.utility.Constants;
import com.philips.platform.appframework.utility.Logger;
import com.philips.cdp.productselection.utils.ProductSelectionLogger;
import com.philips.cdp.uikit.UiKitActivity;

/**
 * AppFrameworkBaseActivity is the App level settings class for controlling the behavior of apps.
 *
 * @author: ritesh.jha@philips.com
 * @since: June 17, 2016
 */
public abstract class AppFrameworkBaseActivity extends UiKitActivity {
    public static final String SHARED_PREFERENCES = "SharedPref";
    public static final String DONE_PRESSED = "donePressed";
    private static String TAG = AppFrameworkBaseActivity.class.getSimpleName();
    private FragmentManager fragmentManager = null;
    private static SharedPreferences mSharedPreference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ProductSelectionLogger.i(Constants.ACTIVITY, "onCreate");
        fragmentManager = getSupportFragmentManager();
    }

    protected void showFragment(Fragment fragment, String fragmentTag) {
        int containerId = Constants.MAIN_ACTIVITY_CONTAINER;

        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            if (mEnterAnimation != 0 && mExitAnimation != 0) {
//                fragmentTransaction.setCustomAnimations(mEnterAnimation,
//                        mExitAnimation, mEnterAnimation, mExitAnimation);
//            }
            fragmentTransaction.replace(containerId, fragment, fragmentTag);
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            Logger.e(TAG, "IllegalStateException" + e.getMessage());
            e.printStackTrace();
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i(Constants.ACTIVITY, " onPause ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(Constants.ACTIVITY, "onDestroy ");
    }

    protected boolean backstackFragment() {
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
        if (mSharedPreference == null) {
            mSharedPreference = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putBoolean(DONE_PRESSED, true);
        editor.commit();
    }

    protected Boolean getIntroScreenDonePressed() {
        if (mSharedPreference == null) {
            mSharedPreference = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return mSharedPreference.getBoolean(DONE_PRESSED, false);
    }
}
