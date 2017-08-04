/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.configuration;

import java.util.List;

import static com.philips.cdp.registration.configuration.URConfigurationConstants.*;

public class AppConfiguration extends BaseConfiguration {

    private static final String REGISTRATION_ENVIRONMENT = "appidentity.appState";
    private static final String MICROSITE_ID_KEY = "appidentity.micrositeId";
    private static final String WE_CHAT_APP_ID_KEY = "weChatAppId";
    private static final String WE_CHAT_APP_SECRET_KEY = "weChatAppSecret";
    private static final String CLIENT_ID_KEY = "JanRainConfiguration.RegistrationClientID.";

    public String getWeChatAppId() {
        Object weChatAppIdObject = appInfraWrapper.getURProperty(WE_CHAT_APP_ID_KEY);
        return getConfigPropertyValue(weChatAppIdObject);
    }

    public String getWeChatAppSecret() {
        Object weChatAppSecretObject = appInfraWrapper.getURProperty(WE_CHAT_APP_SECRET_KEY);
        return getConfigPropertyValue(weChatAppSecretObject);
    }

    public String getMicrositeId() {
        Object micrositeIdObject = appInfraWrapper.getAppInfraProperty(MICROSITE_ID_KEY);
        return getConfigPropertyValue(micrositeIdObject);
    }

    public String getRegistrationEnvironment() {
        return appInfraWrapper.getAppState().toString();
    }

    public String getClientId(String environment) {
        Object clientIdObject = appInfraWrapper.getURProperty(CLIENT_ID_KEY + environment);
        return getConfigPropertyValue(clientIdObject);
    }

    public String getCampaignId() {
        Object campaignIdObject = appInfraWrapper.getURProperty(PIL_CONFIGURATION_CAMPAIGN_ID);
        return getConfigPropertyValue(campaignIdObject);
    }

    public Object getEmailVerificationRequired() {
        return appInfraWrapper.getURProperty(FLOW_EMAIL_VERIFICATION_REQUIRED);
    }

    public Object getTermsAndConditionsAcceptanceRequired() {
        return appInfraWrapper.getURProperty(FLOW_TERMS_AND_CONDITIONS_ACCEPTANCE_REQUIRED);
    }

    public Object getMinimunAgeObject() {
        return appInfraWrapper.getURProperty(FLOW_MINIMUM_AGE_LIMIT);
    }

    public List<String> getProvidersForCountry(String countryCode) {
        Object providersObject = appInfraWrapper.getURProperty(SIGNIN_PROVIDERS + countryCode);
        if (providersObject != null) {
            return (List<String>) providersObject;
        }

        providersObject = appInfraWrapper.getURProperty(SIGNIN_PROVIDERS + DEFAULT);
        return (List<String>) providersObject;
    }


    public List<String> getSupportedHomeCountries() {
        Object providersObject = appInfraWrapper.getURProperty(SUPPORTED_HOME_COUNTRIES);
        if (providersObject != null) {
            return (List<String>) providersObject;
        }
        return null;
    }

    public String getFallBackHomeCountry() {
        Object providersObject = appInfraWrapper.getURProperty(FALLBACK_HOME_COUNTRY);
        if (providersObject != null) {
            return (String) providersObject;
        }
        return null;
    }


    public String getShowCountrySelection() {
        Object showCountrySelectionObject = appInfraWrapper.getURProperty(SHOW_COUNTRY_SELECTION);
        return getConfigPropertyValue(showCountrySelectionObject);
    }

}
