package com.philips.platform.pim.manager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.R;
import com.philips.platform.pim.listeners.PIMAuthServiceConfigListener;
import com.philips.platform.pim.listeners.PIMTokenRequestListener;
import com.philips.platform.pim.utilities.PIMScopes;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import java.util.ArrayList;
import java.util.Map;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

/**
 * Class to communicates with AppAuth of open id
 */
public class PIMAuthManager {
    private final String TAG = PIMAuthManager.class.getSimpleName();
    private LoggingInterface mLoggingInterface;
    private AuthState mAuthState;
    private Context mContext;
    private AuthorizationService mAuthorizationService;

    /**
     * Use this constructor whenever context is not required for OIDC's api call
     */
    public PIMAuthManager() {
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
    }

    /**
     * Use this constructor whenever context is required for OIDC's api call
     *
     * @param context
     */
    public PIMAuthManager(Context context) {
        mContext = context;
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
        mAuthorizationService = new AuthorizationService(mContext);
    }

    /**
     * Fetch AuthorizationServiceConfiguration from OIDC discovery URI
     *
     * @param baseUrl  OIDC discovery URI
     * @param listener A callback to invoke upon completion
     */
    void fetchAuthWellKnownConfiguration(String baseUrl, PIMAuthServiceConfigListener listener) {
        String discoveryEndpoint = baseUrl + "/.well-known/openid-configuration";
        mLoggingInterface.log(DEBUG, TAG, "fetchAuthWellKnownConfiguration discoveryEndpoint : " + discoveryEndpoint);

        final AuthorizationServiceConfiguration.RetrieveConfigurationCallback retrieveCallback =
                (AuthorizationServiceConfiguration authorizationServiceConfiguration, AuthorizationException e) -> {
                    if (e != null) {
                        mLoggingInterface.log(DEBUG, TAG, "fetchAuthWellKnownConfiguration : Failed to retrieve configuration for : " + e.getMessage());
                        listener.onAuthServiceConfigFailed(new Error(e.code, e.getMessage()));
                    } else {
                        mLoggingInterface.log(DEBUG, TAG, "fetchAuthWellKnownConfiguration : Configuration retrieved for  proceeding : " + authorizationServiceConfiguration);
                        listener.onAuthServiceConfigSuccess(authorizationServiceConfiguration);
                    }
                };
        AuthorizationServiceConfiguration.fetchFromUrl(Uri.parse(discoveryEndpoint), retrieveCallback);
    }

    /**
     * Fetch an intent from OIDC for launching CLP page
     *
     * @param authServiceConfiguration configuration downloaded using OIDC discovery URI
     * @param clientID                 to create authorizaton request
     * @param parameter                contains additional parameters
     * @return intent
     * @throws ActivityNotFoundException
     */
    Intent getAuthorizationRequestIntent(AuthorizationServiceConfiguration authServiceConfiguration, String clientID, Map parameter) throws ActivityNotFoundException {
        if (mContext == null || authServiceConfiguration == null || clientID == null)
            return null;

        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        authServiceConfiguration,
                        clientID,
                        ResponseTypeValues.CODE,
                        Uri.parse(mContext.getString(R.string.redirectURL)));

        AuthorizationRequest authRequest = authRequestBuilder
                .setScope(getScopes())
                .setAdditionalParameters(parameter)
                .build();
        return mAuthorizationService.getAuthorizationRequestIntent(authRequest);
    }

    boolean isAuthorizationSuccess(Intent dataIntent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(dataIntent);
        AuthorizationException exception = AuthorizationException.fromIntent(dataIntent);
        if (response != null) {
            return true;
        } else if (exception != null) {
            mLoggingInterface.log(DEBUG, TAG, "Authorization failed with error : " + exception.errorDescription);
            return false;
        } else
            return false;
    }

    /**
     * Perform token request
     *
     * @param dataIntent              to create authorization response and exception
     * @param pimTokenRequestListener A callback to invoke upon completion
     */
    void performTokenRequest(@NonNull Intent dataIntent, @NonNull PIMTokenRequestListener pimTokenRequestListener) {

        AuthorizationResponse response = AuthorizationResponse.fromIntent(dataIntent);
        AuthorizationException exception = AuthorizationException.fromIntent(dataIntent);

        mAuthState = new AuthState(response, exception);

        TokenRequest tokenRequest = response.createTokenExchangeRequest();
        mAuthorizationService.performTokenRequest(tokenRequest, new AuthorizationService.TokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable TokenResponse response, @Nullable AuthorizationException ex) {
                if (response != null) {
                    mAuthState.update(response, ex);
                    mLoggingInterface.log(DEBUG, TAG, "onTokenRequestCompleted => access token : " + response.accessToken);
                    pimTokenRequestListener.onTokenRequestSuccess();
                }

                if (ex != null) {
                    mLoggingInterface.log(DEBUG, TAG, "Token Request failed with error : " + ex.getMessage());
                    pimTokenRequestListener.onTokenRequestFailed(new Error(ex.code, ex.getMessage()));
                }
            }
        });
    }

    /**
     * Perform refresh token
     *
     * @param authState            Pass authstate to refresh its token
     * @param tokenRequestListener A callback to invoke upon completion
     */
    void refreshToken(@NonNull AuthState authState, PIMTokenRequestListener tokenRequestListener) {
        mLoggingInterface.log(DEBUG, TAG, "Old Access Token : " + authState.getAccessToken() + " Refresh Token : " + authState.getRefreshToken());
        authState.setNeedsTokenRefresh(true);
        authState.performActionWithFreshTokens(mAuthorizationService, (accessToken, idToken, ex) -> {
            if (ex == null) {
                mLoggingInterface.log(DEBUG, TAG, "rereshToken success, New  accessToken : " + authState.getAccessToken() + " Refresh Token : " + authState.getRefreshToken());
                tokenRequestListener.onTokenRequestSuccess();
            } else {
                mLoggingInterface.log(DEBUG, TAG, "rereshToken failed : " + ex.getMessage());
                Error error = new Error(ex.code, ex.getMessage());
                tokenRequestListener.onTokenRequestFailed(error);
            }
        });
    }

    private String getScopes() {
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add(PIMScopes.PHONE);
        scopes.add(PIMScopes.EMAIL);
        scopes.add(PIMScopes.PROFILE);
        scopes.add(PIMScopes.ADDRESS);
        scopes.add(PIMScopes.OPENID);
        StringBuilder stringBuilder = new StringBuilder();
        for (String scope : scopes) {
            stringBuilder = stringBuilder.append(scope + " ");
        }
        return stringBuilder.toString();
    }


    AuthState getAuthState() {
        return mAuthState;
    }

    void dispose() {
        if (mAuthorizationService != null) {
            mAuthorizationService.dispose();
            mAuthorizationService = null;
        }
    }
}
