
package com.philips.platform.urdemo;

import android.content.Context;
import android.content.Intent;

import com.philips.cdp.registration.ui.utils.URDependancies;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URSettings;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class URDemouAppInterface implements UappInterface {

    private Context context;

 /**
     * @param uappDependencies - App dependencies
     * @param uappSettings     - App settings
     */
    @Override
    public void init(final UappDependencies uappDependencies, final UappSettings uappSettings) {
       this.context = uappSettings.getContext();
        URDependancies urDependancies = new URDependancies(uappDependencies.getAppInfra());
        URSettings urSettings = new URSettings(context);
        URInterface urInterface = new URInterface();
        urInterface.init(urDependancies, urSettings);
    }

    /**
     * @param uiLauncher - Launcher to differentiate activity or fragment
     */
    @Override
    public void launch(final UiLauncher uiLauncher, final UappLaunchInput uappLaunchInput) {
       if (uiLauncher instanceof ActivityLauncher) {
            Intent intent = new Intent(context, URStandardDemoActivity.class);
           intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } 
    }
}
