package com.philips.platform.dscdemo.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.UserLoginState;
import com.philips.cdp.registration.configuration.URConfigurationConstants;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.core.datatypes.UserProfile;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.datasync.userprofile.UserRegistrationInterface;
import com.philips.platform.dscdemo.DemoAppManager;

import javax.inject.Singleton;

@Singleton
public class UserRegistrationHandler implements UserRegistrationInterface {

    private final Context context;
    private final User user;
    private boolean accessTokenRefreshInProgress;
    private String accessToken = "";

    public UserRegistrationHandler(final Context context, final User user) {
        this.context = context;
        this.user = user;
    }

    @Override
    public boolean isUserLoggedIn() {
        return new User(context).getUserLoginState() == UserLoginState.USER_LOGGED_IN;
    }

    @NonNull
    @Override
    public String getHSDPAccessToken() {
        if (accessToken.isEmpty() && !accessTokenRefreshInProgress) {
            accessToken = new User(context).getHsdpAccessToken();
        }
        return accessToken;
    }

    @NonNull
    @Override
    public UserProfile getUserProfile() {
        final UserProfile userProfile;
        User user = new User(context);
        userProfile = new UserProfile(user.getGivenName(), user.getFamilyName(), user.getEmail(), user.getHsdpUUID());
        return userProfile;
    }

    @Override
    public String getHSDPUrl() {
        Object propertyForKey = DemoAppManager.getInstance().getAppInfra().getConfigInterface().getPropertyForKey(URConfigurationConstants.HSDP_CONFIGURATION_BASE_URL,
                URConfigurationConstants.UR, new AppConfigurationInterface.AppConfigurationError());
        return propertyForKey.toString();
    }

    @Override
    public synchronized void refreshAccessTokenUsingWorkAround() {
        if (accessTokenRefreshInProgress) {
            return;
        }
        accessTokenRefreshInProgress = true;
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(refreshLoginSessionRunnable);
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException ignored) {

            }
        }
        accessTokenRefreshInProgress = false;
    }

    private Runnable refreshLoginSessionRunnable = new Runnable() {
        @Override
        public void run() {
            user.refreshLoginSession(new RefreshLoginSessionHandler() {
                @Override
                public void onRefreshLoginSessionSuccess() {
                    accessToken = new User(context).getHsdpAccessToken();
                    notifyLoginSessionResponse();
                }

                @Override
                public void onRefreshLoginSessionFailedWithError(int statusCode) {
                    Toast.makeText(context, "refresh token failed and status code is = " + statusCode, Toast.LENGTH_LONG).show();
                    notifyLoginSessionResponse();
                }

                @Override
                public void onRefreshLoginSessionInProgress(String s) {
                    accessTokenRefreshInProgress = true;
                }
            });
        }
    };

    private void notifyLoginSessionResponse() {
        synchronized (this) {
            notify();
        }
    }

    public void clearUserData(DBRequestListener dbRequestListener) {
        DataServicesManager manager = DataServicesManager.getInstance();
        manager.deleteAll(dbRequestListener);
        clearPreferences();
        clearAccessToken();
    }

    public void clearAccessToken() {
        accessToken = "";
    }

    private void clearPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

}