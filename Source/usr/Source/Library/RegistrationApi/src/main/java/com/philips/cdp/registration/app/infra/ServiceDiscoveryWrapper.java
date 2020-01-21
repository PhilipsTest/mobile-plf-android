package com.philips.cdp.registration.app.infra;

import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Single;


public class ServiceDiscoveryWrapper {

    private final ServiceDiscoveryInterface serviceDiscoveryInterface;

    public ServiceDiscoveryWrapper(ServiceDiscoveryInterface serviceDiscoveryInterface) {
        this.serviceDiscoveryInterface = serviceDiscoveryInterface;
    }

    public Single<String> getServiceUrlWithCountryPreferenceSingle(String serviceId) {
        return Single.create(emitter -> {
            ServiceDiscoveryInterface.OnGetServiceUrlMapListener listener = new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
                @Override
                public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                    if (!emitter.isDisposed())
                        emitter.onSuccess(urlMap.get(serviceId).getConfigUrls());
                }

                @Override
                public void onError(ERRORVALUES error, String message) {
                    if (!emitter.isDisposed())
                        emitter.onError(new Throwable(message));
                    return;
                }
            };
            ArrayList<String> serviceIDList = new ArrayList<>();
            serviceIDList.add(serviceId);
            serviceDiscoveryInterface.getServicesWithCountryPreference(serviceIDList, listener,null);
        });
    }

    public Single<String> getServiceLocaleWithLanguagePreferenceSingle(String serviceId) {
        return Single.create(emitter -> {
            ArrayList<String> serviceIDList = new ArrayList<>();
            serviceIDList.add(serviceId);
            ServiceDiscoveryInterface.OnGetServiceUrlMapListener listener = new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
                @Override
                public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                    if (!emitter.isDisposed())
                        emitter.onSuccess(urlMap.get(serviceId).getLocale());
                }

                @Override
                public void onError(ERRORVALUES error, String message) {
                    if (!emitter.isDisposed())
                        emitter.onError(new Throwable(message));
                    return;
                }
            };
            serviceDiscoveryInterface.getServicesWithLanguagePreference(serviceIDList, listener,null);
        });
    }

    public Single<String> getServiceLocaleWithCountryPreferenceSingle(String serviceId) {
        return Single.create(emitter -> {
            ServiceDiscoveryInterface.OnGetServiceUrlMapListener listener = new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
                @Override
                public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                    if (!emitter.isDisposed())
                        emitter.onSuccess(urlMap.get(serviceId).getLocale());
                }

                @Override
                public void onError(ERRORVALUES error, String message) {
                    if (!emitter.isDisposed())
                        emitter.onError(new Throwable(message));
                    return;
                }
            };

            ArrayList<String> serviceIDList = new ArrayList<>();
            serviceIDList.add(serviceId);

            serviceDiscoveryInterface.getServicesWithCountryPreference(serviceIDList,listener,null);
        });
    }
}
