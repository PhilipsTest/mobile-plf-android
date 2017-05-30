package com.philips.platform.baseapp.screens.cocoversion;

import android.content.Context;

import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.baseapp.base.AppFrameworkBaseActivity;

import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

/**
 * Created by philips on 4/18/17.
 */

public class CocoVersionState extends BaseState {
    public static final String TAG = CocoVersionState.class.getSimpleName();

    FragmentLauncher fragmentLauncher;

    public CocoVersionState() {
        super(AppStates.COCO_VERSION_INFO);
    }

    /**
     * Navigating to COCOVersionInfo
     * @param uiLauncher requires UiLauncher
     */
    @Override
    public void navigate(UiLauncher uiLauncher) {
        RALog.d(TAG, "navigate called");
        fragmentLauncher = (FragmentLauncher) uiLauncher;
        ((AppFrameworkBaseActivity)fragmentLauncher.getFragmentActivity()).
                handleFragmentBackStack( new CocoVersionFragment(), CocoVersionFragment.TAG,getUiStateData().getFragmentLaunchState());
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void updateDataModel() {

    }
}
