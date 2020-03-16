/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.configuration;

import androidx.annotation.NonNull;

import com.philips.cdp.registration.injection.RegistrationComponent;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.platform.pif.chi.datamodel.ConsentStates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.philips.cdp.registration.configuration.URConfigurationConstants.DEFAULT;

public class RegistrationConfiguration {
    private String TAG = "RegistrationConfiguration";

    @Inject
    HSDPConfiguration hsdpConfiguration;

    @Inject
    AppConfiguration appConfiguration;

    private RegistrationComponent component;

    private RegistrationFunction prioritisedFunction = RegistrationFunction.Registration;

    UserRegistrationUIEventListener userRegistrationUIEventListener;
    private ConsentStates personalConsent;

    public RegistrationComponent getComponent() {
        return component;
    }


    public void setComponent(RegistrationComponent component) {
        this.component = component;
        this.component.inject(this);
    }

    public UserRegistrationUIEventListener getUserRegistrationUIEventListener() {
        return userRegistrationUIEventListener;
    }

    public void setUserRegistrationUIEventListener(UserRegistrationUIEventListener userRegistrationUIEventListener) {
        this.userRegistrationUIEventListener = userRegistrationUIEventListener;
    }

    private static volatile RegistrationConfiguration registrationConfiguration;

    public static synchronized RegistrationConfiguration getInstance() {
        if (registrationConfiguration == null) {
            synchronized (RegistrationConfiguration.class) {
                if (registrationConfiguration == null) {
                    registrationConfiguration = new RegistrationConfiguration();
                }
            }
        }
        return registrationConfiguration;
    }

    public String getRegistrationClientId(@NonNull Configuration environment) {
        String registrationClient = appConfiguration.getClientId(environment.getValue());
        if (registrationClient != null) {
            if (isJSONValid(registrationClient)) {
                try {
                    JSONObject jsonObject = new JSONObject(registrationClient);
                    if (!jsonObject.isNull(RegistrationHelper.getInstance().getCountryCode())) {
                        registrationClient = (String) jsonObject.get(RegistrationHelper.
                                getInstance().getCountryCode());
                        RLog.d(TAG, "getRegistrationClientId : registrationClient :" + registrationClient +
                                "with given Country Code :" + RegistrationHelper.getInstance().getCountryCode());
                        return registrationClient;
                    } else if (!jsonObject.isNull(DEFAULT)) {
                        registrationClient = (String) jsonObject.get(DEFAULT);
                        RLog.d(TAG, "getRegistrationClientId : registrationClient :" + registrationClient + "with DEFAULT ");
                        return registrationClient;
                    }
                } catch (JSONException e) {
                    RLog.e(TAG, "getRegistrationClientId : exception  :" + e.getMessage());
                }
            }
        } else {
            RLog.e(TAG, "getRegistrationClientId : Registration client is null");
        }

        return registrationClient;
    }

    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
            RLog.d(TAG, "isJSONValid : exception JSONObject");
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
                RLog.e(TAG, "isJSONValid : exception JSONArray");
            } catch (JSONException ex1) {
                RLog.e(TAG, "isJSONValid : exception" + ex1.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Get Microsite Id
     *
     * @return String
     */
    public String getMicrositeId() {
        String micrositeId = appConfiguration.getMicrositeId();
        RLog.d(this.getClass().getSimpleName(), "Microsite ID is :" + micrositeId);
        if (null == micrositeId) {
            RLog.e(TAG, "getMicrositeId : Microsite ID is null");
        }
        return micrositeId;
    }

    /**
     * This should not be supported for longer time
     *
     * @return String
     */
    public List<String> getServiceDiscoveryCountries() {
        HashMap<String, String> sdCountryMapping = (HashMap<String, String>) appConfiguration.getServiceDiscoveryCountryMapping();
        if (null == sdCountryMapping) {
            RLog.e(TAG, "getServiceDiscoveryCountries: getServiceDiscoveryCountryMapping is null");
            return new ArrayList<>();
        }
        return new ArrayList<>(sdCountryMapping.keySet());
    }


    /**
     * Get Campaign Id
     *
     * @return String
     */
    public String getCampaignId() {
        String campaignId = appConfiguration.getCampaignId();
        if (null == campaignId) {
            RLog.e(TAG, "getCampaignId: Campaign ID is null");
        }
        return campaignId;
    }

    /**
     * Get Registarion Environment
     *
     * @return String
     */
    public String getRegistrationEnvironment() {
        String registrationEnvironment = appConfiguration.getRegistrationEnvironment();
        if (null == registrationEnvironment) {
            RLog.e(TAG, "getRegistrationEnvironment: Registration environment is null");
            return registrationEnvironment;
        }
        if (registrationEnvironment.equalsIgnoreCase("TEST"))
            return Configuration.TESTING.getValue();
        if (registrationEnvironment.equalsIgnoreCase("ACCEPTANCE"))
            return Configuration.EVALUATION.getValue();
        return registrationEnvironment;
    }


    public String getPRAPIKey(){
        String prApiKey = appConfiguration.getPRApiKey();
        if(null  == prApiKey){
            RLog.e(TAG, "getPRAPIKey:Product registration api key is null");
        }
        return prApiKey;
    }

    /**
     * Status of email verification required
     *
     * @return boolean
     */
    public boolean isEmailVerificationRequired() {
        Object obj = appConfiguration.getEmailVerificationRequired();
        if (obj != null) {
            return Boolean.parseBoolean((String) obj);
        }

        return true;
    }

    /**
     * Status of terms and condition accepatance
     *
     * @return boolean
     */
    public boolean isTermsAndConditionsAcceptanceRequired() {
        Object obj = appConfiguration.getTermsAndConditionsAcceptanceRequired();
        if (obj != null) {
            return Boolean.parseBoolean((String) obj);
        }
        return false;
    }

    /**
     * Status of terms and condition accepatance
     *
     * @return boolean
     */
    public boolean isPersonalConsentAcceptanceRequired() {
        Object obj = appConfiguration.getPersonalConsentAcceptanceRequired();
        if (obj != null) {
            RLog.d(TAG, "isPersonalConsentAcceptanceRequired : " + Boolean.parseBoolean((String) obj));
            return Boolean.parseBoolean((String) obj);
        }
        RLog.d(TAG, "isPersonalConsentAcceptanceRequired : false");
        return false;
    }

    /**
     * Status of HSDP UUID uploading
     *
     * @return boolean
     */
    public boolean isHsdpUuidShouldUpload() {
        Object obj = appConfiguration.getHSDPUuidUpload();
        if (obj != null) {
            RLog.d(TAG, "isHsdpUuidShouldUpload : " + Boolean.parseBoolean((String) obj));
            return Boolean.parseBoolean((String) obj);
        }
        RLog.d(TAG, "isHsdpUuidShouldUpload : false");
        return false;
    }

    /**
     * Status of Skipping HSDP log-in
     *
     * @return boolean
     */
    public boolean isHSDPSkipLoginConfigurationAvailable() {
        Object obj = appConfiguration.getDelayedHsdpLoginStatus();
        if (obj != null) {
            RLog.d(TAG, "isHSDPSkipLoginConfigurationAvailable : " + Boolean.parseBoolean((String) obj));
            return Boolean.parseBoolean((String) obj);
        }
        RLog.d(TAG, "isHSDPSkipLoginConfigurationAvailable : false");
        return false;
    }

    /**
     * get status of Facebook SDK true or false
     *
     * @return boolean
     */
    public boolean isFacebookSDKSupport() {
        Object obj = appConfiguration.getFacebookSDKSupportStatus();
        if (obj != null) {
            RLog.d(TAG, "isFacebookSDKSupport : " + Boolean.parseBoolean((String) obj));
            return Boolean.parseBoolean((String) obj);
        }
        RLog.d(TAG, "isFacebookSDKSupport : true");
        return true;
    }


    /**
     * get status of Facebook SDK true or false
     *
     * @return boolean
     */
    public boolean isCustomOptoin() {
        Object obj = appConfiguration.getCustomOptinStatus();
        if (obj != null) {
            RLog.d(TAG, "isCustomOptoin : " + Boolean.parseBoolean((String) obj));
            return Boolean.parseBoolean((String) obj);
        }
        RLog.d(TAG, "isCustomOptoin : true");
        return false;
    }

    /**
     * get status of Facebook SDK true or false
     *
     * @return boolean
     */
    public boolean isSkipOptin() {
        Object obj = appConfiguration.getSkipOptinStatus();
        if (obj != null) {
            RLog.d(TAG, "isSkipOptin : " + Boolean.parseBoolean((String) obj));
            return Boolean.parseBoolean((String) obj);
        }
        RLog.d(TAG, "isSkipOptin : true");
        return false;
    }







    /**
     * Get minimium age for country
     *
     * @param countryCode Country code
     * @return integer value of min age if mapping available else 0
     */
    public int getMinAgeLimitByCountry(String countryCode) {
        try {
            Object obj = appConfiguration.getMinimunAgeObject();
            if (obj != null) {
                JSONObject jsonObject = new JSONObject((String) obj);
                if (!jsonObject.isNull(countryCode)) {
                    return (int) jsonObject.get(countryCode);
                } else if (!jsonObject.isNull(DEFAULT)) {
                    return (int) jsonObject.get(DEFAULT);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get provoders
     *
     * @param countryCode Country code
     * @return List of providers
     */
    public List<String> getProvidersForCountry(String countryCode) {
        return appConfiguration.getProvidersForCountry(countryCode);
    }

    public List<String> getSupportedHomeCountry() {
        return appConfiguration.getSupportedHomeCountries();
    }

    public String getFallBackHomeCountry() {
        return appConfiguration.getFallBackHomeCountry();
    }

    public synchronized void setPrioritisedFunction(RegistrationFunction prioritisedFunction) {
        this.prioritisedFunction = prioritisedFunction;
    }

    public synchronized RegistrationFunction getPrioritisedFunction() {
        return prioritisedFunction;
    }

    private boolean isEnvironementSet() {
        return getRegistrationEnvironment() != null;
    }

    public boolean isHsdpFlow() {
        boolean bool = isEnvironementSet() && isHsdpInfoAvailable();
        RLog.i(TAG, "isHSDP = " + bool);
        return bool;
    }

    private boolean isHsdpInfoAvailable() {
        return hsdpConfiguration.getHsdpSecretId() != null
                && hsdpConfiguration.getHsdpSharedId() != null;
    }

//    public void setPersonalConsent(ConsentStates personalConsent) {
//        this.personalConsent = personalConsent;
//    }
//
//    public ConsentStates getPersonalConsent() {
//        return personalConsent;
//    }
}
