package com.philips.platform.pim.utilities;

import android.content.Context;
import android.net.Uri;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;

import net.openid.appauth.AuthorizationRequest;

import org.json.JSONException;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

public class PIMSecureStorageHelper {

    private final AppInfraInterface appInfra;
    private final String AUTHORIZATION_REQUEST = "AUTHORIZATION_REQUEST";
    private final String AUTHORIZATION_RESPONSE = "AUTHORIZATION_RESPONSE";

    public PIMSecureStorageHelper(AppInfraInterface appInfra) {
        this.appInfra = appInfra;
    }

    public boolean saveAuthorizationRequest(AuthorizationRequest authorizationRequest) {
        boolean isStored = appInfra.getSecureStorage().storeValueForKey(AUTHORIZATION_REQUEST, authorizationRequest.jsonSerializeString(), new SecureStorageInterface.SecureStorageError());
        return isStored;
    }

    public AuthorizationRequest getAuthorizationRequest() {
        String authorizationRequestString = appInfra.getSecureStorage().fetchValueForKey(AUTHORIZATION_REQUEST, new SecureStorageInterface.SecureStorageError());
        if (authorizationRequestString != null) {
            try {
                return AuthorizationRequest.jsonDeserialize(authorizationRequestString);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    public boolean saveAuthorizationResponse(String authorizationResponse) {
        boolean isStored = appInfra.getSecureStorage().storeValueForKey(AUTHORIZATION_RESPONSE, authorizationResponse, new SecureStorageInterface.SecureStorageError());
        return isStored;
    }

    public String getAuthorizationResponse() {
        String authorizationRequestString = appInfra.getSecureStorage().fetchValueForKey(AUTHORIZATION_RESPONSE, new SecureStorageInterface.SecureStorageError());
        return authorizationRequestString;
    }

    public boolean deleteAuthorizationResponse() {
        return appInfra.getSecureStorage().removeValueForKey(AUTHORIZATION_RESPONSE);
    }
}
