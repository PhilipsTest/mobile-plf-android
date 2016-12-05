package cdp.philips.com.mydemoapp.registration;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.URConfigurationConstants;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.core.datatypes.UserCredentials;
import com.philips.platform.core.datatypes.UserProfile;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.datasync.userprofile.UserRegistrationFacade;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import cdp.philips.com.mydemoapp.listener.EventHelper;
import cdp.philips.com.mydemoapp.listener.UserRegistrationFailureListener;
import retrofit.RetrofitError;

import static cdp.philips.com.mydemoapp.DataSyncApplication.gAppInfra;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
@Singleton
public class UserRegistrationFacadeImpl implements UserRegistrationFacade, UserRegistrationFailureListener {

    // TODO: This I do not want
    @NonNull
    private final Context context;

    @NonNull
    private final User user;
    private boolean accessTokenRefreshInProgress;
    private String accessToken = "";
    private Runnable refreshLoginSessionRunnable = new Runnable() {
        @Override
        public void run() {
            user.refreshLoginSession(new RefreshLoginSessionHandler() {
                @Override
                public void onRefreshLoginSessionSuccess() {
                    //accessTokenRefreshTime = DateTime.now();
                    accessToken = gethsdpaccesstoken();
                    notifyLoginSessionResponse();
                }

                @Override
                public void onRefreshLoginSessionFailedWithError(int statusCode) {
                    Toast.makeText(context, "refresh token failed and status code is = " + statusCode, Toast.LENGTH_LONG).show();
                    notifyLoginSessionResponse();
                }

                @Override
                public void onRefreshLoginSessionInProgress(String s) {

                }
            });
        }
    };

    public void clearUserData() {
        DataServicesManager manager = DataServicesManager.getInstance();
        manager.deleteAll();
        clearPreferences();
        email = null;
        accessToken = "";
    }

    @Nullable
    private String email;

    @Inject
    public UserRegistrationFacadeImpl(
            @NonNull final Context context,
            @NonNull final User user) {
        this.context = context;
        this.user = user;
        EventHelper.getInstance().registerURNotification(EventHelper.UR, this);
    }

    private String gethsdpaccesstoken() {
        return getUser(context).getHsdpAccessToken();
    }

    @NonNull
    private User getUser(final Context context) {
        return new User(context);
    }

    @Override
    public boolean isUserLoggedIn() {
        return user.isUserSignIn();
    }

    @NonNull
    @Override
    public String getAccessToken() {
        //refreshAccessTokenUsingWorkAround();
        if (isAccessTokenStillValid())
            accessToken = gethsdpaccesstoken();
        else
            refreshAccessTokenUsingWorkAround();
        return accessToken;
    }

    @NonNull
    @Override
    public UserProfile getUserProfile() {
        return getUserProfileUserRegistrationPart();
    }

    @NonNull
    private UserProfile getUserProfileUserRegistrationPart() {
        final UserProfile userProfile;
        userProfile = new UserProfile(user.getGivenName(), user.getFamilyName(), user.getEmail(), user.getHsdpUUID());
        return userProfile;
    }

    // TODO: We may have to ask the common component to take care of this
    private synchronized void refreshAccessTokenUsingWorkAround() {
        if (accessTokenRefreshInProgress) {
            return;
        }
        Log.d("***SPO***", "refreshAccessTokenUsingWorkAround()");
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(refreshLoginSessionRunnable);
        accessTokenRefreshInProgress = true;
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException ignored) {

            }
        }
        accessTokenRefreshInProgress = false;
    }

    private void notifyLoginSessionResponse() {
        synchronized (this) {
            notify();
        }
    }

    private boolean isAccessTokenStillValid() {
        return (null != accessToken) || !(accessToken != null ? accessToken.isEmpty() : false);
        //  return accessTokenRefreshInProgress!=null && accessToken == null || accessToken.isEmpty();
    }

/*    public void clearUserData() {
      //  accessTokenRefreshTime = null;
        eventing.post(new DataClearRequest());
        clearPreferences();
        email = null;
    }*/


    @Override
    public boolean isSameUser() {
        return email == null || email.length() == 0 || email.equals(getUserProfile().getEmail());
    }

    @Override
    public UserCredentials getUserCredentials() {
        return new UserCredentials(user.getHsdpUUID(), user.getHsdpAccessToken(), user.getJanrainUUID(), user.getAccessToken());
    }

//    @Nullable
//    private Map<String, String> getQueryParams(final String url, final int baseUrlIndex) {
//        Map<String, String> paramsMap = parseQueryParameters(url.substring(baseUrlIndex + 1));
//        if (paramsMap.isEmpty()) {
//            return null;
//        }
//        return paramsMap;
//    }

//    private Map<String, String> parseQueryParameters(String query) {
//        Map<String, String> queryPairsMap = new LinkedHashMap<>();
//        String[] queryParamsPairs = query.split("&");
//        for (String pair : queryParamsPairs) {
//            int idx = pair.indexOf(":");
//            queryPairsMap.put(pair.substring(0, idx), pair.substring(idx + 1));
//        }
//        return queryPairsMap;
//    }

    private void clearPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    @Override
    public String getHSDHsdpUrl() {

        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();
        Object propertyForKey = gAppInfra.getConfigInterface().getPropertyForKey(URConfigurationConstants.HSDP_CONFIGURATION_BASE_URL, URConfigurationConstants.UR, configError);
        return propertyForKey.toString();
    }

    @Override
    public void onFailure(final RetrofitError error) {
        if (error.getKind().equals(RetrofitError.Kind.UNEXPECTED)) {
            Log.i("***SPO***", "In onFailure of UserRegistration - User Not logged in");
            Toast.makeText(context, "User Not Logged-in", Toast.LENGTH_SHORT).show();
            return;
        }
        refreshAccessTokenUsingWorkAround();
    }
}