package com.pim.demouapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import static com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.AIL_HOME_COUNTRY;

public class HomeCountryUpdateReceiver extends BroadcastReceiver {
    private PIMDemoUAppInterface uAppInterface;
    private AppInfraInterface mAppInfraInterface;
    public HomeCountryUpdateReceiver(AppInfraInterface appInfraInterface) {
        uAppInterface = new PIMDemoUAppInterface();
        mAppInfraInterface = appInfraInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       /* if (intent.getAction().equals(ServiceDiscoveryInterface.AIL_SERVICE_DISCOVERY_HOMECOUNTRY_CHANGE_ACTION)) {
            String countryCode = (String) intent.getExtras().get(AIL_HOME_COUNTRY);
            Toast.makeText(context, "Home country updated to " + countryCode, Toast.LENGTH_SHORT).show();
            Log.v(getClass() + "", "Home country changed to " + countryCode);
            uAppInterface.init(new PIMDemoUAppDependencies(mAppInfraInterface), new PIMDemoUAppSettings(context));
        }*/
    }
}
