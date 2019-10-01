package com.philips.platform.pim.migration;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.errors.PIMErrorEnums;
import com.philips.platform.pim.listeners.PIMUserMigrationListener;
import com.philips.platform.pim.manager.PIMLoginManager;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.rest.IDAssertionRequest;
import com.philips.platform.pim.rest.PIMMigrationAuthRequest;
import com.philips.platform.pim.rest.PIMRequestInterface;
import com.philips.platform.pim.rest.PIMRestClient;

import net.openid.appauth.AuthorizationRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

class PIMMigrationManager {

    private final String TAG = PIMMigrationManager.class.getSimpleName();
    private LoggingInterface mLoggingInterface;
    private Context mContext;
    private PIMLoginManager pimLoginManager;
    private AuthorizationRequest authorizationRequest;
    private PIMUserMigrationListener pimUserMigrationListener;
    private final String MIGRATION_BASE_URL = "userreg.janrainoidc.migration";

    public PIMMigrationManager(Context context, PIMUserMigrationListener pimUserMigrationListener) {
        mContext = context;
        this.pimUserMigrationListener = pimUserMigrationListener;
        pimLoginManager = new PIMLoginManager(mContext, PIMSettingManager.getInstance().getPimOidcConfigration());
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
    }

    void migrateUser(String usrAccessToken) {
        downloadIDAssertionUrlFromSD(new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                ServiceDiscoveryService serviceDiscoveryService = urlMap.get(MIGRATION_BASE_URL);
                String idAssertionUrl = serviceDiscoveryService.getConfigUrls();
                String locale = serviceDiscoveryService.getLocale();
                mLoggingInterface.log(DEBUG, TAG, "downloadIDAssertionUrlFromSD onSuccess. Url : " + idAssertionUrl + " Locale : " + locale);
                performIDAssertion(idAssertionUrl, usrAccessToken);
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                mLoggingInterface.log(DEBUG, TAG, "Migration Failed!! " + " Error in downloadIDAssertionUrlFromSD : " + message);
            }
        });
    }

    private void downloadIDAssertionUrlFromSD(ServiceDiscoveryInterface.OnGetServiceUrlMapListener serviceUrlMapListener) {
        ArrayList<String> serviceIdList = new ArrayList<>();
        serviceIdList.add(MIGRATION_BASE_URL);
        PIMSettingManager.getInstance().getAppInfraInterface().getServiceDiscovery().getServicesWithCountryPreference(serviceIdList, serviceUrlMapListener, null);
    }

    private void performIDAssertion(String idAssertionUrl, String usrAccessToken) {
        IDAssertionRequest idAssertionRequest = new IDAssertionRequest(idAssertionUrl, usrAccessToken);
        PIMRestClient pimRestClient = new PIMRestClient(PIMSettingManager.getInstance().getRestClient());
        pimRestClient.invokeRequest(idAssertionRequest, getSuccessListener(idAssertionRequest), getErrorListener(idAssertionRequest));
    }

    private void performAuthorization(String id_token_hint) {
        authorizationRequest = pimLoginManager.createAuthRequestUriForMigration(createAdditionalParameterForMigration(id_token_hint));
        if (authorizationRequest == null) {
            mLoggingInterface.log(DEBUG, TAG, "performAuthorization failed. Cause : authorizationRequest is null.");
            pimUserMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.MIGRATION_FAILED.getLocalisedErrorDesc(mContext, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
            return;
        }
        PIMMigrationAuthRequest pimMigrationAuthRequest = new PIMMigrationAuthRequest(authorizationRequest.toUri().toString());
        PIMRestClient pimRestClient = new PIMRestClient(PIMSettingManager.getInstance().getRestClient());
        HttpsURLConnection.setFollowRedirects(false);
        pimRestClient.invokeRequest(pimMigrationAuthRequest, getSuccessListener(pimMigrationAuthRequest), getErrorListener(pimMigrationAuthRequest));
    }

    private Response.Listener getSuccessListener(PIMRequestInterface reqType) {
        return (Response.Listener<String>) response -> {
            if (response == null) {
                pimUserMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.MIGRATION_FAILED.getLocalisedErrorDesc(mContext, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
                return;
            }
            if (reqType instanceof IDAssertionRequest) {
                String id_token_hint = parseIDAssertionFromJSONResponse(response);
                mLoggingInterface.log(DEBUG, TAG, "ID Assertion request success. ID_token_hint : " + id_token_hint);
                performAuthorization(id_token_hint);
            } else if (reqType instanceof PIMMigrationAuthRequest) {
                mLoggingInterface.log(DEBUG, TAG, "Token auth request failed."); //PIMMigrationAuthRequest response comes with 302 code and volley throw 302 response code in error.So,handling in error listener
                pimUserMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.MIGRATION_FAILED.getLocalisedErrorDesc(mContext, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
            }
        };
    }

    private Response.ErrorListener getErrorListener(PIMRequestInterface reqType) {
        return error -> {
            if (error == null) {
                mLoggingInterface.log(DEBUG, TAG, "Error response for" + reqType + "is null.");
                return;
            }

            if (reqType instanceof IDAssertionRequest) {
                mLoggingInterface.log(DEBUG, TAG, "Failed in ID Assertion Request. Error : " + error.getMessage());
                pimUserMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.MIGRATION_FAILED.getLocalisedErrorDesc(mContext, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
            } else if (reqType instanceof PIMMigrationAuthRequest) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 302) {
                    String authRsponse = networkResponse.headers.get("Location");
                    mLoggingInterface.log(DEBUG, TAG, "Authorization response success : " + authRsponse);
                    pimLoginManager.exchangeAuthorizationCodeForMigration(authorizationRequest, authRsponse, pimUserMigrationListener);
                } else {
                    mLoggingInterface.log(DEBUG, TAG, "Token auth request failed.");
                    pimUserMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.MIGRATION_FAILED.getLocalisedErrorDesc(mContext, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
                }
            }
        };
    }

    private Map<String, String> createAdditionalParameterForMigration(String id_token_hint) {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("id_token_hint", id_token_hint);
        parameter.put("claims", PIMSettingManager.getInstance().getPimOidcConfigration().getCustomClaims());
        return parameter;
    }


    private String parseIDAssertionFromJSONResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            String id_token_hint = dataObject.getString("identityAssertion");
            return id_token_hint;
        } catch (JSONException e) {
            mLoggingInterface.log(DEBUG, TAG, "parseIDAssertionFromJSONResponse failed. Error : " + e.getMessage());
            pimUserMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.MIGRATION_FAILED.getLocalisedErrorDesc(mContext, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
        }
        return null;
    }
}
