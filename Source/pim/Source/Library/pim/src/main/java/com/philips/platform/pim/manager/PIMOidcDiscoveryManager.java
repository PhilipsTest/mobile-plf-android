package com.philips.platform.pim.manager;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.listeners.PIMAuthServiceConfigListener;
import com.philips.platform.pim.utilities.PIMInitState;

import net.openid.appauth.AuthorizationServiceConfiguration;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

public  class PIMOidcDiscoveryManager implements PIMAuthServiceConfigListener {

    private final String TAG = PIMOidcDiscoveryManager.class.getSimpleName();
    private LoggingInterface mLoggingInterface;


    PIMOidcDiscoveryManager() {
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
    }

    void downloadOidcUrls(String baseUrl) {
        mLoggingInterface.log(DEBUG,TAG,"downloadOidcUrls called with baseUrl : "+baseUrl);
        PIMAuthManager pimAuthManager = new PIMAuthManager();
        pimAuthManager.fetchAuthWellKnownConfiguration("https://stg.accounts.philips.com/c2a48310-9715-3beb-895e-000000000000/login", this); //TODO: Shashi, remove issuer once its uploaded to service discovery
    }

    @Override
    public void onAuthServiceConfigSuccess(AuthorizationServiceConfiguration authServiceConfig) {
        mLoggingInterface.log(DEBUG,TAG,"fetchAuthWellKnownConfiguration : onAuthServiceConfigSuccess : "+ authServiceConfig);
        PIMOIDCConfigration pimoidcConfigration = new PIMOIDCConfigration(authServiceConfig);
        PIMSettingManager.getInstance().setPimOidcConfigration(pimoidcConfigration);
        PIMSettingManager.getInstance().getPimInitLiveData().postValue(PIMInitState.INIT_SUCCESS);
    }

    @Override
    public void onAuthServiceConfigFailed(Error error) {
        mLoggingInterface.log(DEBUG,TAG,"fetchAuthWellKnownConfiguration : onAuthServiceConfigFailed :  "+error.getErrDesc());
        PIMSettingManager.getInstance().getPimInitLiveData().postValue(PIMInitState.INIT_FAILED);
    }
}
