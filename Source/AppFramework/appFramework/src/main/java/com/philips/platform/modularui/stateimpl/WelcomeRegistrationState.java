/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.modularui.stateimpl;

import android.content.Context;
import android.content.Intent;

import com.philips.platform.appframework.AppFrameworkBaseActivity;
import com.philips.platform.appframework.introscreen.WelcomeActivity;
import com.philips.platform.modularui.statecontroller.UIState;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

public class WelcomeRegistrationState extends UIState {

    private FragmentLauncher fragmentLauncher;
    /**
     * constructor
     * @param stateID
     */
    public WelcomeRegistrationState(@UIStateDef int stateID) {
        super(stateID);
    }

    /**
     * to navigate
     * @param uiLauncher requires UiLauncher
     */
    @Override
    public void navigate(UiLauncher uiLauncher) {
        fragmentLauncher = (FragmentLauncher) uiLauncher;
        fragmentLauncher.getFragmentActivity().startActivity(new Intent(fragmentLauncher.getFragmentActivity(), WelcomeActivity.class));
    }

    /**
     * to handle handleBack events
     * @param context requires context
     */
    @Override
    public void handleBack(final Context context) {
        ((AppFrameworkBaseActivity)context).popBackTillHomeFragment();
    }

    @Override
    public void init(Context context) {

    }
}
