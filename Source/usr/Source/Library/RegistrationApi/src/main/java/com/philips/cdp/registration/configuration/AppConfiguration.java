/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.configuration;


import com.philips.cdp.registration.ui.utils.RLog;

import java.util.List;
import java.util.Map;

import static com.philips.cdp.registration.configuration.URConfigurationConstants.CUSTOMOPTIN;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.DEFAULT;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.FALLBACK_HOME_COUNTRY;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.FLOW_EMAIL_VERIFICATION_REQUIRED;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.FLOW_MINIMUM_AGE_LIMIT;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.FLOW_TERMS_AND_CONDITIONS_ACCEPTANCE_REQUIRED;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.HSDP_SKIP_LOGIN;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.HSDP_UUID_UPLOAD_IN_ANALYTICS;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.IS_FACEBOOK_SDK_SUPPORTED;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.PERSONAL_CONSENT_REQUIRED;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.PIL_CONFIGURATION_CAMPAIGN_ID;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.SHOW_COUNTRY_SELECTION;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.SIGNIN_PROVIDERS;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.SKIPOPTIN;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.SUPPORTED_HOME_COUNTRIES;

public class AppConfiguration extends BaseConfiguration {

    private final String TAG = "AppConfiguration";

    private static final String SD_COUNTRYMAPPING_ID_KEY = "servicediscovery.countryMapping";
    private static final String WE_CHAT_APP_ID_KEY = "weChatAppId";
    private static final String WE_CHAT_APP_SECRET_KEY = "weChatAppSecret";
    private static final String CLIENT_ID_KEY = "JanRainConfiguration.RegistrationClientID.";
    private static final String PR_API_KEY ="ApiKey";

    public String getWeChatAppId() {
        Object weChatAppIdObject = appInfraWrapper.getURProperty(WE_CHAT_APP_ID_KEY);
        String configPropertyValue = getConfigPropertyValue(weChatAppIdObject);
        RLog.d(TAG, "getWeChatAppId: " + configPropertyValue);
        RLog.i(TAG, "hasWeChatAppId: " + (configPropertyValue != null));
        return configPropertyValue;
    }

    public String getWeChatAppSecret() {
        Object weChatAppSecretObject = appInfraWrapper.getURProperty(WE_CHAT_APP_SECRET_KEY);
        String configPropertyValue = getConfigPropertyValue(weChatAppSecretObject);
        RLog.d(TAG, "getWeChatAppSecret: " + configPropertyValue);
        RLog.i(TAG, "hasWeChatAppSecret: " + (configPropertyValue != null));
        return configPropertyValue;

    }

    public String getMicrositeId() {
        Object micrositeIdObject = appInfraWrapper.getAppIdentity().getMicrositeId();
        String configPropertyValue = getConfigPropertyValue(micrositeIdObject);
        RLog.d(TAG, "getmicrositeIdObject: " + configPropertyValue);
        RLog.i(TAG, "hasmicrositeIdObject: " + (configPropertyValue != null));
        return configPropertyValue;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getServiceDiscoveryCountryMapping() {
        Object serviceDiscoveryCountryMappingObject = appInfraWrapper.getAppInfraProperty(SD_COUNTRYMAPPING_ID_KEY);
        Map<String, String> hashMap = null;
        if (serviceDiscoveryCountryMappingObject != null) {

            hashMap = (Map<String, String>) serviceDiscoveryCountryMappingObject;
            return hashMap;
        }
        RLog.d(TAG, "getServiceDiscoveryCountryMapping : ");
        return hashMap;
    }
    public String getPRApiKey(){
        RLog.d(TAG, "getPRApiKey : " + appInfraWrapper.getPRProperty(PR_API_KEY));
        return  appInfraWrapper.getPRProperty(PR_API_KEY).toString();
    }

    public String getRegistrationEnvironment() {
        RLog.d(TAG, "getRegistrationEnvironment : " + appInfraWrapper.getAppState().toString());
        return appInfraWrapper.getAppState().toString();
    }

    public String getClientId(String environment) {
        Object clientIdObject = appInfraWrapper.getURProperty(CLIENT_ID_KEY + environment);
        String configPropertyValue = getConfigPropertyValue(clientIdObject);
        RLog.d(TAG, "getclientId: " + configPropertyValue);
        RLog.i(TAG, "hasclientId : " + (configPropertyValue != null));
        return configPropertyValue;
    }

    public String getCampaignId() {
        Object campaignIdObject = appInfraWrapper.getURProperty(PIL_CONFIGURATION_CAMPAIGN_ID);
        String configPropertyValue = getConfigPropertyValue(campaignIdObject);
        RLog.d(TAG, "getCampaignId : " + configPropertyValue);
        RLog.i(TAG, "hasCampaignId : " + (configPropertyValue != null));
        return configPropertyValue;
    }

    public Object getEmailVerificationRequired() {
        RLog.d(TAG, "getEmailVerificationRequired  ");
        return appInfraWrapper.getURProperty(FLOW_EMAIL_VERIFICATION_REQUIRED);
    }

    public Object getTermsAndConditionsAcceptanceRequired() {
        RLog.d(TAG, "getTermsAndConditionsAcceptanceRequired  ");
        return appInfraWrapper.getURProperty(FLOW_TERMS_AND_CONDITIONS_ACCEPTANCE_REQUIRED);
    }

    public Object getMinimunAgeObject() {
        RLog.d(TAG, "getMinimunAgeObject  ");
        return appInfraWrapper.getURProperty(FLOW_MINIMUM_AGE_LIMIT);
    }

    @SuppressWarnings("unchecked")
    public List<String> getProvidersForCountry(String countryCode) {
        RLog.d(TAG, "getProvidersForCountry  ");
        Object providersObject = appInfraWrapper.getURProperty(SIGNIN_PROVIDERS + countryCode);
        if (providersObject != null) {
            return (List<String>) providersObject;
        }

        providersObject = appInfraWrapper.getURProperty(SIGNIN_PROVIDERS + DEFAULT);
        return (List<String>) providersObject;
    }

    @SuppressWarnings("unchecked")
    public List<String> getSupportedHomeCountries() {
        RLog.d(TAG, "getSupportedHomeCountries  ");
        Object providersObject = appInfraWrapper.getURProperty(SUPPORTED_HOME_COUNTRIES);
        List<String> providersObjects = null;
        if (providersObject != null) {
            providersObjects = (List<String>) providersObject;
            return providersObjects;
        }
        return providersObjects;
    }

    public String getFallBackHomeCountry() {
        RLog.d(TAG, "getFallBackHomeCountry  ");
        Object providersObject = appInfraWrapper.getURProperty(FALLBACK_HOME_COUNTRY);
        if (providersObject != null) {
            return (String) providersObject;
        }
        return null;
    }


    public String getShowCountrySelection() {
        RLog.d(TAG, "getShowCountrySelection  ");
        Object showCountrySelectionObject = appInfraWrapper.getURProperty(SHOW_COUNTRY_SELECTION);
        return getConfigPropertyValue(showCountrySelectionObject);
    }

    public Object getHSDPUuidUpload() {
        return appInfraWrapper.getURProperty(HSDP_UUID_UPLOAD_IN_ANALYTICS);
    }

    public Object getPersonalConsentAcceptanceRequired() {
        RLog.d(TAG, "getPersonalConsentAcceptanceRequired  ");
        return appInfraWrapper.getURProperty(PERSONAL_CONSENT_REQUIRED);
    }

    public Object getDelayedHsdpLoginStatus() {
        return appInfraWrapper.getURProperty(HSDP_SKIP_LOGIN);
    }

    public Object getFacebookSDKSupportStatus() {
        return appInfraWrapper.getURProperty(IS_FACEBOOK_SDK_SUPPORTED);
    }

    public Object getCustomOptinStatus() {
        return appInfraWrapper.getURProperty(CUSTOMOPTIN);
    }


    public Object getSkipOptinStatus() {
        return appInfraWrapper.getURProperty(SKIPOPTIN);
    }

}
