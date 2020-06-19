/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.servicediscovery;

import android.content.BroadcastReceiver;

import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * The ServiceDiscovery Interface .
 */
public interface ServiceDiscoveryInterface extends Serializable {

    /**
     * Intent action to notify service discovery home country change.
     *
     * @since 1804.0
     */
    String AIL_SERVICE_DISCOVERY_HOMECOUNTRY_CHANGE_ACTION = "ail.servicediscovery.homecountryChanged";

    /**
     * Constant to get home country from broadcast intent.
     *
     * @since 1804.0
     */
    String AIL_HOME_COUNTRY = "ail.servicediscovery.homeCountry";

    /**
     * This is the callback method Error cases.
     * the call back will have Error method for actions completed with Errors.
     * onSuccess returns the Error response type
     *
     * @since 1.0.0
     */
    interface OnErrorListener {
        enum ERRORVALUES {
            NO_NETWORK, CONNECTION_TIMEOUT, SERVER_ERROR, SECURITY_ERROR,
            INVALID_RESPONSE, UNKNOWN_ERROR, NO_SERVICE_LOCALE_ERROR
        }

        void onError(ERRORVALUES error, String message);
    }

    /**
     * This is the callback method from getHomeCountry() API.
     * the call back will have success method for actions completed successfully.
     * onSuccess returns the successful response
     *
     * @since 1.0.0
     */
    interface OnGetHomeCountryListener extends OnErrorListener {
        enum SOURCE {STOREDPREFERENCE, SIMCARD, GEOIP}

        void onSuccess(String countryCode, SOURCE source);
    }


    /**
     * This is the callback method from getServiceUrlWithLanguagePreference() API.
     * the call back will have success method for actions completed successfully.
     * onSuccess returns the successful response
     *
     * @since 1.0.0
     */
    interface OnGetServiceUrlMapListener extends OnErrorListener {
        void onSuccess(Map<String, ServiceDiscoveryService> urlMap);
    }


    /**
     * This is the callback method from refresh() API.
     * the call back will have success method for actions completed successfully
     *
     * @since 1.0.0
     */
    interface OnRefreshListener extends OnErrorListener {
        void onSuccess();
    }

    /**
     * Fetches the Persistently stored Home country, the value is taken from the variable which has been set by setHomeCountry API.
     * Changing the country automatically clears the cached service list and triggers a refresh.
     *
     * @param listener country code to persistently store, code must be according to ISO 3166-1
     * @since 1.0.0
     */
    void getHomeCountry(OnGetHomeCountryListener listener);

    /**
     * Fetches the Persistently stored Home country, the value is taken from the variable which has been set by setHomeCountry API.
     * Changing the country automatically clears the cached service list and triggers a refresh.
     *
     * @return Persistently stored Home country
     * @since 1.0.0
     */
    String getHomeCountry();

    /**
     * Persistently store Home country, overwrites any existing country value.
     * Changing the country automatically clears the cached service list and triggers a refresh.
     *
     * @param countryCode country code to persistently store, code must be according to ISO 3166-1
     * @since 1.0.0
     */
    void setHomeCountry(String countryCode);


    /**
     * Hashmap with  URL  mapped specific service with a preference for the current language.
     *
     * @param serviceId   ArrayList of the service for which the URL is to be retrieved
     * @param listener    asynchronously returns using onSuccess the URL of the requested service;
     *                    or returns onError the error code when retrieval failed.
     * @param replacement lookup table to be use to replace placeholders (key) with the given value, see {@link #applyURLParameters(URL, Map)}
     * @since 1.0.0
     */
    void getServicesWithLanguagePreference(ArrayList<String> serviceId, OnGetServiceUrlMapListener listener,
                                           Map<String, String> replacement);

    /**
     * Hashmap with  URL  mapped for a specific service with a
     * preference for the current home country.
     *
     * @param serviceId   List of the services for which the URL is to be retrieved
     * @param listener    asynchronously returns using onSuccess the URL of the requested service;
     *                    or returns onError the error code when retrieval failed.
     * @param replacement lookup table to be use to replace placeholders (key) with the given value, see {@link #applyURLParameters(URL, Map)}
     * @since 1.0.0
     */
    void getServicesWithCountryPreference(ArrayList<String> serviceId, OnGetServiceUrlMapListener listener,
                                          Map<String, String> replacement);


//    /**
//     * Locale to be used for a specific service with a preference for the current home country.
//     *
//     * @param serviceId name of the service for which the URL is to be retrieved
//     * @param listener  asynchronously returns using onSuccess the recommended locale for the requested service;
//     *                  or returns onError the error code when retrieval failed.
//     * @since 1.0.0
//     * @deprecated Deprecated since 1901.0
//     */
//    @Deprecated
//    void getServiceLocaleWithCountryPreference(String serviceId, OnGetServiceLocaleListener listener);

    /**
     * Replaces all '%key%' placeholders in the given URL, where the key is the key in the replacement table and the placeholder is replaced with the value of the entry in the replacement table
     *
     * @param url         url in which to search for the key strings given by replacement
     * @param replacement mapping of placeholder string (key) to the replacement string (value)
     * @return input url with all placeholders keys replaced with the respective value in the replacement table
     * @since 1.0.0
     */
    URL applyURLParameters(URL url, Map<String, String> replacement);


    /**
     * Start negotiation with cloud service for the list of service for this application. List is based on sector, microsite, home country, and language.
     * The retrieved list is cached internally (not persistently).
     * The cached list is automatically refreshed every 24hours.
     * A refresh is only required, to ensure the very first service request after app start can be processed from the cache quickly, or when a previous sync failed.
     *
     * @param listener     asynchronously returns using onSuccess when retrieval was successful;
     *                     or returns onError the error code when retrieval failed.
     * @param forceRefresh true or false
     * @since 1.0.0
     */
    void refresh(OnRefreshListener listener, boolean forceRefresh);

    /**
     * UnRegister for the updating home country .
     *
     * @param receiver BroadcastReceiver
     * @since 1.0.0
     */
    void unRegisterHomeCountrySet(BroadcastReceiver receiver);

    /**
     * Register for the home country update.
     *
     * @param receiver BroadcastReceiver
     * @since 1.0.0
     */
    void registerOnHomeCountrySet(BroadcastReceiver receiver);

//    /**
//     * This is the callback method from getServiceUrlWithLanguagePreference() API.
//     * the call back will have success method for actions completed successfully.
//     * onSuccess returns the successful response
//     *
//     * @since 1.0.0
//     * @deprecated Deprecated since 1901.0
//     */
//    @Deprecated
//    interface OnGetServiceUrlListener extends OnErrorListener {
//        void onSuccess(URL url);
//    }

//    /**
//     * This is the callback method from getServiceLocaleWithLanguagePreference() API.
//     * the call back will have success method for actions completed successfully.
//     * onSuccess returns the successful response
//     *
//     * @since 1.0.0
//     * @deprecated Deprecated since 1901.0
//     */
//    @Deprecated
//    interface OnGetServiceLocaleListener extends OnErrorListener {
//        void onSuccess(String locale);
//    }



}
