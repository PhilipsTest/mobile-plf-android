package com.philips.platform.pim;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import android.content.Context;
import androidx.annotation.Nullable;

import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.HSDPAuthenticationListener;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefetchUserDetailsListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;
import com.philips.platform.pif.DataInterface.USR.listeners.UserDataListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UserMigrationListener;
import com.philips.platform.pim.errors.PIMErrorEnums;
import com.philips.platform.pim.manager.PIMConfigManager;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.manager.PIMUserManager;
import com.philips.platform.pim.migration.PIMMigrator;
import com.philips.platform.pim.models.PIMOIDCUserProfile;
import com.philips.platform.pim.utilities.PIMInitState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implements UserDataInterface and handle api calls from propositions.
 * Call responsible pim manager class to perform api request and invoke
 * callback methods on completion of request.
 */
public class PIMDataImplementation implements UserDataInterface {
    private PIMUserManager pimUserManager;
    private Context mContext;
    private boolean isInitRequiredAgain;
    private final CopyOnWriteArrayList<UserDataListener> userDataListeners;

    PIMDataImplementation(Context context, PIMUserManager pimUserManager) {
        mContext = context;
        this.pimUserManager = pimUserManager;
        userDataListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void logoutSession(LogoutSessionListener logoutSessionListener) {
        if (pimUserManager != null)
            pimUserManager.logoutSession(getLogoutSessionListener(logoutSessionListener));
    }

    private LogoutSessionListener getLogoutSessionListener(LogoutSessionListener logoutSessionListener) {
        return new LogoutSessionListener() {
            @Override
            public void logoutSessionSuccess() {
                logoutSessionListener.logoutSessionSuccess();
                notifyLogOutSuccess();
            }

            @Override
            public void logoutSessionFailed(Error error) {
                logoutSessionListener.logoutSessionFailed(error);
                notifyLogoutFailure(error);
            }
        };
    }

    private void notifyLogOutSuccess() {
        synchronized (userDataListeners) {
            for (LogoutSessionListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.logoutSessionSuccess();
                }
            }
        }
    }

    private void notifyLogoutFailure(Error error) {
        synchronized (userDataListeners) {
            for (LogoutSessionListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.logoutSessionFailed(error);
                }
            }
        }
    }

    @Override
    public void refreshSession(RefreshSessionListener refreshSessionListener) {
        pimUserManager.refreshSession(getRefreshSessionListener(refreshSessionListener));
    }

    @Override
    public void refreshHSDPSession(RefreshSessionListener refreshSessionListener) {

    }

    @Override
    public boolean isOIDCToken() {
        if(pimUserManager.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN)
            return true;
        return false;
    }

    private RefreshSessionListener getRefreshSessionListener(RefreshSessionListener refreshSessionListener) {
        return new RefreshSessionListener() {
            @Override
            public void refreshSessionSuccess() {
                refreshSessionListener.refreshSessionSuccess();
                notifyRefreshSessionSuccess();
            }

            @Override
            public void refreshSessionFailed(Error error) {
                refreshSessionListener.refreshSessionFailed(error);
                notifyRefreshSessionFailure(error);
            }

            @Override
            public void forcedLogout() {
                refreshSessionListener.forcedLogout();
                notifyForcedLogout();
            }
        };
    }

    private void notifyRefreshSessionSuccess() {
        synchronized (userDataListeners) {
            for (RefreshSessionListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.refreshSessionSuccess();
                }
            }
        }
    }

    private void notifyRefreshSessionFailure(Error error) {
        synchronized (userDataListeners) {
            for (RefreshSessionListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.refreshSessionFailed(error);
                }
            }
        }
    }

    private void notifyForcedLogout() {
        synchronized (userDataListeners) {
            for (RefreshSessionListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.forcedLogout();
                }
            }
        }
    }

    @Override
    public void migrateUserToPIM(UserMigrationListener userMigrationListener) {
        if (pimUserManager.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
            return;
        }
        isInitRequiredAgain = true;
        MutableLiveData<PIMInitState> pimInitLiveData = PIMSettingManager.getInstance().getPimInitLiveData();
        new PIMConfigManager(PIMSettingManager.getInstance().getPimUserManager()).init(mContext, PIMSettingManager.getInstance().getAppInfraInterface().getServiceDiscovery());
        pimInitLiveData.observeForever(new Observer<PIMInitState>() {
            @Override
            public void onChanged(@Nullable PIMInitState pimInitState) {
                if (pimInitState == PIMInitState.INIT_SUCCESS) {
                    pimInitLiveData.removeObserver( this);
                    PIMMigrator pimMigrator = new PIMMigrator(mContext, userMigrationListener);
                    pimMigrator.migrateUSRToPIM();
                } else if (pimInitState == PIMInitState.INIT_FAILED) {
                    if (isInitRequiredAgain) {
                        new PIMConfigManager(PIMSettingManager.getInstance().getPimUserManager()).init(mContext, PIMSettingManager.getInstance().getAppInfraInterface().getServiceDiscovery());
                        isInitRequiredAgain = false;
                    } else {
                        pimInitLiveData.removeObserver(this);
                        userMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.MIGRATION_FAILED.getLocalisedErrorDesc(mContext, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
                    }
                }
            }
        });
    }

    @Override
    public void refetchUserDetails(RefetchUserDetailsListener userDetailsListener) {
        if (pimUserManager.getUserLoggedInState() != UserLoggedInState.USER_LOGGED_IN) {
            userDetailsListener.onRefetchFailure(new Error(Error.UserDetailError.NotLoggedIn));
            return;
        }

        pimUserManager.refetchUserProfile(userDetailsListener);
    }

    @Override
    public void updateReceiveMarketingEmail(UpdateUserDetailsHandler updateUserDetailsHandler, boolean receiveMarketingEmail) {
        if (pimUserManager.getUserLoggedInState() != UserLoggedInState.USER_LOGGED_IN) {
            updateUserDetailsHandler.onUpdateFailedWithError(new Error(Error.UserDetailError.NotLoggedIn));
            return;
        }

        pimUserManager.updateMarketingOptIn(updateUserDetailsHandler, receiveMarketingEmail);

    }

    @Override
    public HashMap<String, Object> getUserDetails(ArrayList<String> detailKeys) throws UserDataInterfaceException {
        if (getUserLoggedInState() == UserLoggedInState.USER_NOT_LOGGED_IN) {
            throw new UserDataInterfaceException(new Error(Error.UserDetailError.NotLoggedIn));
        }

        PIMOIDCUserProfile pimoidcUserProfile = pimUserManager.getUserProfile();

        if (detailKeys.size() == 0) {
            ArrayList<String> allValidKeys = getAllValidUserDetailsKeys();
            return pimoidcUserProfile.fetchUserDetails(allValidKeys);
        } else {
            ArrayList<String> validDetailKey = fillOnlyRequestedValidKeyToKeyList(detailKeys);
            return pimoidcUserProfile.fetchUserDetails(validDetailKey);
        }
    }

    private ArrayList<String> fillOnlyRequestedValidKeyToKeyList(ArrayList<String> detailskey) throws UserDataInterfaceException {
        ArrayList<String> allValidKeys = getAllValidUserDetailsKeys();
        ArrayList<String> validDetailsKey = new ArrayList<>();
        for (String key : detailskey) {
            if (allValidKeys.contains(key))
                validDetailsKey.add(key);
            else
                throw new UserDataInterfaceException(new Error(Error.UserDetailError.InvalidFields));
        }
        return validDetailsKey;
    }

    private ArrayList<String> getAllValidUserDetailsKeys() {
        ArrayList<String> keyList = new ArrayList<>();
        keyList.add(UserDetailConstants.GIVEN_NAME);
        keyList.add(UserDetailConstants.FAMILY_NAME);
        keyList.add(UserDetailConstants.GENDER);
        keyList.add(UserDetailConstants.EMAIL);
        keyList.add(UserDetailConstants.MOBILE_NUMBER);
        keyList.add(UserDetailConstants.BIRTHDAY);
        keyList.add(UserDetailConstants.RECEIVE_MARKETING_EMAIL);
        keyList.add(UserDetailConstants.UUID);
        keyList.add(UserDetailConstants.ACCESS_TOKEN);
        keyList.add(UserDetailConstants.ID_TOKEN);
        keyList.add(UserDetailConstants.EXPIRES_IN);
        keyList.add(UserDetailConstants.TOKEN_TYPE);
        return keyList;
    }

    /**
     * Get the HSDP access token
     *
     * @return returns the HSDP access token
     * @since 2018.1.0
     */
    @Override
    public String getHSDPAccessToken() {
        return null;
    }

    /**
     * Get the HSDP UUID
     *
     * @return returns the HSDP UUID
     * @since 2018.1.0
     */
    @Override
    public String getHSDPUUID() {
        return null;
    }

    @Override
    public UserLoggedInState getUserLoggedInState() {
        if (pimUserManager != null)
            return pimUserManager.getUserLoggedInState();
        return UserLoggedInState.USER_NOT_LOGGED_IN;
    }

    /**
     * {@code authorizeHSDP} method authorize a user is log-in in HSDP Backend
     *
     * @param hsdpAuthenticationListener
     * @since 1804.0
     */
    @Override
    public void authorizeHsdp(HSDPAuthenticationListener hsdpAuthenticationListener) {

    }

    @Override
    public void addUserDataInterfaceListener(UserDataListener listener) {
        userDataListeners.add(listener);
    }

    @Override
    public void removeUserDataInterfaceListener(UserDataListener listener) {
        userDataListeners.remove(listener);
    }
}
