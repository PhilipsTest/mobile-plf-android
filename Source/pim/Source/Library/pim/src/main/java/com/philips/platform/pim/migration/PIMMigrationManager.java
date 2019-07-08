package com.philips.platform.pim.migration;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pim.manager.PIMLoginManager;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.rest.IDAssertionRequest;
import com.philips.platform.pim.rest.PIMRequestInterface;
import com.philips.platform.pim.rest.PIMRestClient;
import com.philips.platform.pim.rest.TokenAuthRequest;

import net.openid.appauth.AuthorizationRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

class PIMMigrationManager {

    //TODO: Shashi, This is temporary, Need to fetch from OIDC configuration later.
    private String ID_ASSERTION_ENDPOINT = "https://stg.api.eu-west-1.philips.com/consumerIdentityService/identityAssertions/";

    private final String TAG = PIMMigrationManager.class.getSimpleName();
    private LoggingInterface mLoggingInterface;
    private Context mContext;
    private PIMLoginManager pimLoginManager;
    private AuthorizationRequest authorizationRequest;

    public PIMMigrationManager(Context context) {
        mContext = context;
        pimLoginManager = new PIMLoginManager(context,PIMSettingManager.getInstance().getPimOidcConfigration());
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
    }

    void migrateUser(String usrAccessToken) {
        performIDAssertion(usrAccessToken);
    }

    private void performIDAssertion(String usrAccessToken) {
        IDAssertionRequest idAssertionRequest = new IDAssertionRequest(ID_ASSERTION_ENDPOINT, usrAccessToken);
        PIMRestClient pimRestClient = new PIMRestClient(PIMSettingManager.getInstance().getRestClient());
        pimRestClient.invokeRequest(idAssertionRequest, getSuccessListener(idAssertionRequest), getErrorListener(idAssertionRequest));
    }

    private void performAuthorization(String id_token_hint) {
        authorizationRequest = pimLoginManager.createAuthRequestUriForMigration(createAdditionalParameterForMigration(id_token_hint));
        if (authorizationRequest == null) {
            mLoggingInterface.log(DEBUG, TAG, "performAuthorization failed. Cause : authorizationRequest is null.");
            return;
        }

        TokenAuthRequest tokenAuthRequest = new TokenAuthRequest(authorizationRequest.toUri().toString());
        PIMRestClient pimRestClient = new PIMRestClient(PIMSettingManager.getInstance().getRestClient());
        HttpsURLConnection.setFollowRedirects(false);
        pimRestClient.invokeRequest(tokenAuthRequest,getSuccessListener(tokenAuthRequest),getErrorListener(tokenAuthRequest));
    }

    private Response.Listener getSuccessListener(PIMRequestInterface reqType) {
        return (Response.Listener<String>) response -> {
            if (reqType instanceof IDAssertionRequest) {
                String id_token_hint = parseIDAssertionFromJSONResponse(response);
                mLoggingInterface.log(DEBUG, TAG, "ID Assertion request success. ID_token_hint : " + id_token_hint);
                performAuthorization(id_token_hint);
            }else if(reqType instanceof TokenAuthRequest)
                mLoggingInterface.log(DEBUG, TAG, "Token auth request failed."); //TokenAuthRequest response comes with 302 code and volley throw 302 response code in error.So,handling in error listener
        };
    }

    private Response.ErrorListener getErrorListener(PIMRequestInterface reqType) {
        return error -> {
            if (reqType instanceof IDAssertionRequest)
                mLoggingInterface.log(DEBUG, TAG, "Failed in ID Assertion Request. Error : " + error.getMessage());
            else if(reqType instanceof TokenAuthRequest){
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 302) {
                    String authRsponse = networkResponse.headers.get("Location");
                    mLoggingInterface.log(DEBUG, TAG, "Authorization response success : " + authRsponse);
                    pimLoginManager.exchangeAuthorizationCodeForMigration(authorizationRequest,authRsponse);
                }else
                    mLoggingInterface.log(DEBUG, TAG, "Token auth request failed.");
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
            String data = jsonObject.getString("data");
            if (data == null) {
                mLoggingInterface.log(DEBUG, TAG, "parseIDAssertionFromJSONResponse failed. data object is null");
            } else {
                JSONObject dataObject = new JSONObject(data);
                String id_token_hint = dataObject.getString("identityAssertion");
                return id_token_hint;
            }
        } catch (JSONException e) {
            mLoggingInterface.log(DEBUG, TAG, "parseIDAssertionFromJSONResponse failed. Error : " + e.getMessage());
        }
        return null;
    }
}
