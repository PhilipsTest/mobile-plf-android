package com.philips.platform.pthdemolaunch;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;

import com.philips.amwelluapp.PTHMicroAppDependencies;
import com.philips.amwelluapp.PTHMicroAppInterface;
import com.philips.amwelluapp.PTHMicroAppLaunchInput;
import com.philips.amwelluapp.PTHMicroAppSettings;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

public class MainActivity extends FragmentActivity implements ActionBarListener{

    private FragmentLauncher fragmentLauncher;
    private PTHMicroAppLaunchInput PTHMicroAppLaunchInput;
    private PTHMicroAppInterface PTHMicroAppInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pth_launch_activity);
        initAppInfra();
        fragmentLauncher = new FragmentLauncher(this,R.id.uappFragmentLayout,this);
        PTHMicroAppLaunchInput = new PTHMicroAppLaunchInput("Launch Uapp Input");
        PTHMicroAppInterface = new PTHMicroAppInterface();
        PTHMicroAppInterface.init(new PTHMicroAppDependencies(((AmwellDemoApplication)this.getApplicationContext()).getAppInfra()),new PTHMicroAppSettings(this.getApplicationContext()));
        PTHMicroAppInterface.launch(fragmentLauncher, PTHMicroAppLaunchInput);

    }

    private void initAppInfra() {
        ((AmwellDemoApplication)getApplicationContext()).initializeAppInfra(new AppInitializationCallback.AppInfraInitializationCallback() {
            @Override
            public void onAppInfraInitialization() {

            }
        });
    }

    @Override
    public void updateActionBar(@StringRes int i, boolean b) {

    }

    @Override
    public void updateActionBar(String s, boolean b) {

    }
}
