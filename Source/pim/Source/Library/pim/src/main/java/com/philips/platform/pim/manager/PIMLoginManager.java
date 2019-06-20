package com.philips.platform.pim.manager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.adobe.mobile.Analytics;
import com.google.gson.JsonObject;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.pim.listeners.PIMTokenRequestListener;
import com.philips.platform.pim.utilities.UserCustomClaims;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.listeners.PIMLoginListener;
import com.philips.platform.pim.listeners.PIMUserProfileDownloadListener;

import java.util.HashMap;
import java.util.Map;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

/**
 * Class to perform OIDC request during login
 */
//TODO: Shashi,Handle backend issues in test case(Such as invalid client id)
public class PIMLoginManager implements PIMUserProfileDownloadListener {
    private String TAG = PIMLoginManager.class.getSimpleName();
    private PIMOIDCConfigration mPimoidcConfigration;
    private PIMAuthManager mPimAuthManager;
    private LoggingInterface mLoggingInterface;
    private PIMLoginListener mPimLoginListener;
    private AppTaggingInterface mTaggingInterface;

    public PIMLoginManager(Context context,PIMOIDCConfigration pimoidcConfigration) {
        mPimoidcConfigration = pimoidcConfigration;
        mPimAuthManager = new PIMAuthManager(context);
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
        mTaggingInterface = PIMSettingManager.getInstance().getTaggingInterface();
    }

     public Intent getAuthReqIntent(@NonNull PIMLoginListener pimLoginListener) throws ActivityNotFoundException {
        mPimLoginListener = pimLoginListener;
        String clientID = mPimoidcConfigration.getClientId();
        return mPimAuthManager.getAuthorizationRequestIntent(mPimoidcConfigration.getAuthorizationServiceConfiguration(), clientID, createAdditionalParameterForLogin());
    }

    public boolean isAuthorizationSuccess(Intent intentData){
        return mPimAuthManager.isAuthorizationSuccess(intentData);
    }


    public void exchangeAuthorizationCode(@NonNull Intent dataIntent) {
        mPimAuthManager.performTokenRequest(dataIntent, new PIMTokenRequestListener() {
            @Override
            public void onTokenRequestSuccess() {
                PIMUserManager pimUserManager = PIMSettingManager.getInstance().getPimUserManager();
                pimUserManager.requestUserProfile(mPimAuthManager.getAuthState(), PIMLoginManager.this); //Request user profile on success of token request
            }

            @Override
            public void onTokenRequestFailed(Error error) {
                if (mPimLoginListener != null)
                    mPimLoginListener.onLoginFailed(error);
            }
        });
    }
    public void disposeAuthorizationService(){
        if(mPimAuthManager != null)
            mPimAuthManager.dispose();
    }

    @Override
    public void onUserProfileDownloadSuccess() {
        mPimLoginListener.onLoginSuccess();
    }

    @Override
    public void onUserProfileDownloadFailed(Error error) {
        mPimLoginListener.onLoginFailed(error);
    }

    /**
     * Creates additional parameter for authorization request intent
     * @return map containing additional parameter in key-value pair
     */
    private Map<String, String> createAdditionalParameterForLogin() {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("claims", getCustomClaims());
        parameter.put("cookie_consent", String.valueOf(mTaggingInterface.getPrivacyConsent()));//String.valueOf(Config.getPrivacyStatus() == MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_IN));
        if (Analytics.getTrackingIdentifier() != null) {
            parameter.put("adobe_mc", mTaggingInterface.getTrackingIdentifier());
        } else {
            mLoggingInterface.log(DEBUG, TAG, "ADBMobile tracking Identifier is not set.");
        }
        parameter.put("ui_locales", PIMSettingManager.getInstance().getLocale());
        parameter.put("app_rep",new PIMOIDCConfigration().getrsID());
        mLoggingInterface.log(DEBUG, TAG, "Additional parameters : " + parameter.toString());
        return parameter;
    }

    /**
     * Construct json object to store custom claims
     *
     * @return json string
     */
    private String getCustomClaims() {
        JsonObject customClaimObject = new JsonObject();
        customClaimObject.add(UserCustomClaims.RECEIVE_MARKETING_EMAIL_CONSENT,null);
        customClaimObject.add(UserCustomClaims.RECEIVE_MARKETING_EMAIL_TIMESTAMP,null);
        customClaimObject.add(UserCustomClaims.SOCIAL_PROFILES,null);
        customClaimObject.add(UserCustomClaims.UUID,null);

        JsonObject userInfo = new JsonObject();
        userInfo.add("userinfo", customClaimObject);
        PIMSettingManager.getInstance().getLoggingInterface().log(LoggingInterface.LogLevel.DEBUG, TAG, "PIM_KEY_CUSTOM_CLAIMS: " + userInfo.toString());
        return userInfo.toString();
    }
}

