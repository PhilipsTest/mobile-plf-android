package com.philips.platform.pim.manager;

import android.os.Handler;
import android.os.Looper;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pim.utilities.PIMInitState;

import java.util.ArrayList;
import java.util.Map;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

/**
 * Class to download configuration required for PIM
 */
public class PIMConfigManager {
    private static final String TAG = PIMConfigManager.class.getSimpleName();
    private LoggingInterface mLoggingInterface;
    private final String PIM_BASEURL = "userreg.janrainoidc.issuer";
    private PIMUserManager mPimUserManager;


    public PIMConfigManager(PIMUserManager pimUserManager) {
        mPimUserManager = pimUserManager;
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
    }

    public void init(ServiceDiscoveryInterface serviceDiscoveryInterface) {
        mLoggingInterface.log(DEBUG, TAG, "init called");
        ArrayList<String> listOfServiceId = new ArrayList<>();
        listOfServiceId.add(PIM_BASEURL);
        if (mPimUserManager.getUserLoggedInState() == UserLoggedInState.USER_NOT_LOGGED_IN)
            downloadSDServiceURLs(serviceDiscoveryInterface, listOfServiceId);
        else {
            mLoggingInterface.log(DEBUG, TAG, "downloadSDServiceURLs skipped as user is logged in. ");
            PIMSettingManager.getInstance().getPimInitLiveData().setValue(PIMInitState.INIT_SUCCESS);
        }
    }

    private void downloadSDServiceURLs(ServiceDiscoveryInterface serviceDiscoveryInterface, ArrayList<String> listOfServiceId) {
        mLoggingInterface.log(DEBUG, TAG, "downloadSDServiceURLs called");
        new Thread(() -> {
            serviceDiscoveryInterface.getServicesWithCountryPreference(listOfServiceId, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
                @Override
                public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                    mLoggingInterface.log(DEBUG, TAG, "DownloadSDServiceURLs success callbback recieved");

                    ServiceDiscoveryService serviceDiscoveryService = urlMap.get(PIM_BASEURL);
                    if (serviceDiscoveryService == null) {
                        mLoggingInterface.log(DEBUG, TAG, "DownloadSDServiceURLs success  : serviceDiscovery response is null");
                        PIMSettingManager.getInstance().getPimInitLiveData().setValue(PIMInitState.INIT_FAILED);
                    } else {
                        PIMSettingManager.getInstance().setLocale(serviceDiscoveryService.getLocale());
                        String configUrls = serviceDiscoveryService.getConfigUrls();
                        if (configUrls != null) {
                            PIMOidcDiscoveryManager pimOidcDiscoveryManager = new PIMOidcDiscoveryManager();
                            mLoggingInterface.log(DEBUG, TAG, "DownloadSDServiceURLs success : getConfigUrls : " + configUrls);
                            pimOidcDiscoveryManager.downloadOidcUrls(configUrls); //Download OIDC configuration
                        } else {
                            mLoggingInterface.log(DEBUG, TAG, "DownloadSDServiceURLs success : No service url found for Issuer service id");
                            PIMSettingManager.getInstance().getPimInitLiveData().setValue(PIMInitState.INIT_FAILED);
                        }
                    }
                }

                @Override
                public void onError(ERRORVALUES error, String message) {
                    mLoggingInterface.log(DEBUG, TAG, "DownloadSDServiceURLs error. ERRORVALUES: " + error + " Message: " + message);
                    updateInitState(false);
                }
            }, null);
        }).start();
    }

    private void updateInitState(boolean isInitSuccess){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PIMSettingManager.getInstance().getPimInitLiveData().setValue(PIMInitState.INIT_FAILED);
            }
        });
    }
}
