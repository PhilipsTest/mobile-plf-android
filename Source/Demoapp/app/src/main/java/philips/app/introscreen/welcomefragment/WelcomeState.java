/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package philips.app.introscreen.welcomefragment;

import android.content.Context;

import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

import flowmanager.AppStates;
import philips.app.base.AppFrameworkBaseActivity;

public class WelcomeState extends BaseState {

    public WelcomeState() {
        super(AppStates.WELCOME);
    }

    /**
     * to navigate
     * @param uiLauncher requires UiLauncher
     */
    @Override
    public void navigate(UiLauncher uiLauncher) {
        final FragmentLauncher fragmentLauncher = (FragmentLauncher) uiLauncher;
        ((AppFrameworkBaseActivity) fragmentLauncher.getFragmentActivity()).
                addFragment(new WelcomeFragment(), WelcomeFragment.TAG);
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void updateDataModel() {

    }
}
