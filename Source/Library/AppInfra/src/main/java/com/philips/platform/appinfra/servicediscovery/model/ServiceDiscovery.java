/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.servicediscovery.model;

import android.content.Context;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.RequestManager;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.philips.platform.appinfra.servicediscovery.model.AISDResponse.AISDPreference.AISDCountryPreference;
import static com.philips.platform.appinfra.servicediscovery.model.AISDResponse.AISDPreference.AISDLanguagePreference;

/**
 * Created by 310238114 on 6/7/2016.
 */
public class ServiceDiscovery {

    private boolean success = false;
    String httpStatus;
    String country;
    private MatchByCountryOrLanguage matchByCountry;
    private MatchByCountryOrLanguage matchByLanguage;
    private AppInfra mAppInfra;
    private Context mContext;
    private ServiceDiscoveryManager mServiceDiscoveryManager;

    Error error = null;


    public static class Error {
        private String message;
        private ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES errorvalue = null;

        public Error(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES e, String m) {
            errorvalue = e;
            message = m;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES getErrorvalue() {
            return errorvalue;
        }

        public void setErrorvalue(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES errorvalues) {
            this.errorvalue = errorvalues;
        }
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }


    public MatchByCountryOrLanguage getMatchByCountry() {
        return matchByCountry;
    }

    public void setMatchByCountry(MatchByCountryOrLanguage matchByCountry) {
        this.matchByCountry = matchByCountry;
    }


    public MatchByCountryOrLanguage getMatchByLanguage() {
        return matchByLanguage;
    }

    public void setMatchByLanguage(MatchByCountryOrLanguage matchByLanguage) {
        this.matchByLanguage = matchByLanguage;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }


    public void parseResponse(Context context, AppInfra appInfra, JSONObject response) {
        this.mAppInfra = appInfra;
        this.mContext = context;
        try {
            setSuccess(response.optBoolean("success"));
            setHttpStatus(response.optString("httpStatus"));
            JSONObject payloadJSONObject = response.getJSONObject("payload");
            String country = response.getJSONObject("payload").optString("country");
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "ServiceDiscovery country",
                    country);
            this.country = country.toUpperCase();
            parseMatchByCountryJSON(payloadJSONObject.getJSONObject("matchByCountry"));
            parseMatchByLanguageJSON(payloadJSONObject.getJSONObject("matchByLanguage"));
        } catch (JSONException exception) {
            setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR, "ServiceDiscovery cannot find the locale");
        }
    }

    private void parseMatchByLanguageJSON(JSONObject response) {
        try {
            matchByLanguage = new MatchByCountryOrLanguage();
            matchByLanguage.setAvailable(response.optBoolean("available"));

            JSONArray resultsLanguageJSONArray = response.optJSONArray("results");
            if (null == resultsLanguageJSONArray) {
                resultsLanguageJSONArray = new JSONArray();
                resultsLanguageJSONArray.put(response.optJSONObject("results"));
            } else if (resultsLanguageJSONArray.length() > 0) {
                matchByLanguage.setLocale(resultsLanguageJSONArray.getJSONObject(0).optString("locale"));
                ArrayList<MatchByCountryOrLanguage.Config> matchByLanguageConfigs = new ArrayList<>();
                JSONArray configLanguageJSONArray = resultsLanguageJSONArray.getJSONObject(0).optJSONArray("configs");
                if (configLanguageJSONArray != null) {
                    for (int configCount = 0; configCount < configLanguageJSONArray.length(); configCount++) {
                        MatchByCountryOrLanguage.Config config = new MatchByCountryOrLanguage.Config();
                        config.parseConfigArray(configLanguageJSONArray.optJSONObject(configCount));
                        matchByLanguageConfigs.add(config);
                    }
                }
                matchByLanguage.setConfigs(matchByLanguageConfigs);
            }
            setMatchByLanguage(matchByLanguage);
        } catch (JSONException exception) {
            setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                    "ServiceDiscovery cannot find the locale");
        }
    }

    private void parseMatchByCountryJSON(JSONObject jsonObject) {
        try {
            matchByCountry = new MatchByCountryOrLanguage();
            this.matchByCountry.setAvailable(jsonObject.optBoolean("available"));
            JSONArray resultsJSONArray = jsonObject.optJSONArray("results");
            if (null == resultsJSONArray) {
                resultsJSONArray = new JSONArray();
                resultsJSONArray.put(jsonObject.optJSONObject("results"));
            }
            JSONArray configCountryJSONArray = null;
            String resConfig = getActualResultsForLocaleList(matchByCountry, resultsJSONArray);

            if (resConfig != null) {
                configCountryJSONArray = new JSONArray(resConfig);
            } else {
                if (resultsJSONArray.length() > 0) {
                    matchByCountry.setLocale(resultsJSONArray.getJSONObject(0).optString("locale")); // return first locale if nothing matches
                    configCountryJSONArray = resultsJSONArray.getJSONObject(0).optJSONArray("configs");
                }
            }
            if (configCountryJSONArray != null) {
                for (int configCount = 0; configCount < configCountryJSONArray.length(); configCount++) {
                    MatchByCountryOrLanguage.Config config = new MatchByCountryOrLanguage.Config();
                    config.parseConfigArray(configCountryJSONArray.optJSONObject(configCount));
                    this.matchByCountry.configs.add(config);
                }
                matchByCountry.setConfigs(matchByCountry.configs);
                setMatchByCountry(matchByCountry);
            } else {
                setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                        "ServiceDiscovery cannot find the locale");
            }

        } catch (JSONException e) {
            setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.UNKNOWN_ERROR, "Parse Error");
        }
    }

    private String getActualResultsForLocaleList(MatchByCountryOrLanguage matchByCountry,
                                                 JSONArray resultsJSONArray) {
        try {
            ArrayList<String> deviceLocaleList = new ArrayList<>(Arrays.asList(new RequestManager(mContext, mAppInfra)
                    .getLocaleList().split(",")));

            if (resultsJSONArray != null && resultsJSONArray.length() > 0) {
                for (int i = 0; i < deviceLocaleList.size(); i++) {
                    for (int j = 0; j < resultsJSONArray.length(); j++) {
                        String resLocale = resultsJSONArray.getJSONObject(j).optString("locale");
                        String deviceLocale = deviceLocaleList.get(i).replaceAll("[\\[\\]]", ""); // removing extra [] from locale list
                        if (deviceLocale.equals(resLocale)) {
                            matchByCountry.setLocale(resLocale);
                            return resultsJSONArray.getJSONObject(j).optString("configs");
                        } else if (deviceLocale.substring(0, 2).equals(resLocale.substring(0, 2))) { // comparing the language part of the locale
                            matchByCountry.setLocale(resLocale);
                            return resultsJSONArray.getJSONObject(0).optString("configs");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.UNKNOWN_ERROR, "Parse Error");
            e.printStackTrace();
        }
        return null;
    }


    private void setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES error, String message) {
        Error err = new ServiceDiscovery.Error(error, message);
        setError(err);
    }

    protected URL getServiceURLWithServiceID(String serviceId, AISDResponse.AISDPreference preference
            , Map<String, String> replacement) {
        URL url = null;
        try {
            if (serviceId != null) {
                if (preference.equals(AISDCountryPreference)) {
                    if (getMatchByCountry() != null && getMatchByCountry().getConfigs() != null) {
                        if (getMatchByCountry().getLocale() == null) {
                            setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                                    "ServiceDiscovery cannot find the locale");
                        } else {
                            String serviceID = getMatchByCountry().getConfigs().get(0).getUrls().get(serviceId);
                            if (serviceID != null) {
                                url = new URL(getMatchByCountry().getConfigs().get(0).getUrls().get(serviceId));
                            }
                        }
                    }
                } else if (preference.equals(AISDLanguagePreference)) {
                    if(getMatchByLanguage() != null && getMatchByLanguage().getConfigs() != null) {
                        if(getMatchByLanguage().getLocale() == null) {
                            setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                                    "ServiceDiscovery cannot find the locale");
                        } else {
                            String serviceID = getMatchByLanguage().getConfigs().get(0).getUrls().get(serviceId);
                            if (serviceID != null) {
                                url = new URL(getMatchByLanguage().getConfigs().get(0).getUrls().get(serviceId));
                            }
                        }
                    }
                }
                if (url != null) {
                    url = formatUrl(url, replacement);
                }
            } else {
                setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
                        "NO VALUE FOR KEY");
            }

        } catch (MalformedURLException Exception) {
            setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
                    "NO VALUE FOR KEY");
        }

        return url;
    }



    protected HashMap<String, ServiceDiscoveryService> getServicesWithServiceID(ArrayList<String> serviceIds,
                                                                                AISDResponse.AISDPreference preference,
                                                                                Map<String, String> replacement) {
        final HashMap<String, ServiceDiscoveryService> responseMap = new HashMap<>();
        if (serviceIds != null) {
            if (preference.equals(AISDCountryPreference)) {
                if (getMatchByCountry() != null && getMatchByCountry().getConfigs() != null) {
                    final int configSize = getMatchByCountry().getConfigs().size();
                    return formatMappedUrl(configSize, AISDCountryPreference, serviceIds, replacement);
                } else {
                    setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                            "ServiceDiscovery cannot find the locale");
                }
            } else if (preference.equals(AISDLanguagePreference)) {
                if (getMatchByLanguage() != null && getMatchByLanguage().getConfigs() != null) {
                    final int configSize = getMatchByLanguage().getConfigs().size();
                    return formatMappedUrl(configSize, AISDLanguagePreference, serviceIds, replacement);
                } else {
                    setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                            "ServiceDiscovery cannot find the locale");
                }
            }
        } else {
            setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
                    "INVALID INPUT");
        }
        return responseMap;
    }


    private HashMap<String, ServiceDiscoveryService> formatMappedUrl(int configSize, AISDResponse.AISDPreference
            preference, ArrayList<String> serviceIds, Map<String, String> replacement) {
        Map<String, String> urls = null;
        String modelLocale = null;
        mServiceDiscoveryManager = new ServiceDiscoveryManager(mAppInfra);

        final HashMap<String, ServiceDiscoveryService> responseMap = new HashMap<>();

        for (int config = 0; config < configSize; config++) {
            if (preference.equals(AISDCountryPreference)) {
                modelLocale = getMatchByCountry().getLocale();
                urls = getMatchByCountry().getConfigs().get(config).getUrls();
            } else if (preference.equals(AISDLanguagePreference)) {
                modelLocale = getMatchByLanguage().getLocale();
                urls = getMatchByLanguage().getConfigs().get(config).getUrls();
            }

            for (int i = 0; i < serviceIds.size(); i++) {
                if (urls != null) {
                    for (final String key : urls.keySet()) {
                        if (key.equalsIgnoreCase(serviceIds.get(i).trim())) {
                            String serviceUrlval = urls.get(key);
                            if (serviceUrlval.contains("%22")) {
                                serviceUrlval = serviceUrlval.replace("%22", "\"");
                            }
                            if (replacement != null && replacement.size() > 0) {
                                URL replacedUrl;
                                try {
                                    ServiceDiscoveryService sdService = new ServiceDiscoveryService();
                                    replacedUrl = mServiceDiscoveryManager.applyURLParameters(new URL(serviceUrlval), replacement);
                                    if(replacedUrl != null)
                                        sdService.init(modelLocale, replacedUrl.toString());
                                        responseMap.put(key, sdService);

                                } catch (MalformedURLException e) {
                                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,
                                            "ServiceDiscovery URL error",
                                            "Malformed URL");
                                }
                            } else {
                                ServiceDiscoveryService sdService = new ServiceDiscoveryService();
                                sdService.init(modelLocale, serviceUrlval);
                                responseMap.put(key, sdService);
                            }
                        }
                    }
                } else {
                    setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                            "ServiceDiscovery cannot find the locale");
                }
            }
        }
        if (responseMap.isEmpty()) {
            setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                    "ServiceDiscovery cannot find the locale");
        } else {
            return responseMap;
        }

        return responseMap;
    }


    private URL formatUrl(URL url, Map<String, String> replacement) {
        mServiceDiscoveryManager = new ServiceDiscoveryManager(mAppInfra);
        try {
            if (url.toString().contains("%22")) {
                url = new URL(url.toString().replace("%22", "\""));
            }
            if (replacement != null && replacement.size() > 0) {
                return mServiceDiscoveryManager.applyURLParameters(url, replacement);
            } else {
                return url;
            }

        } catch (MalformedURLException exception) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "ServiceDiscovery error",
                    exception.toString());
            setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "NO VALUE FOR KEY");
        }
        return url;
    }

    protected String getLocaleWithPreference(AISDResponse.AISDPreference preference) {
        if (preference.equals(AISDCountryPreference)) {
            if (getMatchByCountry() != null && getMatchByCountry().getConfigs() != null) {
                if (getMatchByCountry().getLocale() != null) {
                    return getMatchByCountry().getLocale();
                } else {
                    setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                            "ServiceDiscovery cannot find the locale");
                }
            }
        } else if (preference.equals(AISDLanguagePreference)) {
            if (getMatchByLanguage() != null && getMatchByLanguage().getConfigs() != null) {

                if (getMatchByLanguage().getLocale() != null) {
                    return getMatchByLanguage().getLocale();
                } else {
                    setError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
                            "ServiceDiscovery cannot find the locale");
                }
            }
        }
        return null;
    }
}
