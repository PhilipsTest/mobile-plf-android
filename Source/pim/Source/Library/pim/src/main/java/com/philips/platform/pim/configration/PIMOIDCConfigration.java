package com.philips.platform.pim.configration;

import com.google.gson.JsonObject;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.utilities.UserCustomClaims;

import net.openid.appauth.AuthorizationServiceConfiguration;

import java.util.Map;

/**
 * Responsible for providing configuration required for OIDC.
 */
public class PIMOIDCConfigration {
    private static final String USR_CLIENT_ID_KEY = "JanRainConfiguration.RegistrationClientID.";
    private static String TAG = PIMOIDCConfigration.class.getSimpleName();
    private static final String GROUP_PIM = "PIM";
    private static final String CLIENT_ID = "clientId";
    private AuthorizationServiceConfiguration authorizationServiceConfiguration;

    private AppInfraInterface appInfraInterface;

    public PIMOIDCConfigration() {
        this.appInfraInterface = PIMSettingManager.getInstance().getAppInfraInterface();
    }

    public PIMOIDCConfigration(AuthorizationServiceConfiguration authorizationServiceConfiguration) {
        this.authorizationServiceConfiguration = authorizationServiceConfiguration;
        this.appInfraInterface = PIMSettingManager.getInstance().getAppInfraInterface();
    }

    public AuthorizationServiceConfiguration getAuthorizationServiceConfiguration() {
        return authorizationServiceConfiguration;
    }

    /**
     * Fetch client id from App config
     *
     * @return client id
     */
    public String getClientId() {
        Object obj = getProperty(CLIENT_ID, GROUP_PIM);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public String getMigrationClientId() {
        Object obj = getProperty("migrationClientId", GROUP_PIM);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public String getRedirectUrl() {
        Object obj = getProperty("redirectUri", GROUP_PIM);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public String getMigrationRedirectUrl() {
        String redirectUri = getRedirectUrl();
        return redirectUri.replace(getClientId(), getMigrationClientId());
    }

    public String getLegacyClientID(){
        Object obj = getProperty("legacyClientId", GROUP_PIM);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    /**
     * Fetch rsid from App config
     *
     * @return rsid
     */
    public String getrsID() {
        Object obj = getProperty("rsids", GROUP_PIM);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public String getAPIKey() {
        Object obj = getProperty("apiKey", GROUP_PIM);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public String getMarketingOptinAPIKey() {
        Object obj = getProperty("marketingOptinApiKey", GROUP_PIM);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    private Object getProperty(String key, String group) {
        AppConfigurationInterface appConfigurationInterface = appInfraInterface.getConfigInterface();
        Object obj = appConfigurationInterface.getPropertyForKey(key, group, new AppConfigurationInterface.AppConfigurationError());
        return obj;
    }

    public String getCustomClaims() {
        JsonObject customClaimObject = new JsonObject();
        customClaimObject.add(UserCustomClaims.RECEIVE_MARKETING_EMAIL_CONSENT, null);
        customClaimObject.add(UserCustomClaims.RECEIVE_MARKETING_EMAIL_TIMESTAMP, null);
        customClaimObject.add(UserCustomClaims.SOCIAL_PROFILES, null);
        customClaimObject.add(UserCustomClaims.UUID, null);
        customClaimObject.add(UserCustomClaims.RECEIVE_MARKETING_EMAIL_OPTED_IN, null);

        JsonObject userInfo = new JsonObject();
        userInfo.add("userinfo", customClaimObject);
        PIMSettingManager.getInstance().getLoggingInterface().log(LoggingInterface.LogLevel.DEBUG, TAG, "PIM_KEY_CUSTOM_CLAIMS: " + userInfo.toString());
        return userInfo.toString();
    }
}
