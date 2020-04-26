package com.philips.platform.pim.manager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.PIMParameterToLaunchEnum;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.listeners.PIMLoginListener;
import com.philips.platform.pim.listeners.PIMTokenRequestListener;
import com.philips.platform.pim.listeners.PIMUserMigrationListener;
import com.philips.platform.pim.listeners.PIMUserProfileDownloadListener;
import com.philips.platform.pim.utilities.PIMSecureStorageHelper;

import net.openid.appauth.AuthorizationRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

/**
 * Class to perform OIDC request during login
 */
public class PIMLoginManager {
    private String TAG = PIMLoginManager.class.getSimpleName();
    private PIMOIDCConfigration mPimoidcConfigration;
    private PIMAuthManager mPimAuthManager;
    private LoggingInterface mLoggingInterface;
    private PIMLoginListener mPimLoginListener;
    private AppTaggingInterface mTaggingInterface;
    private PIMUserManager mPimUserManager;
    private HashMap consentParameterMap;
    private PIMSecureStorageHelper pimSecureStorageHelper;

    public PIMLoginManager(Context context, PIMOIDCConfigration pimoidcConfigration, HashMap consentParameterMap) {
        mPimoidcConfigration = pimoidcConfigration;
        mPimAuthManager = new PIMAuthManager(context);
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
        mTaggingInterface = PIMSettingManager.getInstance().getTaggingInterface();
        mPimUserManager = PIMSettingManager.getInstance().getPimUserManager();
        pimSecureStorageHelper = new PIMSecureStorageHelper(PIMSettingManager.getInstance().getAppInfraInterface());
        this.consentParameterMap = consentParameterMap;
    }

    public Intent getAuthReqIntent(@NonNull PIMLoginListener pimLoginListener) throws ActivityNotFoundException {
        mPimLoginListener = pimLoginListener;
        final AuthorizationRequest authorizationRequest = mPimAuthManager.createAuthorizationRequest(mPimoidcConfigration, createAdditionalParameterForLogin());
        pimSecureStorageHelper.saveAuthorizationRequest(authorizationRequest);
        return mPimAuthManager.getAuthorizationRequestIntent(authorizationRequest);
    }

    public boolean isAuthorizationSuccess(Intent intentData) {
        return mPimAuthManager.isAuthorizationSuccess(intentData);
    }

    public void exchangeAuthorizationCode(@NonNull Intent dataIntent) {
        mPimAuthManager.performTokenRequestFromLogin(dataIntent, new PIMTokenRequestListener() {
            @Override
            public void onTokenRequestSuccess() {
                mPimUserManager.requestUserProfile(mPimAuthManager.getAuthState(), new PIMUserProfileDownloadListener() {
                    @Override
                    public void onUserProfileDownloadSuccess() {
                        mPimUserManager.saveLoginFlowType(PIMUserManager.LOGIN_FLOW.DEFAULT);
                        if (mPimLoginListener != null)
                            mPimLoginListener.onLoginSuccess();
                    }

                    @Override
                    public void onUserProfileDownloadFailed(Error error) {
                        if (mPimLoginListener != null)
                            mPimLoginListener.onLoginFailed(error);
                    }
                }); //Request user profile on success of token request
            }

            @Override
            public void onTokenRequestFailed(Error error) {
                if (mPimLoginListener != null)
                    mPimLoginListener.onLoginFailed(error);
            }
        });
    }

    public AuthorizationRequest createAuthRequestUriForMigration(Map<String, String> additionalParameter) {
        return mPimAuthManager.createAuthRequestUriForMigration(additionalParameter);
    }

    public void exchangeAuthorizationCodeForMigration(AuthorizationRequest authorizationRequest, String authResponse, PIMUserMigrationListener pimUserMigrationListener) {
        mPimAuthManager.performTokenRequestFromLogin(authorizationRequest, authResponse, new PIMTokenRequestListener() {
            @Override
            public void onTokenRequestSuccess() {
                mLoggingInterface.log(DEBUG, TAG, "exchangeAuthorizationCodeForMigration success");
                mPimUserManager.requestUserProfile(mPimAuthManager.getAuthState(), new PIMUserProfileDownloadListener() {
                    @Override
                    public void onUserProfileDownloadSuccess() {
                        mPimUserManager.saveLoginFlowType(PIMUserManager.LOGIN_FLOW.MIGRATION);
                        pimUserMigrationListener.onUserMigrationSuccess();
                    }

                    @Override
                    public void onUserProfileDownloadFailed(Error error) {
                        pimUserMigrationListener.onUserMigrationFailed(error);
                    }
                });
            }

            @Override
            public void onTokenRequestFailed(Error error) {
                mLoggingInterface.log(DEBUG, TAG, "exchangeAuthorizationCodeForMigration Failed. Error : " + error.getErrDesc());
                pimUserMigrationListener.onUserMigrationFailed(error);
            }
        });
    }

    /**
     * Creates additional parameter for authorization request intent
     *
     * @return map containing additional parameter in key-value pair
     */
    private Map<String, String> createAdditionalParameterForLogin() {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("claims", mPimoidcConfigration.getCustomClaims());
        parameter.put("consents", getConsentList());
        String urlString = "http://";
        String[] urlStringWithVisitorData = mTaggingInterface.getVisitorIDAppendToURL(urlString).split("=");
        mLoggingInterface.log(DEBUG, TAG, "External URL with Adobe_mc : " + urlStringWithVisitorData[1]);
        parameter.put("adobe_mc", urlStringWithVisitorData[1]);
        parameter.put("ui_locales", PIMSettingManager.getInstance().getLocale());
        parameter.put("analytics_report_suite_id", mPimoidcConfigration.getrsID());
        mLoggingInterface.log(DEBUG, TAG, "Additional parameters : " + parameter.toString());
        return parameter;
    }

    private String getConsentList() {
        AppTaggingInterface.PrivacyStatus privacyConsent = mTaggingInterface.getPrivacyConsent();
        boolean isAnalyticsEnabled = privacyConsent.equals(AppTaggingInterface.PrivacyStatus.OPTIN);
        boolean isABTesingEnabled = false;
        if (consentParameterMap != null && consentParameterMap.get(PIMParameterToLaunchEnum.PIM_AB_TESTING_CONSENT) != null
                && (Boolean) consentParameterMap.get(PIMParameterToLaunchEnum.PIM_AB_TESTING_CONSENT))
            isABTesingEnabled = true;

        List<String> consentList = new ArrayList<>();
        if(isAnalyticsEnabled)
            consentList.add(PIMParameterToLaunchEnum.PIM_ANALYTICS_CONSENT.pimConsent);
        if(isABTesingEnabled)
            consentList.add(PIMParameterToLaunchEnum.PIM_AB_TESTING_CONSENT.pimConsent);

        String consents = TextUtils.join(",",consentList);

        mLoggingInterface.log(DEBUG, TAG, "consent list parameters : " + consents);
        return consents;
    }

    public void exchangeCodeOnEmailVerify() {
        Intent authIntent = mPimAuthManager.extractResponseData(pimSecureStorageHelper.getAuthorizationResponse(), pimSecureStorageHelper.getAuthorizationRequest());
        pimSecureStorageHelper.deleteAuthorizationResponse();
        if (mPimAuthManager.isAuthorizationSuccess(authIntent))
            exchangeAuthorizationCode(authIntent);
    }
}

