/*
 *
 *  * Copyright (c) Koninklijke Philips N.V. 2017
 *  * All rights are reserved. Reproduction or dissemination in whole or in part
 *  * is prohibited without the prior written consent of the copyright holder.
 *
 */
package com.philips.platform.aildemo.servicediscovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.AISDResponse;
import com.philips.platform.appinfra.servicediscovery.model.MatchByCountryOrLanguage;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscovery;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

//import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveyService;

/**
 * This class downloads list of URLs from service discovery server,
 * providing the locale and the identity of the application.
 */
public class ServiceDiscoveryManagerCSV implements ServiceDiscoveryInterface {

    private abstract class DownloadItemListener {
        public boolean forceRefresh() {
            return false;
        }

        public abstract void onDownloadDone(ServiceDiscovery result);
    }

    public static final String COUNTRY_PRREFERENCE = "COUNTRY_PRREFERENCE";
    private static final String COUNTRY = "country";
    private static final String COUNTRY_SOURCE = "country_source";

    private OnGetHomeCountryListener.SOURCE countryCodeSource;

    public static final String CANCELLED_REQUEST = "Request cancelled";

    private AppInfraInterface mAppInfra;
    private Context context;
    AISDResponse serviceDiscovery = null;
    //    boolean isDataAvailable = false;
//    public static boolean isDownloadInProgress = false;
    private String countryCode;
    //    private String mUrl = null;
    String mCountry;

    OnErrorListener.ERRORVALUES mErrorValues = null;
    String errorMessage = null;


    //
    private boolean downloadInProgress;
    private ArrayDeque<DownloadItemListener> downloadAwaiters;
    private ReentrantLock downloadLock;

    public ServiceDiscoveryManagerCSV() {
      /*  mAppInfra = aAppInfra;
        context = mAppInfra.getAppInfraContext();*/

        downloadInProgress = false;
        downloadAwaiters = new ArrayDeque<DownloadItemListener>();
        downloadLock = new ReentrantLock();
//        OnRefreshListener mOnRefreshListener = this;
        // Class shall not presume appInfra to be completely initialized at this point.
        // At any call after the constructor, appInfra can be presumed to be complete.

//        refresh(new OnRefreshListener() {
//            @Override
//            public void onSuccess() {
//                ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "refresh ", "refresh" );
//            }
//
//            @Override
//            public void onError(ERRORVALUES error, String message) {
//                ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "refresh", "refresh" );
//            }
//        });
    }
    public void init (AppInfraInterface aAppInfra){
        // to be called after builder pattern
        mAppInfra = aAppInfra;
        context = mAppInfra.getAppInfraContext();
        refresh(new OnRefreshListener() {
            @Override
            public void onSuccess() {
                Log.i("SD CSV", "" + "Initialized");
            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
                Log.i("SD CSV", "" + "Error");
            }
        },false);
    }



    protected JSONObject getMasterConfigFromApp() {
        JSONObject result = null;
        try {
            InputStream mInputStream = context.getAssets().open("AppConfig.json");
            BufferedReader r = new BufferedReader(new InputStreamReader(mInputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
            result = new JSONObject(total.toString());
            Log.v("MasterJson", result.toString());

        } catch (Exception e) {
            Log.v("AppConfig exception", e.getMessage());
        }
        return result;
    }


    private ServiceDiscovery getDataFromCSVFile(String fileType) {
        ServiceDiscovery mServiceDiscovery = null;
        try {
            InputStream mInputStream = context.getAssets().open(fileType);
            BufferedReader r = new BufferedReader(new InputStreamReader(mInputStream));
            StringBuilder total = new StringBuilder();
            String line;
            ArrayList<String> csvArrayStrings = new ArrayList<>();
            while ((line = r.readLine()) != null) {
                if (!line.equals(""))
                    csvArrayStrings.add(line);
//                total.append(line).append('\n');
            }
            String titleString = csvArrayStrings.get(0);
            csvArrayStrings.remove(0);
            List<String> titlesArray = Arrays.asList(titleString.split(","));

            HashMap<String, String> urlsMap = new HashMap<>();
            for (String serviceIDData : csvArrayStrings) {
                List<String> serviceIds = Arrays.asList(serviceIDData.split(","));

//                Log.i("serviceIds.get(0) " + serviceIds.get(0), "" + mAppInfra.getInternationalization().getUILocaleString());
//                Log.i("serviceIds.get(1) " + serviceIds.get(1), "" + mAppInfra.getAppIdentity().getAppState().toString());

                if (serviceIds.get(0).equals(mAppInfra.getInternationalization().getUILocaleString()) && serviceIds.get(2).equalsIgnoreCase(mAppInfra.getAppIdentity().getAppState().toString())) {

                    for (int i = 2; i < serviceIds.size(); i++) {
                        String serviceUrl = serviceIds.get(i);
                        String stringTitle = titlesArray.get(i);
//                        if(null!=serviceUrl&& !serviceUrl.isEmpty()) {
                            urlsMap.put(stringTitle, serviceUrl);
//                        }
                    }
                }
            }

            mServiceDiscovery = new ServiceDiscovery();
            JSONObject obj = new JSONObject();
            mServiceDiscovery.parseResponse(context,mAppInfra,obj); // to get context and AppInfra instance
            MatchByCountryOrLanguage
                    matchByCountry = new MatchByCountryOrLanguage();
            MatchByCountryOrLanguage.Config config = new MatchByCountryOrLanguage.Config();
            config.setUrls(urlsMap);
            ArrayList<MatchByCountryOrLanguage.Config> configArrayList = new ArrayList<>();
            configArrayList.add(config);
            matchByCountry.setConfigs(configArrayList);
            matchByCountry.setLocale(mAppInfra.getInternationalization().getUILocaleString());
            mServiceDiscovery.setSuccess(true);
            mServiceDiscovery.setCountry("IN");
            mServiceDiscovery.setMatchByCountry(matchByCountry);
            mServiceDiscovery.setMatchByLanguage(matchByCountry);



//            result = new JSONObject(total.toString());
            Log.v("csv file", csvArrayStrings.toString());

        } catch (Exception e) {
            Log.v("AppConfigexception", e.getMessage());
        }
        return mServiceDiscovery;
    }

   /* String buildUrl() {
        try {
            final AppIdentityInterface identityManager = mAppInfra.getAppIdentity(); // TODO RayKlo don't recreate existing instances
            final InternationalizationInterface localManager = mAppInfra.getInternationalization();
            Locale localeForURL = localManager.getUILocale();
            final AppIdentityInterface.AppState state = identityManager.getAppState();
            String service_environment = identityManager.getServiceDiscoveryEnvironment();
            String tags = null;
            String environment = null;
            if (state != null && service_environment != null) {
                switch (state) {
                    case DEVELOPMENT:
                        tags = "apps%2b%2benv%2bdev";
                        break;
                    case STAGING:
                        tags = "apps%2b%2benv%2bstage";
                        break;
                    case ACCEPTANCE:
                        tags = "apps%2b%2benv%2bacc";
                        break;
                    case TEST:
                        tags = "apps%2b%2benv%2btest";
                        break;
                    case PRODUCTION:
                        tags = "apps%2b%2benv%2bprod";
                        break;
                    default:
                        tags = "apps%2b%2benv%2btest";
                }

                if (service_environment.equalsIgnoreCase("PRODUCTION")) {
                    environment = "www.philips.com";
                } else if (service_environment.equalsIgnoreCase("TEST")) {
                    environment = "tst.philips.com";
                } else if (service_environment.equalsIgnoreCase("STAGING")) {
                    environment = "dev.philips.com";
                } else if (service_environment.equalsIgnoreCase("ACCEPTANCE")) {
                    environment = "acc.philips.com";
                } else {
                    environment = "tst.philips.com";
                }
            } else {
                // TODO RayKlo ??
            }

            String url = null;

            if (identityManager.getSector() != null && identityManager.getMicrositeId() != null &&
                    localManager.getUILocale() != null && tags != null && environment != null) {
                url = "https://" + environment + "/api/v1/discovery/" + identityManager.getSector() + "/" + identityManager.getMicrositeId() + "?locale=" + localeForURL.getLanguage() + "_" + localeForURL.getCountry() + "&tags=" + tags;

                String countryHome = getCountry(serviceDiscovery);
                if (countryHome != null) {
                    url += "&country=" + countryHome;
                }
                ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "URL", "" + url);
            } else {
                // TODO RayKlo ??
            }
            return url;
        } catch (Exception e) {
            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "ServiceDiscovery", e.toString());
        }
        return null;
    }*/

    /**
     * Gets the country from app according to settings/SIM/GEOIP
     *
     * @param listener
     */
    @Override
    public void getHomeCountry(OnGetHomeCountryListener listener) {
        if (listener == null) {
            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "Service Discovery",
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
    public void setHomeCountry(String countryCode) {
        //mAppInfra.getServiceDiscovery().setHomeCountry(countryCode); //
        this.countryCode = countryCode;
        if (countryCode != null) {
            countryCodeSource = OnGetHomeCountryListener.SOURCE.STOREDPREFERENCE;
            saveToSecureStore(countryCode, COUNTRY);
            saveToSecureStore(countryCodeSource.toString(), COUNTRY_SOURCE);
            serviceDiscovery = null;  // if there is no internet then also old SD value must be cleared.
            refresh(new OnRefreshListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(ERRORVALUES errorvalues, String s) {

                }
            },false);
        } else {
            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "Null not Allowed", "Null not Allowed");
        }
    }



    @Override
    public void getServicesWithLanguagePreference(ArrayList<String> serviceId, OnGetServiceUrlMapListener listener, Map<String, String> replacement) {
        getURlMAPwithLanguageOrCountry(serviceId, listener, replacement, AISDResponse.AISDPreference.AISDLanguagePreference);
    }




    @Override
    public void getServicesWithCountryPreference(ArrayList<String> serviceId, OnGetServiceUrlMapListener listener, Map<String, String> replacement) {
        getURlMAPwithLanguageOrCountry(serviceId, listener, replacement, AISDResponse.AISDPreference.AISDCountryPreference);
    }



    @Override
    public URL applyURLParameters(URL url, Map<String, String> map) {
        return null;
    }




    private void getURlMAPwithLanguageOrCountry(final ArrayList<String> serviceIds, final OnGetServiceUrlMapListener urlMapListener,
                                                final Map<String, String> replacement, final AISDResponse.AISDPreference preference) {
        if (urlMapListener == null) {
            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "Service Discovery",
                    "OnGetServiceUrlMapListener is null initialized");
        } else {
            if (serviceIds == null || serviceIds.isEmpty()) {
                urlMapListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "INVALID_INPUT");
            } else {
                HashMap<String, ServiceDiscoveryService> responseMap = serviceDiscovery.getServicesUrl(serviceIds,
                        preference, replacement);
                if (responseMap != null && responseMap.size() > 0) {
                    urlMapListener.onSuccess(responseMap);
                } else {
                    urlMapListener.onError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                            "ServiceDiscovery cannot find the locale");
                }
            }
        }
    }


    @Override
    public void refresh(final OnRefreshListener listener, final boolean forcerefresh) {

        String urlBuild = null;

        try {
           serviceDiscovery  = fetchServicesFromCSV();

            listener.onSuccess();
            Log.i("Request read ", "csv  file read");
        } catch (Exception e) {
            Log.i("Request read ", "csv  file could NOT be read");
        }



    }

    @Override
    public void unRegisterHomeCountrySet(BroadcastReceiver receiver) {
        if(receiver != null && mAppInfra.getAppInfraContext() != null)  {
            LocalBroadcastManager.getInstance(mAppInfra.getAppInfraContext())
                    .unregisterReceiver(receiver);
        } else {
            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY,
                    "unregister Home country update" + "context is null");
        }
    }

    @Override
    public void registerOnHomeCountrySet(BroadcastReceiver receiver) {
        if(receiver != null && mAppInfra.getAppInfraContext() != null)  {
            LocalBroadcastManager.getInstance(mAppInfra.getAppInfraContext())
                    .registerReceiver(receiver, new IntentFilter(AIL_SERVICE_DISCOVERY_HOMECOUNTRY_CHANGE_ACTION));
        } else {
            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,
                    AppInfraLogEventID.AI_SERVICE_DISCOVERY, "unregister Home country update "+ "context is null");
        }
    }

    private void homeCountryCode(final OnGetHomeCountryListener listener) {

        String homeCountry = fetchFromSecureStorage(COUNTRY);
        String countrySource = fetchFromSecureStorage(COUNTRY_SOURCE);
        if (homeCountry == null && countrySource == null) {
            String countryCode = getCountryCodeFromSim();
            if (countryCode != null) {
                saveToSecureStore(countryCode, COUNTRY);
                countryCodeSource = OnGetHomeCountryListener.SOURCE.SIMCARD;
                saveToSecureStore(countryCodeSource.name(), COUNTRY_SOURCE);
                listener.onSuccess(countryCode, countryCodeSource);
            } else {
                String country = serviceDiscovery.getCountryCode();
                if (country != null) {
                    if (countryCodeSource == null)
                        countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
                    saveToSecureStore(country, COUNTRY);
                    saveToSecureStore(countryCodeSource.name(), COUNTRY_SOURCE);
                    listener.onSuccess(country, countryCodeSource);
                } else {
                    if (serviceDiscovery.getError() != null) {
                        ServiceDiscovery.Error err = serviceDiscovery.getError();
                        listener.onError(err.getErrorvalue(), err.getMessage());
                    }
                }
            }
        } else {
            listener.onSuccess(homeCountry, OnGetHomeCountryListener.SOURCE.valueOf(countrySource.trim()));
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
            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "ServiceDiscovery URL error",
                    e.toString());
        }
        return countryCode;
    }

    private AISDResponse fetchServicesFromCSV() {
        AISDResponse response = new AISDResponse(mAppInfra);


        ServiceDiscovery platformService = null, propositionService = null;

        propositionService =    getDataFromCSVFile("77000.csv"); // For Proposition
        if (propositionService != null && propositionService.isSuccess()) {
            String country = fetchFromSecureStorage(COUNTRY);
            String countrySource = fetchFromSecureStorage(COUNTRY_SOURCE);
            if (country == null) {
                if (countrySource == null)
                    countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
                saveToSecureStore(propositionService.getCountry(), COUNTRY);
                saveToSecureStore(countryCodeSource.toString(), COUNTRY_SOURCE);
            }
            platformService =  getDataFromCSVFile("77001.csv"); // For Platform
        }
        if (platformService != null && propositionService != null) {
            if (propositionService.isSuccess() && platformService.isSuccess()) {
                response.setPlatformURLs(platformService);
                response.setPropositionURLs(propositionService);
            } else {
                ServiceDiscovery error = new ServiceDiscovery();
                error.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "DOWNLOAD FAILED"));
                error.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.SERVER_ERROR, "DOWNLOAD FAILED"));
                ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "DOWNLOAD FAILED");
            }
        } else {
            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call",
                    "Download failed");
        }
        return response;
    }




    private void saveToSecureStore(String country, String countryCode) {
        SecureStorageInterface ssi = mAppInfra.getSecureStorage();
        SecureStorageInterface.SecureStorageError mSecureStorage = new SecureStorageInterface.SecureStorageError();
        if (countryCode.equals(COUNTRY)) {
            ssi.storeValueForKey(COUNTRY, country, mSecureStorage);
        } else if (countryCode.equals(COUNTRY_SOURCE)) {
            ssi.storeValueForKey(COUNTRY_SOURCE, country, mSecureStorage);
        }
    }


    private String fetchFromSecureStorage(String countrySource) {
        SecureStorageInterface ssi = mAppInfra.getSecureStorage();
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        String value = null;
        if (countrySource.equals(COUNTRY)) {
            value = ssi.fetchValueForKey(COUNTRY, sse);
        } else if (countrySource.equals(COUNTRY_SOURCE)) {
            value = ssi.fetchValueForKey(COUNTRY_SOURCE, sse);
        }
        return value;
    }

}
