package com.philips.platform.pim.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.listeners.PIMTokenRequestListener;
import com.philips.platform.pim.listeners.PIMUserProfileDownloadListener;
import com.philips.platform.pim.models.PIMOIDCUserProfile;
import com.philips.platform.pim.rest.LogoutRequest;
import com.philips.platform.pim.rest.PIMRestClient;
import com.philips.platform.pim.rest.UserProfileRequest;

import net.openid.appauth.AuthState;

import org.json.JSONException;
import org.json.JSONObject;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

/**
 * Class to manage users
 */
public class PIMUserManager {
    private static final String PIM_ACTIVEUUID = "com.pim.activeuuid";
    private PIMOIDCUserProfile pimoidcUserProfile;
    private Context context;
    private AppInfraInterface appInfraInterface;
    private LoggingInterface mLoggingInterface;
    private final String TAG = PIMUserManager.class.getSimpleName();
    private AuthState authState;
    private PIMRestClient pimRestClient;
    private PIMAuthManager pimAuthManager;
    private String uuid;

    public void init(@NonNull Context context, @NonNull AppInfraInterface appInfraInterface) {
        this.context = context;
        this.appInfraInterface = appInfraInterface;
        pimAuthManager = new PIMAuthManager(context); //Context is required to inject from PIMUserManager. Don't call default constructor.
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
        uuid = getUUIDFromPref();
        if (isUUIDAvailable()) {
            //On app relaunch if UUID is saved then fetch profile and auth state from secure storage
            authState = getAuthStateFromSecureStorage();
            String userProfileJson = getUserProfileFromSecureStorage();
            pimoidcUserProfile = new PIMOIDCUserProfile(userProfileJson, authState);
        }
        pimRestClient = new PIMRestClient(PIMSettingManager.getInstance().getRestClient());
        mLoggingInterface.log(DEBUG, TAG, "User  manager initialized");
    }


    public void requestUserProfile(AuthState oidcAuthState, PIMUserProfileDownloadListener userProfileRequestListener) {
        UserProfileRequest userProfileRequest = new UserProfileRequest(oidcAuthState);
        pimRestClient.invokeRequest(userProfileRequest, response -> {

            authState = oidcAuthState;
            pimoidcUserProfile = new PIMOIDCUserProfile(response, authState);
            uuid = getUUIDFromUserProfileJson(response);

            saveUUIDToPreference(uuid); //Store uuid from json reponse to shared preference
            storeUserProfileToSecureStorage(response); //store jsonm reponse to secure storgae
            storeAuthStateToSecureStorage(oidcAuthState); //store auth state to secure storage

            if (userProfileRequestListener != null)
                userProfileRequestListener.onUserProfileDownloadSuccess();
        }, error -> {
            mLoggingInterface.log(DEBUG, TAG, "error : " + error.getMessage());
            if (userProfileRequestListener != null)
                userProfileRequestListener.onUserProfileDownloadFailed(new Error(Error.UserDetailError.NetworkError));
        });
    }


    public UserLoggedInState getUserLoggedInState() {
        if (authState != null && pimoidcUserProfile != null)
            return UserLoggedInState.USER_LOGGED_IN;
        else
            return UserLoggedInState.USER_NOT_LOGGED_IN;
    }

    public PIMOIDCUserProfile getUserProfile() {
        return pimoidcUserProfile;
    }

    public void refreshSession(RefreshSessionListener refreshSessionListener) {
        pimAuthManager.refreshToken(authState, new PIMTokenRequestListener() {
            @Override
            public void onTokenRequestSuccess() {
                storeAuthStateToSecureStorage(authState);  //Update auth state to secure storage on token refresh
                refreshSessionListener.refreshSessionSuccess();
            }

            @Override
            public void onTokenRequestFailed(Error error) {
                refreshSessionListener.refreshSessionFailed(error);
            }
        });
    }

    public void logoutSession(LogoutSessionListener logoutSessionListener) {
        String clientID = new PIMOIDCConfigration().getClientId();
        LogoutRequest logoutRequest = new LogoutRequest(authState, clientID);
        pimRestClient.invokeRequest(logoutRequest, response -> {
            pimAuthManager.dispose();
            appInfraInterface.getSecureStorage().removeValueForKey(getAuthStateKey());
            appInfraInterface.getSecureStorage().removeValueForKey(getUserInfoKey());
            removeUUIDFromPref();
            authState = null;
            pimoidcUserProfile = null;
            logoutSessionListener.logoutSessionSuccess();
        }, error -> {
            logoutSessionListener.logoutSessionFailed(new Error(Error.UserDetailError.NetworkError));
        });
    }

    private void storeUserProfileToSecureStorage(String jsonUserProfileResponse) {
        if (isUUIDAvailable()) {
            boolean isStored = appInfraInterface.getSecureStorage().storeValueForKey(getUserInfoKey(), jsonUserProfileResponse, new SecureStorageInterface.SecureStorageError());
            mLoggingInterface.log(DEBUG, TAG, "UserProfile Stored to Secure storage : " + isStored);
        }
    }

    private String getUserProfileFromSecureStorage() {
        if(isUUIDAvailable()) {
            return appInfraInterface.getSecureStorage().fetchValueForKey(getUserInfoKey(), new SecureStorageInterface.SecureStorageError());
        }
        return null;
    }

    private void storeAuthStateToSecureStorage(AuthState authState) {
        if (isUUIDAvailable()) {
            boolean isStored = appInfraInterface.getSecureStorage().storeValueForKey(getAuthStateKey(), authState.jsonSerializeString(), new SecureStorageInterface.SecureStorageError());
            mLoggingInterface.log(DEBUG, TAG, "AuthState Stored to Secure storage : " + isStored);
        }
    }

    private AuthState getAuthStateFromSecureStorage() {
        if(isUUIDAvailable()) {
            String authStateString = appInfraInterface.getSecureStorage().fetchValueForKey(getAuthStateKey(), new SecureStorageInterface.SecureStorageError());
            if (authStateString != null) {
                try {
                    return AuthState.jsonDeserialize(authStateString);
                } catch (JSONException e) {
                    mLoggingInterface.log(DEBUG, TAG, "exception in getAuthStateFromSecureStorage : " + e.getMessage());
                }
            }
        }
        return null;
    }

    private String getUUIDFromUserProfileJson(String userProfileJson) {
        if (userProfileJson == null)
            return null;
        try {
            JSONObject jsonObject = new JSONObject(userProfileJson);
            return jsonObject.getString("sub"); //sub in response json represents UUID
        } catch (JSONException e) {
            mLoggingInterface.log(DEBUG, TAG, "exception in getUUIDFromUserProfileJson : " + e.getMessage());
        }
        return null;
    }

    private String getUUIDFromPref() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PIM_PREF", Context.MODE_PRIVATE);
        return sharedPreferences.getString(PIM_ACTIVEUUID, null);
    }

    private void saveUUIDToPreference(String uuid) {
        if (uuid != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("PIM_PREF", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PIM_ACTIVEUUID, uuid);
            editor.apply();
        } else {
            mLoggingInterface.log(DEBUG, TAG, "UUID or context is null");
        }
    }

    private void removeUUIDFromPref() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PIM_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PIM_ACTIVEUUID);
        editor.apply();
    }

    /**
     * @return key for storing/fetching user info into/from secure storage
     */
    private String getUserInfoKey() {
        return  "UUID_" + uuid + "_UserInfo";
    }

    /**
     * @return key for storing/fetching auth state into/from secure storage
     */
    private String getAuthStateKey() {
        return  "UUID_" + uuid + "_AuthState";
    }

    /**
     * @return false if uuid is null else true
     */
    private boolean isUUIDAvailable() {
        if (uuid == null) {
            mLoggingInterface.log(DEBUG, TAG, "UUID not found");
            return false;
        }
        return true;
    }
}
