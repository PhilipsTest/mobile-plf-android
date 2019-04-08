package com.philips.platform.pim.configration;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;

import net.openid.appauth.AuthorizationServiceDiscovery;

public class PIMOIDCConfigration {
    private static String TAG = PIMOIDCConfigration.class.getSimpleName();
    private static final String GROUP_PIM = "PIM";
    private static final String CLIENT_ID = "clientId";
    private AuthorizationServiceDiscovery authorizationServiceDiscovery;

    private AppInfraInterface appInfraInterface;

    public PIMOIDCConfigration(AuthorizationServiceDiscovery authorizationServiceDiscovery, AppInfraInterface appInfraInterface) {
        this.authorizationServiceDiscovery = authorizationServiceDiscovery;
        this.appInfraInterface = appInfraInterface;
    }

    //TODO: Note once saved AuthState, do we need to populate PIMOIDCConfigration class through AuthState
    public AuthorizationServiceDiscovery getAuthorizationServiceDiscovery() {
        return authorizationServiceDiscovery;
    }

    // TODO: Get appinfra via settings manager or create constructor to inject what is required
    protected String getClientId() {
        Object obj = getProperty(CLIENT_ID, GROUP_PIM);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    private Object getProperty(String key, String group) {
        //TODO: Deepthi  ( Low ) check impact of cloud config
        AppConfigurationInterface appConfigurationInterface = appInfraInterface.getConfigInterface();
        Object obj = appConfigurationInterface.getPropertyForKey(key, group, new AppConfigurationInterface.AppConfigurationError());
        return obj;
    }
}
