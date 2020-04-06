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

    private final Context context;
    private final AppInfraInterface appInfra;

    public PIMSecureStorageHelper(Context context, AppInfraInterface appInfra) {
        this.context = context;
        this.appInfra = appInfra;
    }

    public boolean saveAuthorizationRequest(AuthorizationRequest authorizationRequest) {
        boolean isStored = appInfra.getSecureStorage().storeValueForKey("AuthorizationRequest", authorizationRequest.jsonSerializeString(), new SecureStorageInterface.SecureStorageError());
        return isStored;
    }

    public AuthorizationRequest getAuthorizationRequest() {
        String authorizationRequestString = appInfra.getSecureStorage().fetchValueForKey("AuthorizationRequest", new SecureStorageInterface.SecureStorageError());
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
        boolean isStored = appInfra.getSecureStorage().storeValueForKey("AuthorizationResponse", authorizationResponse, new SecureStorageInterface.SecureStorageError());
        return isStored;
    }

    public String getAuthorizationResponse() {
        String authorizationRequestString = appInfra.getSecureStorage().fetchValueForKey("AuthorizationResponse", new SecureStorageInterface.SecureStorageError());
        return authorizationRequestString;
    }

    public boolean deleteAuthorizationResponse(){
        return appInfra.getSecureStorage().removeValueForKey("AuthorizationResponse");

    }
}
