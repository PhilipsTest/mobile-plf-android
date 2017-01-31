/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.servicediscovery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;
import com.philips.platform.appinfra.internationalization.InternationalizationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorage;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.servicediscovery.model.AISDResponse;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscovery;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class downloads list of URLs from service discovery server,
 * providing the locale and the identity of the application.
 */
public class ServiceDiscoveryManager implements ServiceDiscoveryInterface {


    private abstract class DownloadItemListener {
        public boolean forceRefresh() {
            return false;
        }

        public abstract void onDownloadDone(AISDResponse result);
    }

    private interface AISDListener extends OnErrorListener {
        void ondataReceived(AISDResponse response);
    }

    private enum AISDURLType {AISDURLTypeProposition, AISDURLTypePlatform}


    private static final String URLTagTest = "apps%2b%2benv%2btest";
    private static final String URLTagDevelopment = "apps%2b%2benv%2bdev";
    private static final String URLTagStaging = "apps%2b%2benv%2bstage";
    private static final String URLTagAcceptance = "apps%2b%2benv%2bacc";
    private static final String URLTagProduction = "apps%2b%2benv%2bprod";

    private static final String baseURLProduction = "www.philips.com";
    private static final String baseURLTesting = "tst.philips.com";
    private static final String baseURLStaging = "dev.philips.com";
    private static final String baseURLAcceptance = "acc.philips.com";

    private static final String stateTesting = "TEST";
    private static final String stateDevelopment = "DEVELOPMENT";
    private static final String stateStaging = "STAGING";
    private static final String stateAccepteance = "ACCEPTANCE";
    private static final String stateProduction = "PRODUCTION";


    private OnGetHomeCountryListener.SOURCE countryCodeSource;
    private final AppInfra mAppInfra;
    private final Context context;
    private AISDResponse serviceDiscovery = null;

    private String countryCode;
    private long holdbackTime = 0l;

    private final RequestManager mRequestItemManager;

    //
    private boolean downloadInProgress;
    private ArrayDeque<DownloadItemListener> downloadAwaiters;
    private ReentrantLock downloadLock;

    public ServiceDiscoveryManager(AppInfra aAppInfra) {
        mAppInfra = aAppInfra;
        context = mAppInfra.getAppInfraContext();
        mRequestItemManager = new RequestManager(context, mAppInfra);
        downloadInProgress = false;
        downloadAwaiters = new ArrayDeque<>();
        downloadLock = new ReentrantLock();
    }

    private void queueResultListener(boolean forcerefresh, DownloadItemListener listener) {
        downloadLock.lock();

        if (forcerefresh)
            serviceDiscovery = null;
        downloadAwaiters.add(listener);

        if (!downloadInProgress) {
            if (new Date().getTime() > holdbackTime) {// if current time is greater then holdback time
                downloadInProgress = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean forceRefresh = false;
                        AISDResponse service;
                        ArrayList<DownloadItemListener> stalledAwaiters = new ArrayList<DownloadItemListener>();
                        do {
                            if (forceRefresh)
                                downloadLock.unlock();
                            forceRefresh = false;
                            service = downloadServices();
                            downloadLock.lock();
                            DownloadItemListener d;
                            while ((d = downloadAwaiters.poll()) != null) {
                                if (d.forceRefresh())
                                    forceRefresh = true;
                                stalledAwaiters.add(d);
                            }
                        }
                        while (forceRefresh);
                        downloadInProgress = false;
                        serviceDiscovery = service;
                        final AISDResponse result = service;
                        downloadLock.unlock();

                        for (final DownloadItemListener d : stalledAwaiters) {
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    d.onDownloadDone(result);
                                }
                            });

                            new Handler(Looper.getMainLooper()).post(t);
                        }
                    }
                }).start();
            } else {
                ServiceDiscovery ServiceDiscoveryError = new ServiceDiscovery();
                ServiceDiscoveryError.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.SERVER_ERROR, "Server is not reachable at the moment,Please try after some time"));
                //ServiceDiscoveryError.setSuccess(false);
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "Server is not reachable at the moment,Please try after some time");
                //listener.onDownloadDone(ServiceDiscoveryError);
            }
        } else {

            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "Download already in progress, please wait for response");
        }
        downloadLock.unlock();
    }


    /**
     * Precondition: download lock is acquired
     */
    private AISDResponse downloadServices() {
        ServiceDiscovery platformService = null;
        ServiceDiscovery propositionService;
        propositionService = downloadPropositionService();
        if (propositionService.isSuccess()) {
            platformService = downloadPlatformService();
        }
        AISDResponse response = new AISDResponse();
        response.setPlatformURLs(platformService);
        response.setPropositionURLs(propositionService);
        return response;
    }


    private ServiceDiscovery downloadPlatformService() {
        String platformURL = getSDURLForType(AISDURLType.AISDURLTypePlatform);
        ServiceDiscovery platformService = new ServiceDiscovery();
        if (platformURL != null) {
            platformService = processRequest(platformURL, platformService);
        }
        return platformService;
    }


    private ServiceDiscovery downloadPropositionService() {
        String propositionURL = getSDURLForType(AISDURLType.AISDURLTypeProposition);
        ServiceDiscovery propositionService = new ServiceDiscovery();
        if (propositionURL != null) {
            propositionService = processRequest(propositionURL, propositionService);
        }
        return propositionService;
    }


    private ServiceDiscovery processRequest(String urlBuild, ServiceDiscovery service) {
        ServiceDiscovery SDcache = mRequestItemManager.getServiceDiscoveryFromCache(urlBuild);
        if (null == SDcache) {
            if (!isOnline()) {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "NO_NETWORK");
                service.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO_NETWORK"));
                // service.setSuccess(false);
            } else {
                //urlBuild = buildUrl();
                if (urlBuild != null) {
                    service = mRequestItemManager.execute(urlBuild);
                    saveToSecureStore(service.getCountry(), true);

                    if (service.isSuccess()) {
                        holdbackTime = 0;   //remove hold back time
                        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "SD Fetched from server");
                    } else {
                        holdbackTime = new Date().getTime() + 10000; // curent time + 10 Seconds
                        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "SD call", service.getError().toString());
                    }
                } else {
                    service.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.UNKNOWN_ERROR, "URL is null"));
                }
            }
        } else {
            service = SDcache;
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "SD Fetched from cache");
        }
        return service;
    }

    private String getSDURLForType(AISDURLType aisdurlType) {
        try {
            String sector = null, micrositeid = null, environment = null;
            String url = null;
            final AppIdentityInterface identityManager = mAppInfra.getAppIdentity();
            final InternationalizationInterface localManager = mAppInfra.getInternationalization();
            final String locale = localManager.getUILocaleString();
            final AppIdentityInterface.AppState state = identityManager.getAppState();
            final String service_environment = identityManager.getServiceDiscoveryEnvironment();

            final String appState = getAppStateStringFromState(state);

            switch (aisdurlType) {
                case AISDURLTypePlatform:
                    sector = "B2C";
                    micrositeid = (String) mAppInfra.getConfigInterface().getDefaultPropertyForKey
                            ("servicediscovery.platformMicrositeId", "appinfra", null);
                    environment = (String) mAppInfra.getConfigInterface().getDefaultPropertyForKey
                            ("servicediscovery.platformMicrositeId", "appinfra", null);
                    environment = getSDBaseURLForEnvironment(environment);
                    break;

                case AISDURLTypeProposition:
                    sector = identityManager.getSector();
                    micrositeid = identityManager.getMicrositeId();
                    environment = getSDBaseURLForEnvironment(service_environment);
                    break;
            }
            if (sector != null && micrositeid != null &&
                    localManager.getUILocale() != null && appState != null) {

                url = "https://" + environment + "/api/v1/discovery/" + sector
                        + "/" + micrositeid + "?locale=" +
                        locale + "&tags=" + appState;


                String countryHome = getCountry(serviceDiscovery);
                if (countryHome != null) {
                    url += "&country=" + countryHome;
                    saveToSecureStore(countryHome, true);
                }
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "URL", "" + url);
            } else {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "Build URL in SD", "" + "Appidentity values are null");
            }
            return url;

        } catch (Exception exception) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "ServiceDiscovery",
                    exception.toString());
        }
        return null;
    }

    private String getSDBaseURLForEnvironment(String serviceEnv) {
        String baseUrl;
        if (serviceEnv.equalsIgnoreCase(stateProduction)) {
            baseUrl = baseURLProduction;
        } else if (serviceEnv.equalsIgnoreCase(stateTesting)) {
            baseUrl = baseURLTesting;
        } else if (serviceEnv.equalsIgnoreCase(stateStaging)) {
            baseUrl = baseURLStaging;
        } else if (serviceEnv.equalsIgnoreCase(stateAccepteance)) {
            baseUrl = baseURLAcceptance;
        } else {
            baseUrl = baseURLTesting;
        }
        return baseUrl;
    }


    private String getAppStateStringFromState(AppIdentityInterface.AppState appState) {
        String appstate = null;
        switch (appState) {
            case TEST:
                appstate = URLTagTest;
                break;
            case DEVELOPMENT:
                appstate = URLTagDevelopment;
                break;
            case STAGING:
                appstate = URLTagStaging;
                break;
            case PRODUCTION:
                appstate = URLTagProduction;
                break;
            case ACCEPTANCE:
                appstate = URLTagAcceptance;
                break;
        }
        return appstate;
    }


    /**
     * Gets the country from app according to settings/SIM/GEOIP
     *
     * @param listener callback.
     */
    @Override
    public void getHomeCountry(final OnGetHomeCountryListener listener) {

        String country = getCountry(serviceDiscovery);
        String countrySource = fetchFromSecureStorage(false);
        if (countrySource != null && countrySource.equalsIgnoreCase("SIMCARD")) {
            countryCodeSource = OnGetHomeCountryListener.SOURCE.SIMCARD;
        } else if (countrySource != null && countrySource.equalsIgnoreCase("GEOIP")) {
            countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
        } else if (countrySource != null && countrySource.equalsIgnoreCase("STOREDPREFERENCE")) {
            countryCodeSource = OnGetHomeCountryListener.SOURCE.STOREDPREFERENCE;
        }

        if (country != null) {
            listener.onSuccess(country, countryCodeSource);
        } else {
            queueResultListener(false, new DownloadItemListener() {
                @Override
                public void onDownloadDone(AISDResponse result) {
                    String country = getCountry(result);
                    if (country != null) {
                        listener.onSuccess(country, countryCodeSource);
                    } else {
                        ServiceDiscovery.Error err = result.getError();
                        listener.onError(err.getErrorvalue(), err.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void setHomeCountry(String countryCode) {

        if (countryCode != null && countryCode.length() == 2) {
            if (!countryCode.equals(getCountry(serviceDiscovery))) { // entered country is different then existing
                this.countryCode = countryCode;
                countryCodeSource = OnGetHomeCountryListener.SOURCE.STOREDPREFERENCE;
                saveToSecureStore(countryCode, true);
                saveToSecureStore(countryCodeSource.toString(), false);
                serviceDiscovery = null;  // if there is no internet then also old SD value must be cleared.
                mRequestItemManager.clearCacheServiceDiscovery(); // clear SD cache
                queueResultListener(true, new DownloadItemListener() {
                    @Override
                    public void onDownloadDone(AISDResponse result) {
                        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "Force Refresh is done", "Force Refresh is done");
                    }
                });
            } else {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SAME COUNTRY", "Entered Country code is same as old one");
            }
        } else {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "Invalid COUNTRY", "Country code is INVALID");
        }
    }


    @Override
    public void getServiceUrlWithLanguagePreference(final String serviceId, final OnGetServiceUrlListener listener) {
        getServiceDiscoveryData(new AISDListener() {
            @Override
            public void ondataReceived(AISDResponse response) {
                URL url = response.getServiceURL(serviceId, AISDResponse.AISDPreference.AISDLanguagePreference, null);
                listener.onSuccess(url);
            }
        });
    }

    @Override
    public void getServiceUrlWithLanguagePreference(final String serviceId, final OnGetServiceUrlListener listener,
                                                    final Map<String, String> replacement) {
        getServiceDiscoveryData(new AISDListener() {
            @Override
            public void ondataReceived(AISDResponse response) {
                URL url = response.getServiceURL(serviceId, AISDResponse.AISDPreference.AISDLanguagePreference,
                        replacement);
                listener.onSuccess(url);
            }
        });
    }

    @Override
    public void getServiceUrlWithCountryPreference(final String serviceId, final OnGetServiceUrlListener listener) {
        getServiceDiscoveryData(new AISDListener() {
            @Override
            public void ondataReceived(AISDResponse response) {
                response.getServiceURL(serviceId, AISDResponse.AISDPreference.AISDCountryPreference, null);
            }
        });
    }

    @Override
    public void getServiceUrlWithCountryPreference(final String serviceId, final OnGetServiceUrlListener listener,
                                                   final Map<String, String> replacement) {
        getServiceDiscoveryData(new AISDListener() {
            @Override
            public void ondataReceived(AISDResponse response) {
                response.getServiceURL(serviceId, AISDResponse.AISDPreference.AISDCountryPreference, replacement);
            }
        });
    }

    @Override
    public void getServicesWithLanguagePreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener) {
        getServiceDiscoveryData(new AISDListener() {
            @Override
            public void ondataReceived(AISDResponse response) {
                HashMap<String, ServiceDiscoveryService> responseMap = response.getServicesUrl(serviceId,
                        AISDResponse.AISDPreference.AISDLanguagePreference, null);
                listener.onSuccess(responseMap);

            }
        });
    }

    @Override
    public void getServicesWithLanguagePreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener,
                                                  final Map<String, String> replacement) {
        getServiceDiscoveryData(new AISDListener() {
            @Override
            public void ondataReceived(AISDResponse response) {
                HashMap<String, ServiceDiscoveryService> responseMap = response.getServicesUrl(serviceId,
                        AISDResponse.AISDPreference.AISDLanguagePreference, replacement);
                listener.onSuccess(responseMap);

            }
        });
    }

    @Override
    public void getServicesWithCountryPreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener) {

        getServiceDiscoveryData(new AISDListener() {
            @Override
            public void ondataReceived(AISDResponse response) {
                HashMap<String, ServiceDiscoveryService> responseMap = response.getServicesUrl(serviceId,
                        AISDResponse.AISDPreference.AISDCountryPreference, null);
                listener.onSuccess(responseMap);
            }
        });
    }

    @Override
    public void getServicesWithCountryPreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener,
                                                 final Map<String, String> replacement) {

        getServiceDiscoveryData(new AISDListener() {
            @Override
            public void ondataReceived(AISDResponse response) {
                HashMap<String, ServiceDiscoveryService> responseMap = response.getServicesUrl(serviceId,
                        AISDResponse.AISDPreference.AISDCountryPreference, replacement);
                listener.onSuccess(responseMap);

            }
        });
    }

    @Override
    public void getServiceLocaleWithLanguagePreference(final String serviceId, final OnGetServiceLocaleListener listener) {
        getServiceDiscoveryData(new AISDListener() {
            @Override
            public void ondataReceived(AISDResponse response) {
                String locale = response.getLocaleWithPreference(AISDResponse.AISDPreference.AISDLanguagePreference);
                listener.onSuccess(locale);
            }
        });
    }

    @Override
    public void getServiceLocaleWithCountryPreference(final String serviceId, final OnGetServiceLocaleListener listener) {
        getServiceDiscoveryData(new AISDListener() {
            @Override
            public void ondataReceived(AISDResponse response) {
                String locale = response.getLocaleWithPreference(AISDResponse.AISDPreference.AISDCountryPreference);
                listener.onSuccess(locale);
            }
        });
    }

    @Override
    public URL applyURLParameters(URL inputURL, Map<String, String> parameters) {
        String url = inputURL.toString();
        URL output;
        if (parameters != null && parameters.size() > 0) {
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                String key = param.getKey();
                String value = param.getValue();
                url = url.replace('%' + key + '%', value);
            }
        }
        try {
            output = new URL(url);
            return output;
        } catch (MalformedURLException ex) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "ServiceDiscovery URL error",
                    "Malformed URL");
            return null;
        }

    }


    void getServiceDiscoveryData(final AISDListener listener) {

        AISDResponse response = serviceDiscovery;
        if (response != null) {
            listener.ondataReceived(response);
        } else {
            queueResultListener(false, new DownloadItemListener() {
                @Override
                public void onDownloadDone(AISDResponse SDResponse) {
                    if (SDResponse != null && SDResponse.isSuccess()) {
                        listener.ondataReceived(SDResponse);
                    } else {
                        if(SDResponse != null) {
                            ServiceDiscovery.Error err = SDResponse.getError();
                            listener.onError(err.getErrorvalue(), err.getMessage());
                        }
                    }
                }
            });
        }
    }


    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void refresh(final OnRefreshListener listener) { // TODO RayKlo: refresh only works if we have no data?
        refresh(listener, false);
    }

    @Override
    public void refresh(final OnRefreshListener listener, final boolean forcerefresh) {
        queueResultListener(forcerefresh, new DownloadItemListener() {
            @Override
            public void onDownloadDone(AISDResponse result) {
                if (result != null && result.isSuccess()) {
                    listener.onSuccess();
                } else {
                    if (result != null) {
                        ServiceDiscovery.Error err = result.getError();
                        listener.onError(err.getErrorvalue(), err.getMessage());
                    }
                }
            }

            @Override
            public boolean forceRefresh() {
                return forcerefresh;
            }
        });
    }

    private String getCountry(AISDResponse service) {

        if (countryCode != null) {
            return countryCode;
        }

        countryCode = fetchFromSecureStorage(true);
        String countrySource = fetchFromSecureStorage(false);
        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "Country", countryCode);

        if (countryCode != null) {
            if (countrySource != null && countrySource.equalsIgnoreCase("SIMCARD")) {
                countryCodeSource = OnGetHomeCountryListener.SOURCE.SIMCARD;
            } else if (countrySource != null && countrySource.equalsIgnoreCase("GEOIP")) {
                countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
            }
//            else {
//                countryCodeSource = OnGetHomeCountryListener.SOURCE.STOREDPREFERENCE;
//            }

            countryCode = countryCode.toUpperCase();
            return countryCode;
        }

        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                countryCode = simCountry.toUpperCase(Locale.US);
                countryCodeSource = OnGetHomeCountryListener.SOURCE.SIMCARD;
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { //
                final String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    countryCode = networkCountry.toUpperCase(Locale.US);
                    countryCodeSource = OnGetHomeCountryListener.SOURCE.SIMCARD;
                }
            }
        } catch (Exception e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "ServiceDiscovery URL error",
                    e.toString());
        }
        if (countryCode == null) {
            if (service != null) {
                countryCode = service.getCountryCode();
                countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
            }
        }
        if (countryCode != null) {
            saveToSecureStore(countryCode, true);
            saveToSecureStore(countryCodeSource.toString(), false);
        }
        return countryCode;
    }

    private void saveToSecureStore(String country, boolean isCountry) {
        SecureStorageInterface ssi = mAppInfra.getSecureStorage();
        SecureStorage.SecureStorageError mSecureStorage = new SecureStorage.SecureStorageError();
        if (isCountry) {
            ssi.storeValueForKey("Country", country, mSecureStorage);
        } else {
            ssi.storeValueForKey("COUNTRY_SOURCE", country, mSecureStorage);
        }
    }

    private String fetchFromSecureStorage(boolean isCountry) {
        SecureStorageInterface ssi = mAppInfra.getSecureStorage();
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        String value;
        if (isCountry) {
            value = ssi.fetchValueForKey("Country", sse);
        } else {
            value = ssi.fetchValueForKey("COUNTRY_SOURCE", sse);
        }
        return value;
    }

}
