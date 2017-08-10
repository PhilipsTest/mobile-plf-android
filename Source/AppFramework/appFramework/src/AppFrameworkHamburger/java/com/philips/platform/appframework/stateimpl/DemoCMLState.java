/* Copyright (c) Koninklijke Philips N.V., 2017
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.appframework.stateimpl;

import android.content.Context;

import com.philips.cdp.devicepair.uappdependencies.WifiCommLibUappSettings;
import com.philips.cdp2.demouapp.CommlibUapp;
import com.philips.cdp2.demouapp.DefaultCommlibUappDependencies;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

/**
 * State class to launch Comm lib demo micro app.
 */

public class DemoCMLState extends BaseState {

    private static final String TAG = DemoCMLState.class.getSimpleName();
    private Context context;

    public DemoCMLState() {
        super(AppStates.TESTDICOMM);
    }

    @Override
    public void navigate(UiLauncher uiLauncher) {
        CommlibUapp uAppInterface = getCommLibUApp();
        if (uAppInterface != null) {
            RALog.d(TAG, "CommlibUApp is null");
            try {
                uAppInterface.init(new DefaultCommlibUappDependencies(context.getApplicationContext()), new WifiCommLibUappSettings(context.getApplicationContext()));
            }
            catch (UnsatisfiedLinkError error) {
                RALog.d(TAG, "Not able to find native implementation");
            }
            uAppInterface.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, 0), null);
        }


    }

    @Override
    public void init(Context context) {
        this.context = context;
    }

    public CommlibUapp getCommLibUApp() {
        return CommlibUapp.get();
    }

    @Override
    public void updateDataModel() {

    }
}
