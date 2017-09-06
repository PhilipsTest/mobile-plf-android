package com.philips.cdp.di.iap.integration;

import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.uappframework.launcher.UiLauncher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IAPServiceDiscoveryWrapper {
    private IAPSettings mIAPSettings;
    private ArrayList<String> listOfServiceId;
    ServiceDiscoveryInterface.OnGetServiceUrlMapListener serviceUrlMapListener;
    private ServiceDiscoveryInterface serviceDiscoveryInterface;

    IAPServiceDiscoveryWrapper(IAPSettings pIAPSettings) {
        mIAPSettings = pIAPSettings;
        listOfServiceId = new ArrayList<>();
        listOfServiceId.add("iap.baseurl");
        AppInfraInterface appInfra = CartModelContainer.getInstance().getAppInfraInstance();
        serviceDiscoveryInterface = appInfra.getServiceDiscovery();
        serviceDiscoveryInterface = appInfra.getServiceDiscovery();
    }

    void initializeStoreFromServiceDiscoveryResponse(final IAPHandler iapHandler) {

        serviceUrlMapListener = new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> map) {
                IAPLog.i(IAPLog.LOG, " getServicesWithCountryPreference Map" + map.toString());
                Collection<ServiceDiscoveryService> collection = map.values();

                List<ServiceDiscoveryService> list = new ArrayList<>();
                list.addAll(collection);
                ServiceDiscoveryService serviceDiscoveryService = list.get(0);

                String locale = serviceDiscoveryService.getLocale();
                String configUrls = serviceDiscoveryService.getConfigUrls();
                if (configUrls == null) {
                    mIAPSettings.setUseLocalData(true);
                } else {
                    mIAPSettings.setUseLocalData(false);
                }
                iapHandler.initIAPRequisite();
            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
                IAPLog.i(IAPLog.LOG, "ServiceDiscoveryInterface ==errorvalues " + errorvalues.name() + "String= " + s);
            }
        };
        serviceDiscoveryInterface.getServicesWithCountryPreference(listOfServiceId, serviceUrlMapListener);
    }


    void getLocaleFromServiceDiscovery(final UiLauncher pUiLauncher, final IAPHandler pIAPHandler, final IAPLaunchInput pIapLaunchInput, final IAPListener iapListener, final String entry) {


        serviceUrlMapListener = new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> map) {
                IAPLog.i(IAPLog.LOG, " getServicesWithCountryPreference Map" + map.toString());
                Collection<ServiceDiscoveryService> collection = map.values();

                List<ServiceDiscoveryService> list = new ArrayList<>();
                list.addAll(collection);
                ServiceDiscoveryService serviceDiscoveryService = list.get(0);

                String locale = serviceDiscoveryService.getLocale();
                String configUrls = serviceDiscoveryService.getConfigUrls();
                if (locale != null) {
                    setLangAndCountry(locale);
                }

                //Condition for launching IAP screens
                if (iapListener == null && entry == null) {
                    if (configUrls == null || configUrls.isEmpty()) {
                        mIAPSettings.setUseLocalData(true);
                    } else {
                        // TODO Retailer view hence making the userLocalData to true
                        mIAPSettings.setUseLocalData(false);
                        //    String urlPort = "https://acc.us.pil.shop.philips.com/en_US";//;"https://www.occ.shop.philips.com/en_US";
                        mIAPSettings.setHostPort(configUrls + "/");
                    }
                    pIAPHandler.initControllerFactory();
                    launchingIAP(pIAPHandler, pUiLauncher, pIapLaunchInput);
                } else {
                    //Condition for returning gatCartCount API and getCompleteProductlist API
                    if (configUrls == null) {
                        mIAPSettings.setUseLocalData(true);
                    } else {
                        // TODO Retailer view hence making the userLocalData to true
                        mIAPSettings.setUseLocalData(false);
                        //String urlPort = "https://acc.us.pil.shop.philips.com/en_US";//;"https://www.occ.shop.philips.com/en_US";
                        mIAPSettings.setHostPort(configUrls + "/");
                        mIAPSettings.setProposition(loadConfigParams());
                        pIAPHandler.initControllerFactory();
                        if (entry.equalsIgnoreCase("completeProductList"))
                            pIAPHandler.getExposedAPIImplementor().getCompleteProductList(iapListener);
                        else
                            pIAPHandler.getExposedAPIImplementor().getProductCartCount(iapListener);
                    }
                }
            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
                IAPLog.i(IAPLog.LOG, "ServiceDiscoveryInterface ==errorvalues " + errorvalues.name() + "String= " + s);
            }
        };
        serviceDiscoveryInterface.getServicesWithCountryPreference(listOfServiceId, serviceUrlMapListener);
    }

    private void launchingIAP(IAPHandler pIAPHandler, UiLauncher pUiLauncher, IAPLaunchInput pUappLaunchInput) {
        mIAPSettings.setProposition(loadConfigParams());
        if (!mIAPSettings.isUseLocalData() && (!pIAPHandler.isStoreInitialized(mIAPSettings.getContext()))) {
            pIAPHandler.initIAP(pUiLauncher, pUappLaunchInput);
        } else {
            pIAPHandler.launchIAP(pUiLauncher, pUappLaunchInput);
        }
    }

    private String loadConfigParams() {
        AppConfigurationInterface mConfigInterface = CartModelContainer.getInstance().getAppInfraInstance().getConfigInterface();
        AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();

        String propositionId = (String) mConfigInterface.getPropertyForKey("propositionid", "IAP", configError);

        if (configError.getErrorCode() != null) {
            IAPLog.e(IAPLog.LOG, "VerticalAppConfig ==loadConfigurationFromAsset " + configError.getErrorCode().toString());
        }
        return propositionId;
    }

    private void setLangAndCountry(String locale) {
        String[] localeArray;
        localeArray = locale.split("_");
        CartModelContainer.getInstance().setLanguage(localeArray[0]);
        CartModelContainer.getInstance().setCountry(localeArray[1]);
        HybrisDelegate.getInstance().getStore().setLangAndCountry(localeArray[0], localeArray[1]);
        IAPLog.i(IAPLog.LOG, "setLangAndCountry Locale = " + HybrisDelegate.getInstance().getStore().getLocale());
    }

}
