package com.philips.platform.pim.manager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.errors.PIMErrorEnums;
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

    /* *
     * Use this constructor whenever context is required for OIDC's api call
     *
     * @param context
     */
    PIMAuthManager(Context context) {
        mContext = context;
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
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
                (AuthorizationServiceConfiguration authorizationServiceConfiguration, AuthorizationException ex) -> {
                    if (ex != null) {
                        mLoggingInterface.log(DEBUG, TAG, "fetchAuthWellKnownConfiguration : Failed to retrieve configuration for : " + ex.getMessage() + " error code :" + ex.code);
                        listener.onAuthServiceConfigFailed(new Error(PIMErrorEnums.getErrorCode(ex.code), PIMErrorEnums.getLocalisedErrorDesc(mContext, PIMErrorEnums.getErrorCode(ex.code))));
                    } else {
                        mLoggingInterface.log(DEBUG, TAG, "fetchAuthWellKnownConfiguration : Configuration retrieved for  proceeding : " + authorizationServiceConfiguration);
                        listener.onAuthServiceConfigSuccess(authorizationServiceConfiguration);
                    }
                };
        AuthorizationServiceConfiguration.fetchFromUrl(Uri.parse(discoveryEndpoint), retrieveCallback);
    }

    /**
     * Fetch authorization request from OIDC for launching CLP page
     *
     * @param pimOidcConfigration configuration downloaded using OIDC discovery URI
     * @param parameter           contains additional parameters
     * @return authorizationRequest
     * @throws ActivityNotFoundException
     */
    AuthorizationRequest createAuthorizationRequest(@NonNull PIMOIDCConfigration pimOidcConfigration, Map<String, String> parameter) throws ActivityNotFoundException {
        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        pimOidcConfigration.getAuthorizationServiceConfiguration(),
                        pimOidcConfigration.getClientId(),
                        ResponseTypeValues.CODE,
                        Uri.parse(pimOidcConfigration.getRedirectUrl()));

        AuthorizationRequest authorizationRequest = authRequestBuilder
                .setScope(getScopes())
                .setAdditionalParameters(parameter)
                .build();

        return authorizationRequest;
    }

    Intent getAuthorizationRequestIntent(AuthorizationRequest authorizationRequest) {
        AuthorizationService authorizationService = new AuthorizationService(mContext);
        Intent authIntent = authorizationService.getAuthorizationRequestIntent(authorizationRequest);
        authorizationService.dispose();
        return authIntent;
    }

    AuthorizationRequest createAuthRequestUriForMigration(Map<String, String> additionalParameter) {
        PIMOIDCConfigration pimOidcConfigration = PIMSettingManager.getInstance().getPimOidcConfigration();
        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        pimOidcConfigration.getAuthorizationServiceConfiguration(),
                        pimOidcConfigration.getMigrationClientId(),
                        ResponseTypeValues.CODE,
                        Uri.parse(pimOidcConfigration.getMigrationRedirectUrl()));

        AuthorizationRequest authRequest = authRequestBuilder
                .setScope(getScopes())
                .setAdditionalParameters(additionalParameter)
                .setPrompt("none")
                .build();
        return authRequest;
    }

    boolean isAuthorizationSuccess(Intent dataIntent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(dataIntent);
        AuthorizationException exception = AuthorizationException.fromIntent(dataIntent);
        if (response != null) {
            return true;
        } else if (exception != null) {
            mLoggingInterface.log(DEBUG, TAG, "Authorization failed with error : " + exception.errorDescription + " with code :" + exception.code);
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
    void performTokenRequestFromLogin(@NonNull Intent dataIntent, @NonNull PIMTokenRequestListener pimTokenRequestListener) {

        AuthorizationResponse response = AuthorizationResponse.fromIntent(dataIntent);
        AuthorizationException exception = AuthorizationException.fromIntent(dataIntent);

        mAuthState = new AuthState(response, exception);

        performTokenRequest(pimTokenRequestListener, response);
    }

    void performTokenRequestFromLogin(@NonNull AuthorizationRequest authorizationRequest, @NonNull String authResponse, @NonNull PIMTokenRequestListener pimTokenRequestListener) {
        AuthorizationResponse authorizationResponse = new AuthorizationResponse.Builder(authorizationRequest).fromUri(Uri.parse(authResponse)).build();
        if (authorizationResponse == null || authorizationResponse.authorizationCode == null) {
            AuthorizationException authorizationException = AuthorizationException.fromOAuthRedirect(Uri.parse(authResponse));
            mLoggingInterface.log(DEBUG, TAG, "PerformTokenRequest Token Request failed with error : " + authorizationException.getMessage() + "with code : " + authorizationException.code);
            Error error = new Error(PIMErrorEnums.getErrorCode(authorizationException.code), PIMErrorEnums.getLocalisedErrorDesc(mContext, PIMErrorEnums.getErrorCode(authorizationException.code)));
            pimTokenRequestListener.onTokenRequestFailed(error);
            return;
        }
        mAuthState = new AuthState(authorizationResponse, null);

        performTokenRequest(pimTokenRequestListener, authorizationResponse);
    }

    private void performTokenRequest(@NonNull PIMTokenRequestListener pimTokenRequestListener, AuthorizationResponse authorizationResponse) {
        TokenRequest tokenRequest = authorizationResponse.createTokenExchangeRequest();
        AuthorizationService authorizationService = new AuthorizationService(mContext);
        authorizationService.performTokenRequest(tokenRequest, (response1, ex) -> {
            if (response1 != null) {
                mAuthState.update(response1, ex);
                mLoggingInterface.log(DEBUG, TAG, "onTokenRequestCompleted => access token : " + response1.accessToken);
                pimTokenRequestListener.onTokenRequestSuccess();
            }

            if (ex != null) {
                mLoggingInterface.log(DEBUG, TAG, "Token Request failed with error : " + ex.getMessage() + "with code : " + ex.code);
                Error error = new Error(PIMErrorEnums.getErrorCode(ex.code), PIMErrorEnums.getLocalisedErrorDesc(mContext, PIMErrorEnums.getErrorCode(ex.code)));
                pimTokenRequestListener.onTokenRequestFailed(error);
            }
            authorizationService.dispose();
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
        AuthorizationService authorizationService = new AuthorizationService(mContext);
        authState.performActionWithFreshTokens(authorizationService, (accessToken, idToken, ex) -> {
            if (ex == null) {
                mLoggingInterface.log(DEBUG, TAG, "rereshToken success, New  accessToken : " + authState.getAccessToken() + " Refresh Token : " + authState.getRefreshToken());
                tokenRequestListener.onTokenRequestSuccess();
            } else {
                mLoggingInterface.log(DEBUG, TAG, "rereshToken failed : " + ex.getMessage() + "with code : " + ex.code);
//                Error error = new Error(ex.code, ex.getMessage());
                //Error error = new Error(UserDataInterfaceError.TokenRefreshError.errorCode, UserDataInterfaceError.TokenRefreshError.getLocalisedErrorDesc(mContext, ex.code));
                Error error = new Error(PIMErrorEnums.getErrorCode(ex.code), PIMErrorEnums.getLocalisedErrorDesc(mContext, PIMErrorEnums.getErrorCode(ex.code)));
                tokenRequestListener.onTokenRequestFailed(error);
            }
            authorizationService.dispose();
        });
    }

    private String getScopes() {
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add(PIMScopes.PHONE);
        scopes.add(PIMScopes.EMAIL);
        scopes.add(PIMScopes.PROFILE);
        scopes.add(PIMScopes.ADDRESS);
        scopes.add(PIMScopes.OPENID);
        scopes.add(PIMScopes.MARKETING_EMAIL);
        StringBuilder stringBuilder = new StringBuilder();
        for (String scope : scopes) {
            stringBuilder = stringBuilder.append(scope + " ");
        }
        return stringBuilder.toString();
    }

    AuthState getAuthState() {
        return mAuthState;
    }

    public Intent extractResponseData(String responseData, AuthorizationRequest authorizationRequest) {


        Uri responseUri = Uri.parse(responseData);
        if (responseUri.getQueryParameterNames().contains(AuthorizationException.PARAM_ERROR)) {
            return AuthorizationException.fromOAuthRedirect(responseUri).toIntent();
        } else {
            AuthorizationResponse response = new AuthorizationResponse.Builder(authorizationRequest)
                    .fromUri(responseUri)
                    .build();

            if (authorizationRequest.state == null && response.state != null
                    || (authorizationRequest.state != null && !authorizationRequest.state.equals(response.state))) {

                mLoggingInterface.log(DEBUG, TAG, "State returned in authorization response " + response.state + " does not match state from request " + authorizationRequest.state + "  discarding response");

                return AuthorizationException.AuthorizationRequestErrors.STATE_MISMATCH.toIntent();
            }

            return response.toIntent();
        }
    }
}
