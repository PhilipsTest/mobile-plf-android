/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.settingscreen;

import android.content.Context;

import com.philips.platform.baseapp.base.AppFrameworkBaseActivity;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

public class SettingsFragmentState extends BaseState {

    public SettingsFragmentState() {
        super(AppStates.SETTINGS);
    }

    /**
     * to navigate
     * @param uiLauncher requires UiLauncher
     */
    @Override
    public void navigate(UiLauncher uiLauncher) {
        final FragmentLauncher fragmentLauncher = (FragmentLauncher) uiLauncher;
            ((AppFrameworkBaseActivity) fragmentLauncher.getFragmentActivity()).
                    handleFragmentBackStack(new SettingsFragment(), SettingsFragment.TAG,
                            getUiStateData().getFragmentLaunchState());

    }

    @Override
    public void init(Context context) {

    }
}
