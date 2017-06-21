package com.philips.cdp.registration.app.infra;

import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import java.net.URL;

import io.reactivex.Single;

import static com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnGetServiceUrlListener;

public class ServiceDiscoveryWrapper {

    private final ServiceDiscoveryInterface serviceDiscoveryInterface;

    public ServiceDiscoveryWrapper(ServiceDiscoveryInterface serviceDiscoveryInterface) {
        this.serviceDiscoveryInterface = serviceDiscoveryInterface;
    }

    public Single<String> getServiceUrlWithCountryPreferenceSingle(String serviceId) {
        return Single.create(emitter -> {
            OnGetServiceUrlListener listener = new OnGetServiceUrlListener() {
                @Override
                public void onSuccess(URL url) {
                    emitter.onSuccess(url.toString());
                }

                @Override
                public void onError(ERRORVALUES errorvalues, String s) {
                    emitter.onError(new Throwable(s));
                }
            };
            serviceDiscoveryInterface.getServiceUrlWithCountryPreference(serviceId, listener);
        });
    }
    public Single<String> getServiceLocaleWithLanguagePreferenceSingle(String serviceId) {
        return Single.create(emitter -> {
            ServiceDiscoveryInterface.OnGetServiceLocaleListener listener = new ServiceDiscoveryInterface.OnGetServiceLocaleListener() {
                @Override
                public void onSuccess(String s) {
                    emitter.onSuccess(s.toString());
                }

                @Override
                public void onError(ERRORVALUES errorvalues, String s) {
                    emitter.onError(new Throwable(s));
                }
            };
            serviceDiscoveryInterface.getServiceLocaleWithLanguagePreference(serviceId, listener);
        });
    }
    public Single<String> getServiceLocaleWithCountryPreferenceSingle(String serviceId) {
        return Single.create(emitter -> {
            ServiceDiscoveryInterface.OnGetServiceLocaleListener listener = new ServiceDiscoveryInterface.OnGetServiceLocaleListener() {
                @Override
                public void onSuccess(String s) {
                    emitter.onSuccess(s.toString());
                }

                @Override
                public void onError(ERRORVALUES errorvalues, String s) {
                    emitter.onError(new Throwable(s));
                }
            };
            serviceDiscoveryInterface.getServiceLocaleWithCountryPreference(serviceId, listener);
        });
    }
}
