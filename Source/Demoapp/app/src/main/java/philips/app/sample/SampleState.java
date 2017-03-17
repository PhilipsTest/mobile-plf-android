/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package philips.app.sample;

import android.content.Context;

import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.samplemicroapp.SampleMicroAppDependencies;
import com.philips.platform.samplemicroapp.SampleMicroAppInterface;
import com.philips.platform.samplemicroapp.SampleMicroAppLaunchInput;
import com.philips.platform.samplemicroapp.SampleMicroAppSettings;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

import flowmanager.AppStates;
import philips.app.base.AppFrameworkApplication;

public class SampleState extends BaseState {

    public SampleState() {
        super(AppStates.SAMPLE);
    }

    /**
     * to navigate
     * @param uiLauncher requires UiLauncher
     */
    @Override
    public void navigate(UiLauncher uiLauncher) {
        FragmentLauncher fragmentLauncher = (FragmentLauncher) uiLauncher;
        AppFrameworkApplication appFrameworkApplication = (AppFrameworkApplication) fragmentLauncher.getFragmentActivity().getApplicationContext();
        SampleMicroAppDependencies sampleMicroAppDependencies = new SampleMicroAppDependencies(appFrameworkApplication.getAppInfra());
        SampleMicroAppInterface sampleMicroAppInterface = new SampleMicroAppInterface();
        sampleMicroAppInterface.init(sampleMicroAppDependencies, new SampleMicroAppSettings(fragmentLauncher.getFragmentActivity()));
        sampleMicroAppInterface.launch(uiLauncher, new SampleMicroAppLaunchInput("Welcome to Sample micro app"));
    }

    @Override
    public void init(Context context) {
    }

    @Override
    public void updateDataModel() {

    }
}
