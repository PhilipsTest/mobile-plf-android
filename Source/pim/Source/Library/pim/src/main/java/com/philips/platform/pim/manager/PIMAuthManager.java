package com.philips.platform.pim.manager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pim.R;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.listeners.PIMAuthorizationServiceConfigurationListener;
import com.philips.platform.pim.utilities.PIMConstants;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

public class PIMAuthManager {
    private final String TAG = PIMAuthManager.class.getSimpleName();
    private LoggingInterface mLoggingInterface;
    private ExecutorService executorService;

    public PIMAuthManager() {
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
        executorService = Executors.newSingleThreadExecutor();
    }

    protected void fetchAuthWellKnownConfiguration(String baseUrl, PIMAuthorizationServiceConfigurationListener listener) {
        String discoveryEndpoint = baseUrl + "/.well-known/openid-configuration";
        mLoggingInterface.log(DEBUG, TAG, "fetchAuthWellKnownConfiguration discoveryEndpoint : " + discoveryEndpoint);

        final AuthorizationServiceConfiguration.RetrieveConfigurationCallback retrieveCallback =
                (AuthorizationServiceConfiguration authorizationServiceConfiguration, AuthorizationException e) -> {
                    if (e != null) {
                        mLoggingInterface.log(DEBUG, TAG, "fetchAuthWellKnownConfiguration : Failed to retrieve configuration for : " + e.getMessage());
                        listener.onError(e.getMessage());
                    } else {
                        mLoggingInterface.log(DEBUG, TAG, "fetchAuthWellKnownConfiguration : Configuration retrieved for  proceeding : " + authorizationServiceConfiguration);
                        listener.onSuccess(authorizationServiceConfiguration);
                    }
                };
        AuthorizationServiceConfiguration.fetchFromUrl(Uri.parse(discoveryEndpoint), retrieveCallback);
    }

    protected void performTokenRequest(Context context, AuthorizationResponse authResponse, AuthorizationService.TokenResponseCallback tokenResponseCallback) {
        mLoggingInterface.log(DEBUG, TAG, "performTokenRequest for code exchange to get Access token");
        AuthorizationService authService = new AuthorizationService(context);
        TokenRequest tokenRequest = authResponse.createTokenExchangeRequest();
        authService.performTokenRequest(tokenRequest, tokenResponseCallback);
    }

    public AuthorizationRequest makeAuthRequest(Context pimFragmentContext, PIMOIDCConfigration pimoidcConfigration, Bundle mBundle) {
        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        pimoidcConfigration.getAuthorizationServiceConfiguration(), // the authorization service configuration
                        pimoidcConfigration.getClientId(), // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        Uri.parse(pimFragmentContext.getString(R.string.redirectURL))); // the redirect URI to which the auth response is sent
        Map<String, String> parameter = new HashMap<>();
        Serializable serializable = mBundle.getSerializable(PIMConstants.PIM_KEY_CUSTOM_CLAIMS);
        String customClaims = "id_token =" + serializable.toString();
        parameter.put("claims", customClaims);
        AuthorizationRequest authRequest = authRequestBuilder
                .setScope(getScopes(mBundle))
//                .setAdditionalParameters(parameter)
                .build();
        return authRequest;
    }

    private String getScopes(Bundle mBundle) {
        ArrayList<String> scopes = mBundle.getStringArrayList(PIMConstants.PIM_KEY_SCOPES);
        StringBuilder stringBuilder = new StringBuilder();
        for (String scope : scopes) {
            stringBuilder = stringBuilder.append(scope + " ");
        }
        return stringBuilder.toString();
    }
}
