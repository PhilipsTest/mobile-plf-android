/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.servicediscovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityManager;
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

import static com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryManager.AISDURLType.AISDURLTypeProposition;

/**
 * This class downloads list of URLs from service discovery server,
 * providing the locale and the identity of the application.
 */
public class ServiceDiscoveryManager implements ServiceDiscoveryInterface {


    public static final String AIL_SERVICE_DISCOVERY_HOMECOUNTRY_CHANGE_ACTION = "ail.servicediscovery.homecountryChanged";
    public static final String AIL_HOME_COUNTRY = "ail.servicediscovery.homeCountry";
    private static final String COUNTRY = "country";
    private static final String COUNTRY_SOURCE = "country_source";
    private static final String URL_TAG_TEST = "apps%2b%2benv%2btest";
    private static final String URL_TAG_DEVELOPMENT = "apps%2b%2benv%2bdev";
    private static final String URL_TAG_STAGING = "apps%2b%2benv%2bstage";
    private static final String URL_TAG_ACCEPTANCE = "apps%2b%2benv%2bacc";
    private static final String URL_TAG_PRODUCTION = "apps%2b%2benv%2bprod";
    private static final String BASE_URL_PRODUCTION = "www.philips.com";
    private static final String BASE_URL_STAGING = "stg.philips.com";
    private static final String STATE_STAGING = "STAGING";
    private static final String STATE_PRODUCTION = "PRODUCTION";
    private final AppInfra mAppInfra;
    private final Context context;
    private final RequestManager mRequestItemManager;
    private final ArrayDeque<AbstractDownloadItemListener> downloadAwaiters;
    private final ReentrantLock downloadLock;
    private OnGetHomeCountryListener.SOURCE countryCodeSource;
    private AISDResponse serviceDiscovery = null;
    private String countryCode;
    private long holdbackTime = 0l;
    private ServiceDiscoveryInterface.OnGetHomeCountryListener.ERRORVALUES errorvalues;
    private String mCountry;
    private String mCountrySourceType;
    /**
     * Instantiates a new Service discovery manager.
     *
     * @param aAppInfra the a app infra
     */
    public ServiceDiscoveryManager(final AppInfra aAppInfra) {
        mAppInfra = aAppInfra;
        context = mAppInfra.getAppInfraContext();
        mRequestItemManager = new RequestManager(context, mAppInfra);
        downloadAwaiters = new ArrayDeque<>();
        downloadLock = new ReentrantLock();
    }

    private void queueResultListener(final boolean forcerefresh, final AbstractDownloadItemListener listener) {
        downloadLock.lock();
        if (forcerefresh) {
            serviceDiscovery = null;
        }
        downloadAwaiters.add(listener);
        if (new Date().getTime() > holdbackTime) {// if current time is greater then holdback time
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean forceRefresh = false;
                    AISDResponse service;
                    final ArrayList<AbstractDownloadItemListener> stalledAwaiters = new ArrayList<AbstractDownloadItemListener>();
                    do {
                        if (forceRefresh) {
                            downloadLock.unlock();
                        }
                        forceRefresh = false;
                        service = downloadServices();
                        downloadLock.lock();
                        AbstractDownloadItemListener d;
                        while ((d = downloadAwaiters.poll()) != null) {
                            if (d.forceRefresh()) {
                                forceRefresh = true;
                            }
                            stalledAwaiters.add(d);
                        }
                    }
                    while (forceRefresh);
                    serviceDiscovery = service;
                    final AISDResponse result = service;
                    downloadLock.unlock();
                    for (final AbstractDownloadItemListener d : stalledAwaiters) {
                        final Thread mThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                d.onDownloadDone(result);
                            }
                        });
                        new Handler(Looper.getMainLooper()).post(mThread);
                    }
                }
            }).start();
        } else {
            final AISDResponse serviceDiscoveryError = new AISDResponse(mAppInfra);
            if (serviceDiscoveryError.getPlatformURLs() != null) {
                final ServiceDiscovery platformError = serviceDiscoveryError.getPlatformURLs();
                platformError.setError(new ServiceDiscovery.Error
                        (OnErrorListener.ERRORVALUES.SERVER_ERROR, "Server is not reachable at the moment,Please try after some time"));
            }
            if (serviceDiscoveryError.getPropositionURLs() != null) {
                serviceDiscoveryError.getPropositionURLs().setError(new ServiceDiscovery.Error
                        (OnErrorListener.ERRORVALUES.SERVER_ERROR, "Server is not reachable at the moment,Please try after some time"));
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "Server is not reachable at the moment,Please try after some time");

            }

            if (listener != null)
                listener.onDownloadDone(serviceDiscoveryError);
        }
        downloadLock.unlock();
    }

    /**
     * Precondition: download lock is acquired
     */
    private AISDResponse downloadServices() {
        final AISDResponse response = new AISDResponse(mAppInfra);
        ServiceDiscovery platformService = null, propositionService = null;

        if (mRequestItemManager.getPropositionEnabled(mAppInfra)) {

            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "Downloading from platform microsite id  and should return the URL's for Service id.  ");
            platformService = downloadPlatformService();
            if (platformService != null && platformService.isSuccess()) {
               /* String country = fetchFromSecureStorage(COUNTRY);
                String countrySource = fetchFromSecureStorage(COUNTRY_SOURCE);
                if (country == null) {
                    if (countrySource == null) {
                        countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
                        saveToSecureStore(countryCodeSource.toString(), COUNTRY_SOURCE);
                    }
                    saveToSecureStore(platformService.getCountry(), COUNTRY);
                }*/
                // fetchCountryAndCountrySource(platformService.getCountry());
                response.setPlatformURLs(platformService);
            }
        } else {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "Downloading from  both proposition microsite id and platform microsite id ");
            propositionService = downloadPropositionService();
            if (propositionService != null && propositionService.isSuccess()) {
                /* String country = fetchFromSecureStorage(COUNTRY);
                 String countrySource = fetchFromSecureStorage(COUNTRY_SOURCE);
                if (country == null) {
                    if (countrySource == null) {
                        countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
                        saveToSecureStore(countryCodeSource.toString(), COUNTRY_SOURCE);
                    }
                    saveToSecureStore(propositionService.getCountry(), COUNTRY);
                }*/
                //   fetchCountryAndCountrySource(propositionService.getCountry());
                platformService = downloadPlatformService();
            }
            if (platformService != null && propositionService != null) {
                if (propositionService.isSuccess() && platformService.isSuccess()) {
                    response.setPlatformURLs(platformService);
                    response.setPropositionURLs(propositionService);
                } else {
                    final ServiceDiscovery error = new ServiceDiscovery();
                    error.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "DOWNLOAD FAILED"));
                    error.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.SERVER_ERROR, "DOWNLOAD FAILED"));
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "DOWNLOAD FAILED");
                }
            } else {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_SERVICE_DISCOVERY,
                        "Download failed");
            }
        }
        return response;
    }

    private void fetchCountryAndCountrySource(String platformOrPropositionCountry,
                                              String url, ServiceDiscovery service, AISDURLType aisdurlType) {
        String country = fetchFromSecureStorage(COUNTRY);
        boolean shouldRetry = false;
        if (country == null) {
            country = platformOrPropositionCountry;
            countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
            saveToSecureStore(platformOrPropositionCountry, countryCodeSource.toString());
            String countryMapped = getMappedCountry(country);
            if(countryMapped != null) {
                shouldRetry = true;
                url += "&country=" + countryMapped;
            } else {
                url += "&country=" + country;
            }
            if(shouldRetry) {
                processRequest(url,service,aisdurlType);
            }
        }
    }

    private ServiceDiscovery downloadPlatformService() {
        final String platformURL = getSDURLForType(AISDURLType.AISDURLTypePlatform);
        ServiceDiscovery platformService = new ServiceDiscovery();
        if (platformURL != null) {
            platformService = processRequest(platformURL, platformService, AISDURLType.AISDURLTypePlatform);
        }
        return platformService;
    }

    private ServiceDiscovery downloadPropositionService() {
        final String propositionURL = getSDURLForType(AISDURLTypeProposition);
        ServiceDiscovery propositionService = new ServiceDiscovery();
        if (propositionURL != null) {
            propositionService = processRequest(propositionURL, propositionService, AISDURLTypeProposition);
        }
        return propositionService;
    }

    private ServiceDiscovery processRequest(String urlBuild, ServiceDiscovery service,
                                            AISDURLType aisdurlType) {
        if (null != mAppInfra.getRestClient() && !mAppInfra.getRestClient().isInternetReachable()) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "NO_NETWORK");
            service.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO_NETWORK"));
            errorvalues = OnErrorListener.ERRORVALUES.NO_NETWORK;
        } else {
            if (urlBuild != null) {
                service = mRequestItemManager.execute(urlBuild, aisdurlType);
                if (service.isSuccess()) {
                    holdbackTime = 0;   //remove hold back time
                    fetchCountryAndCountrySource(service.getCountry(),urlBuild , service ,aisdurlType);
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "SD Fetched from server");
                } else {
                    holdbackTime = new Date().getTime() + 10000; // curent time + 10 Seconds
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY,"Error in process request"+ service.getError().toString());
                }
            } else {
                service.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.UNKNOWN_ERROR, "URL is null"));
            }
        }
        return service;
    }

    private String getSDURLForType(AISDURLType aisdurlType) {
        String sector = null, micrositeid = null, environment = null, url = null;
        final AppConfigurationInterface.AppConfigurationError error = new AppConfigurationInterface
                .AppConfigurationError();
        final AppIdentityInterface identityManager = mAppInfra.getAppIdentity();
        final InternationalizationInterface localManager = mAppInfra.getInternationalization();
        final String locale = localManager.getUILocaleString();
        final AppIdentityInterface.AppState state = identityManager.getAppState();
        final String service_environment = identityManager.getServiceDiscoveryEnvironment();
        final String appState = getAppStateStringFromState(state);

        switch (aisdurlType) {
            case AISDURLTypePlatform:
                sector = "B2C";
                final AppIdentityManager appIdentityManager = new AppIdentityManager(mAppInfra);
                micrositeid = (String) mAppInfra.getConfigInterface().getDefaultPropertyForKey
                        ("servicediscovery.platformMicrositeId", "appinfra", error);
                appIdentityManager.validateMicrositeId(micrositeid);

                final String defSevicediscoveryEnv = (String) mAppInfra.getConfigInterface().getDefaultPropertyForKey
                        ("servicediscovery.platformEnvironment", "appinfra", error);

                final Object dynServiceDiscoveryEnvironment = mAppInfra.getConfigInterface()
                        .getPropertyForKey("servicediscovery.platformEnvironment", "appinfra",
                                error);

                if (defSevicediscoveryEnv != null) {
                    if (defSevicediscoveryEnv.equalsIgnoreCase("production")) { // allow manual override only if static appstate != production
                        environment = defSevicediscoveryEnv;
                    }
                    else {
                        if (dynServiceDiscoveryEnvironment != null)
                            environment = dynServiceDiscoveryEnvironment.toString();
                        else
                            environment = defSevicediscoveryEnv;
                    }
                }
                appIdentityManager.validateServiceDiscoveryEnv(environment);
                environment = getSDBaseURLForEnvironment(environment);
                checkArgumentException(micrositeid,environment);
                break;

            case AISDURLTypeProposition:
                sector = identityManager.getSector();
                micrositeid = identityManager.getMicrositeId();
                environment = getSDBaseURLForEnvironment(service_environment);
                checkArgumentException(micrositeid,environment);
                break;
        }
        if (sector != null && micrositeid != null &&
                locale != null && appState != null) {

            url = "https://" + environment + "/api/v1/discovery/" + sector
                    + "/" + micrositeid + "?locale=" +
                    locale + "&tags=" + appState;


            String country = fetchFromSecureStorage(COUNTRY);
            if (country == null) {
                country = getCountryCodeFromSim();
                if (country != null) {
                    countryCodeSource = OnGetHomeCountryListener.SOURCE.SIMCARD;
                    saveToSecureStore(country, countryCodeSource.toString());
                }
            }

            String countryMapped = getMappedCountry(country);

            if (countryMapped != null) {
                url += "&country=" + countryMapped;
            } else if (country != null) {
                url += "&country=" + country;
            }
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO,AppInfraLogEventID.AI_SERVICE_DISCOVERY, "URL " + url);
        } else {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_SERVICE_DISCOVERY,"Build URL in SD"
                    + "Appidentity values are null");
        }
        return url;
    }

    private String getMappedCountry(String country) {
        Map<String, String> countryMapping = getServiceDiscoveryCountryMapping();
        if (countryMapping != null && countryMapping.size() > 0 && country != null) {
            return countryMapping.get(country);
        }
        return null;
    }

    private void checkArgumentException(String micrositeid,String environment){
        if (micrositeid == null || micrositeid.isEmpty() || environment == null || environment.isEmpty()) {
            throw new IllegalArgumentException("Platform MicrositeId or Platform Service Environment is Missing");
        }
    }


    private String getSDBaseURLForEnvironment(String serviceEnv) {
        String baseUrl = null;
        if (serviceEnv.equalsIgnoreCase(STATE_PRODUCTION)) {
            baseUrl = BASE_URL_PRODUCTION;
        } else if (serviceEnv.equalsIgnoreCase(STATE_STAGING)) {
            baseUrl = BASE_URL_STAGING;
        }
        return baseUrl;
    }

    private String getAppStateStringFromState(AppIdentityInterface.AppState appState) {
        String appstate = null;
        switch (appState) {
            case TEST:
                appstate = URL_TAG_TEST;
                break;
            case DEVELOPMENT:
                appstate = URL_TAG_DEVELOPMENT;
                break;
            case STAGING:
                appstate = URL_TAG_STAGING;
                break;
            case PRODUCTION:
                appstate = URL_TAG_PRODUCTION;
                break;
            case ACCEPTANCE:
                appstate = URL_TAG_ACCEPTANCE;
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
        if (listener == null) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY,
                    "OnGetServiceUrlListener is null initialized");
        } else {
            homeCountryCode(listener);
        }
    }

    @Override
    public String getHomeCountry() {
        String country = fetchFromSecureStorage(COUNTRY);
        country = TextUtils.isEmpty(country) ? null : country;
        return country;
    }

    @Override
    public void setHomeCountry(final String countryCode) {
        if (countryCode != null && countryCode.length() == 2) {
            final String country = fetchFromSecureStorage(COUNTRY);
            if (!countryCode.equals(country)) { // entered country is different then existing
                this.countryCode = countryCode;
                countryCodeSource = OnGetHomeCountryListener.SOURCE.STOREDPREFERENCE;
                saveToSecureStore(countryCode, countryCodeSource.toString());
                sendBroadcast(countryCode);
                serviceDiscovery = null;  // if there is no internet then also old SD value must be cleared.
                mRequestItemManager.clearCacheServiceDiscovery(); // clear SD cache
                queueResultListener(true, new AbstractDownloadItemListener() {
                    @Override
                    public void onDownloadDone(AISDResponse result) {
                        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO,AppInfraLogEventID.AI_SERVICE_DISCOVERY, "Force Refresh is done Force Refresh is done");
                    }
                });

            } else {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_SERVICE_DISCOVERY,"SAME COUNTRY Entered Country code is same as old one");
            }
        } else {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_SERVICE_DISCOVERY,"Invalid COUNTRY Country code is INVALID");
        }
    }

    @Override
    public void getServiceUrlWithLanguagePreference(final String serviceId, final OnGetServiceUrlListener listener) {
        getURlwithLanguageOrCountry(serviceId, listener, null, AISDResponse.AISDPreference.AISDLanguagePreference);
    }

    @Override
    public void getServiceUrlWithLanguagePreference(final String serviceId, final OnGetServiceUrlListener listener,
                                                    final Map<String, String> replacement) {
        getURlwithLanguageOrCountry(serviceId, listener, replacement, AISDResponse.AISDPreference.AISDLanguagePreference);
    }

    @Override
    public void getServiceUrlWithCountryPreference(final String serviceId, final OnGetServiceUrlListener listener) {
        getURlwithLanguageOrCountry(serviceId, listener, null, AISDResponse.AISDPreference.AISDCountryPreference);
    }

    @Override
    public void getServiceUrlWithCountryPreference(final String serviceId, final OnGetServiceUrlListener listener,
                                                   final Map<String, String> replacement) {
        getURlwithLanguageOrCountry(serviceId, listener, replacement, AISDResponse.AISDPreference.AISDCountryPreference);
    }

    private void getURlwithLanguageOrCountry(final String serviceId, final OnGetServiceUrlListener urlListener,
                                             final Map<String, String> replacement, final AISDResponse.AISDPreference preference) {
        if (urlListener == null) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY,
                    "OnGetServiceUrlListener is null initialized");
        } else {
            if (serviceId == null || serviceId.isEmpty()) {
                urlListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "INVALID_INPUT");
            } else {
                getServiceDiscoveryData(new AISDListener() {
                    @Override
                    public void ondataReceived(AISDResponse response) {
                        if (response != null) {
                            if (response.isSuccess()) {
                                final URL url = response.getServiceURL(serviceId, preference,
                                        replacement);
                                if (url != null) {
                                    urlListener.onSuccess(url);
                                } else {
                                    urlListener.onError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                                            "ServiceDiscovery cannot find the locale");
                                }
                            } else if (response.getError() != null) {
                                urlListener.onError(response.getError().getErrorvalue(),
                                        response.getError().getMessage());
                            }
                        } else {
                            if (errorvalues != null) {
                                urlListener.onError(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO NETWORK");
                            } else {
                                urlListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
                                        "INVALID RESPONSE OR DOWNLOAD FAILED");
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void getServicesWithLanguagePreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener) {
        getURlMAPwithLanguageOrCountry(serviceId, listener, null, AISDResponse.AISDPreference.AISDLanguagePreference);
    }

    @Override
    public void getServicesWithLanguagePreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener,
                                                  final Map<String, String> replacement) {
        getURlMAPwithLanguageOrCountry(serviceId, listener, replacement, AISDResponse.AISDPreference.AISDLanguagePreference);
    }

    @Override
    public void getServicesWithCountryPreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener) {
        getURlMAPwithLanguageOrCountry(serviceId, listener, null, AISDResponse.AISDPreference.AISDCountryPreference);
    }

    @Override
    public void getServicesWithCountryPreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener,
                                                 final Map<String, String> replacement) {
        getURlMAPwithLanguageOrCountry(serviceId, listener, replacement, AISDResponse.AISDPreference.AISDCountryPreference);
    }

    private void getURlMAPwithLanguageOrCountry(final ArrayList<String> serviceIds, final OnGetServiceUrlMapListener urlMapListener,
                                                final Map<String, String> replacement, final AISDResponse.AISDPreference preference) {
        if (urlMapListener == null) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY,
                    "OnGetServiceUrlMapListener is null initialized");
        } else {
            if (serviceIds == null || serviceIds.isEmpty()) {
                urlMapListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "INVALID_INPUT");
            } else {
                getServiceDiscoveryData(new AISDListener() {
                    @Override
                    public void ondataReceived(AISDResponse response) {
                        if (response != null) {
                            if (response.isSuccess()) {
                                final HashMap<String, ServiceDiscoveryService> responseMap = response.getServicesUrl(serviceIds,
                                        preference, replacement);
                                if (responseMap != null && responseMap.size() > 0) {
                                    urlMapListener.onSuccess(responseMap);
                                } else {
                                    urlMapListener.onError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                                            "ServiceDiscovery cannot find the locale");
                                }
                            } else if (response.getError() != null) {
                                urlMapListener.onError(response.getError().getErrorvalue(), response.getError().getMessage());
                            }
                        } else {
                            if (errorvalues != null) {
                                urlMapListener.onError(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO NETWORK");
                            } else {
                                urlMapListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
                                        "INVALID RESPONSE OR DOWNLOAD FAILED");
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void getServiceLocaleWithLanguagePreference(final String serviceId, final OnGetServiceLocaleListener listener) {
        getServiceLocale(serviceId, listener, AISDResponse.AISDPreference.AISDLanguagePreference);
    }

    @Override
    public void getServiceLocaleWithCountryPreference(final String serviceId, final OnGetServiceLocaleListener listener) {
        getServiceLocale(serviceId, listener, AISDResponse.AISDPreference.AISDCountryPreference);
    }

    private void getServiceLocale(final String serviceId, final OnGetServiceLocaleListener localeListener,
                                  final AISDResponse.AISDPreference preference) {
        if (localeListener == null) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY,
                    "OnGetServiceLocaleUrlMapListener is null initialized");
        } else {
            if (serviceId == null) {
                localeListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "INVALID_INPUT");
            } else {
                getServiceDiscoveryData(new AISDListener() {
                    @Override
                    public void ondataReceived(AISDResponse response) {

                        if (response != null) {
                            if (response.isSuccess()) {
                                final String locale = response.getLocaleWithPreference(preference);
                                if (locale != null) {
                                    localeListener.onSuccess(locale);
                                } else {
                                    localeListener.onError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                                            "ServiceDiscovery cannot find the locale");
                                }
                            } else if (response.getError() != null) {
                                localeListener.onError(response.getError().getErrorvalue(), response.getError().getMessage());
                            }
                        } else {
                            if (errorvalues != null) {
                                localeListener.onError(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO NETWORK");
                            } else {
                                localeListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
                                        "INVALID RESPONSE OR DOWNLOAD FAILED");
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public URL applyURLParameters(URL inputURL, Map<String, String> parameters) {
        String url = inputURL.toString();
        URL output=null;
        if (parameters != null && parameters.size() > 0) {
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                final String key = param.getKey();
                final String value = param.getValue();
                if (key != null && value != null)
                    url = url.replace('%' + key + '%', value);
            }
        }
        try {
            output = new URL(url);
            return output;
        } catch (MalformedURLException ex) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,AppInfraLogEventID.AI_SERVICE_DISCOVERY, "ServiceDiscovery URL error Malformed URL");
            return null;
        }
    }

    private void getServiceDiscoveryData(final AISDListener listener) {
        final AISDResponse cachedData = mRequestItemManager.getCachedData();
        if (cachedData != null && !cachedURLsExpired()) {
            listener.ondataReceived(cachedData);
        } else {
            mRequestItemManager.clearCacheServiceDiscovery();
            queueResultListener(false, new AbstractDownloadItemListener() {
                @Override
                public void onDownloadDone(AISDResponse SDResponse) {
                    if (SDResponse != null && SDResponse.isSuccess()) {
                        listener.ondataReceived(SDResponse);
                    } else {
                        if (SDResponse != null && SDResponse.getError() != null) {
                            final ServiceDiscovery.Error err = SDResponse.getError();
                            err.setErrorvalue(err.getErrorvalue());
                            //   listener..onError(err.getErrorvalue(), err.getMessage());
                        } else {
                            listener.ondataReceived(null);
                        }
                    }
                }
            });
            //}
        }
    }

    @Override
    public void refresh(final OnRefreshListener listener) {
        refresh(listener, false);
    }

    @Override
    public void refresh(final OnRefreshListener listener, final boolean forcerefresh) {
        queueResultListener(forcerefresh, new AbstractDownloadItemListener() {
            @Override
            public void onDownloadDone(AISDResponse result) {
                if (result != null && result.isSuccess()) {
                    listener.onSuccess();
                } else {
                    if (result != null && result.getError() != null) {
                        final ServiceDiscovery.Error err = result.getError();
                        listener.onError(err.getErrorvalue(), err.getMessage());
                    } else {
                        if (errorvalues != null) {
                            listener.onError(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO NETWORK");
                        } else {
                            listener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
                                    "INVALID RESPONSE OR DOWNLOAD FAILED");
                        }
                    }
                }
            }

            @Override
            public boolean forceRefresh() {
                return forcerefresh;
            }
        });
    }

    private void homeCountryCode(final OnGetHomeCountryListener listener) {

        final String homeCountry = fetchFromSecureStorage(COUNTRY);
        final String countrySource = fetchFromSecureStorage(COUNTRY_SOURCE);
        if (homeCountry == null) {
            final String countryCode = getCountryCodeFromSim();
            if (countryCode != null) {
                countryCodeSource = OnGetHomeCountryListener.SOURCE.SIMCARD;
                saveToSecureStore(countryCode, countryCodeSource.toString());
                listener.onSuccess(countryCode, countryCodeSource);
            } else {
                queueResultListener(false, new AbstractDownloadItemListener() {
                    @Override
                    public void onDownloadDone(AISDResponse result) {
                        if (result != null && result.isSuccess()) {
                            final String country = result.getCountryCode();
                            if (country != null) {
                                countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
                                saveToSecureStore(country, countryCodeSource.toString());
                                listener.onSuccess(country, countryCodeSource);
                            } else {
                                if (result.getError() != null) {
                                    final ServiceDiscovery.Error err = result.getError();
                                    listener.onError(err.getErrorvalue(), err.getMessage());
                                }
                            }
                        } else {
                            if (errorvalues != null) {
                                listener.onError(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO NETWORK");
                            } else {
                                listener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
                                        "INVALID RESPONSE OR DOWNLOAD FAILED");
                            }
                        }
                    }
                });
            }
        } else if (countrySource != null) {
            listener.onSuccess(homeCountry, OnGetHomeCountryListener.SOURCE.valueOf(countrySource));
        } else {
            listener.onSuccess(homeCountry, null);
        }
    }

    private String getCountryCodeFromSim() {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                countryCode = simCountry.toUpperCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { //
                final String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    countryCode = networkCountry.toUpperCase(Locale.US);
                }
            }
            return countryCode;
        } catch (Exception e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY,"ServiceDiscovery URL error"+e.toString());
        }
        return countryCode;
    }

    private void saveToSecureStore(final String country, final String countrySource) {

        final SecureStorageInterface mSecureStorageInterface = mAppInfra.getSecureStorage();
        final SecureStorage.SecureStorageError mSecureStorageError = new SecureStorage.SecureStorageError();

        mSecureStorageInterface.storeValueForKey(COUNTRY, country, mSecureStorageError);

        mSecureStorageInterface.storeValueForKey(COUNTRY_SOURCE, countrySource, mSecureStorageError);

    }

    private String fetchFromSecureStorage(final String countrySource) {
        final SecureStorageInterface mSecureStorageInterface = mAppInfra.getSecureStorage();
        final SecureStorageInterface.SecureStorageError mSecureStorageError = new SecureStorageInterface.SecureStorageError();
        String value = "";
        if (countrySource.equals(COUNTRY)) {
            if (mCountry != null) {
                value = mCountry;
            } else {
                value = mSecureStorageInterface.fetchValueForKey(COUNTRY, mSecureStorageError);
            }
        } else if (countrySource.equals(COUNTRY_SOURCE)) {
            if (mCountrySourceType != null) {
                value = mCountrySourceType;
            } else {
                value = mSecureStorageInterface.fetchValueForKey(COUNTRY_SOURCE, mSecureStorageError);
            }
        }
        return value;
    }

    private boolean cachedURLsExpired() {

        if (mRequestItemManager.getPropositionEnabled(mAppInfra)) {
            final String mURLStringPlatform = getSDURLForType(AISDURLType.AISDURLTypePlatform);
            final String mSavedURLPlatform = mRequestItemManager.getUrlPlatform();

            if (mSavedURLPlatform != null && mURLStringPlatform != null) {
                //if previously saved URL differs from current platform URL, URLs are expired
                if (!mSavedURLPlatform.equals(mURLStringPlatform)) {
                    return true;
                }
            } else {
                return true;
            }

        } else {
            final String mURLStringPlatform = getSDURLForType(AISDURLType.AISDURLTypePlatform);
            final String mSavedURLPlatform = mRequestItemManager.getUrlPlatform();

            final String mURLStringProposition = getSDURLForType(AISDURLTypeProposition);
            final String mSavedURLProposition = mRequestItemManager.getUrlProposition();

            if (mSavedURLProposition != null && mURLStringProposition != null) {
                //if previously saved URL differs from current proposition URL, URLs are expired
                if (!mSavedURLProposition.equals(mURLStringProposition)) {
                    return true;
                }
            } else {
                return true;
            }

            if (mSavedURLPlatform != null && mURLStringPlatform != null) {
                //if previously saved URL differs from current platform URL, URLs are expired
                if (!mSavedURLPlatform.equals(mURLStringPlatform)) {
                    return true;
                }
            } else {
                return true;
            }
        }

        return mRequestItemManager.isServiceDiscoveryDataExpired();
    }

    /**
     * Sending the broadcast event .
     * @param data Updated home country code
     */
    private void sendBroadcast(final String data) {
        Intent intent = new Intent(AIL_SERVICE_DISCOVERY_HOMECOUNTRY_CHANGE_ACTION);
        intent.putExtra(AIL_HOME_COUNTRY, data);
        LocalBroadcastManager.getInstance(mAppInfra.getAppInfraContext())
                .sendBroadcast(intent);
    }

    @Override
    public void unRegisterHomeCountrySet(final BroadcastReceiver receiver) {
        if(receiver != null && mAppInfra.getAppInfraContext() != null)  {
            LocalBroadcastManager.getInstance(mAppInfra.getAppInfraContext())
                    .unregisterReceiver(receiver);
        } else {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY,
                    "unregister Home country update" + "context is null");
        }
    }

    @Override
    public void registerOnHomeCountrySet(final BroadcastReceiver receiver) {
        if(receiver != null && mAppInfra.getAppInfraContext() != null)  {
            LocalBroadcastManager.getInstance(mAppInfra.getAppInfraContext())
                    .registerReceiver(receiver, new IntentFilter(AIL_SERVICE_DISCOVERY_HOMECOUNTRY_CHANGE_ACTION));
        } else {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,
                    AppInfraLogEventID.AI_SERVICE_DISCOVERY, "unregister Home country update "+ "context is null");
        }
    }

    @SuppressWarnings({"unchecked"})
    private Map<String, String> getServiceDiscoveryCountryMapping() {
        final AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface
                .AppConfigurationError();
        if (mAppInfra.getConfigInterface() != null) {
            try {
                final Object countryMapping = mAppInfra.getConfigInterface().getPropertyForKey
                        ("servicediscovery.countryMapping", "appinfra", configError);
                if (countryMapping != null && countryMapping instanceof Map) {
                    return (Map<String, String>) countryMapping;
                }
            } catch (IllegalArgumentException exception) {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO,
                        "ServiceDiscovery-getServiceDiscoveryCountryMapping",
                        exception.toString());
            }
        }
        return null;
    }

    /**
     * The enum Aisdurl type.
     */
    enum AISDURLType {
        /**
         * Aisdurl type proposition aisdurl type.
         */
        AISDURLTypeProposition, /**
         * Aisdurl type platform aisdurl type.
         */
        AISDURLTypePlatform
    }

    /**
     * The interface Aisd listener.
     */
    interface AISDListener {
        /**
         * Ondata received.
         *
         * @param response the response
         */
        void ondataReceived(AISDResponse response);
    }

    /**
     * The abstract class for DownloadItem listener.
     */
    private abstract class AbstractDownloadItemListener {
        /**
         * Force refresh boolean.
         *
         * @return the boolean
         */
        public boolean forceRefresh() {
            return false;
        }

        /**
         * On download done.
         *
         * @param result the result
         */
        public abstract void onDownloadDone(AISDResponse result);
    }
}