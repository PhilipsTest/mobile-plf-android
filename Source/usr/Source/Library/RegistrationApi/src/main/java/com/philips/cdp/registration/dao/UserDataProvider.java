/*
 * Copyright (c) Koninklijke Philips N.V. 2018
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */
package com.philips.cdp.registration.dao;

import android.content.Context;
import androidx.annotation.NonNull;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.UserLoginState;
import com.philips.cdp.registration.controller.UpdateReceiveMarketingEmail;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.JanrainErrorEnum;
import com.philips.cdp.registration.handlers.LogoutHandler;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.cdp.registration.handlers.RefreshUserHandler;
import com.philips.cdp.registration.listener.HSDPAuthenticationListener;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefetchUserDetailsListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;
import com.philips.platform.pif.DataInterface.USR.listeners.UserDataListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UserMigrationListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserDataProvider extends User implements UserDataInterface {
    private final String TAG = "UserDataProvider";
    private static final long serialVersionUID = 1995972602210564L;
    private transient Context context;
    private HashMap<String, Object> userDataMap;
    private final CopyOnWriteArrayList<UserDataListener> userDataListeners;

    public UserDataProvider(Context context) {
        super(context);
        this.context = context;
        userDataListeners = new CopyOnWriteArrayList<>();
    }

    private ArrayList<String> getAllValidKeyNames() {
        return new ArrayList<>(Arrays.asList(
                UserDetailConstants.GIVEN_NAME,
                UserDetailConstants.FAMILY_NAME,
                UserDetailConstants.GENDER,
                UserDetailConstants.EMAIL,
                UserDetailConstants.MOBILE_NUMBER,
                UserDetailConstants.BIRTHDAY,
                UserDetailConstants.RECEIVE_MARKETING_EMAIL,
                UserDetailConstants.UUID,
                UserDetailConstants.ACCESS_TOKEN));
    }

    private void fillUserData() {
        userDataMap = new HashMap<>();
        userDataMap.put(UserDetailConstants.GIVEN_NAME, getGivenName());
        userDataMap.put(UserDetailConstants.FAMILY_NAME, getFamilyName());
        userDataMap.put(UserDetailConstants.GENDER, getGender());
        userDataMap.put(UserDetailConstants.EMAIL, getEmail());
        userDataMap.put(UserDetailConstants.MOBILE_NUMBER, getMobile());
        userDataMap.put(UserDetailConstants.BIRTHDAY, getDateOfBirth());
        userDataMap.put(UserDetailConstants.RECEIVE_MARKETING_EMAIL, getReceiveMarketingEmail());
        userDataMap.put(UserDetailConstants.UUID,getJanrainUUID());
        userDataMap.put(UserDetailConstants.ACCESS_TOKEN,getAccessToken());
    }

    @Override
    public HashMap<String, Object> getUserDetails(ArrayList<String> detailKeys) throws UserDataInterfaceException {
        if(getUserLoggedInState() == UserLoggedInState.USER_NOT_LOGGED_IN){
            throw new UserDataInterfaceException(new Error(Error.UserDetailError.NotLoggedIn));
        }
        RLog.d(TAG, "getUserDetails : " + detailKeys);
        if (detailKeys.isEmpty()) {
            fillUserData();
            return userDataMap;
        }
        return fillRequiredUserDataMap(detailKeys);
    }


    private HashMap<String, Object> fillRequiredUserDataMap(ArrayList<String> detailKeys) throws UserDataInterfaceException {
        RLog.d(TAG, "fillRequiredUserDataMap : " + detailKeys);
        fillUserData();
        HashMap<String, Object> requiredUserDataMap = new HashMap<>();
        for (String key : detailKeys) {
            if (getAllValidKeyNames().contains(key)) {
                requiredUserDataMap.put(key, userDataMap.get(key));
            } else {
                throw new UserDataInterfaceException(new Error(Error.UserDetailError.InvalidFields));
            }
        }
        return requiredUserDataMap;
    }

    @Override
    public String getHSDPAccessToken() {
        RLog.d(TAG, "getHSDPAccessToken : " + getHsdpAccessToken());
        return getHsdpAccessToken();
    }

    @Override
    public String getHSDPUUID() {
        RLog.d(TAG, "getHSDPUUID : " + getHsdpUUID());
        return getHsdpUUID();
    }

    @Override
    public UserLoggedInState getUserLoggedInState() {
        return getState();
    }

    private UserLoggedInState getState() {
        switch (getUserLoginState()) {
            case USER_LOGGED_IN:
                return UserLoggedInState.USER_LOGGED_IN;
            case USER_NOT_LOGGED_IN:
                return UserLoggedInState.USER_NOT_LOGGED_IN;
            case PENDING_VERIFICATION:
                return UserLoggedInState.PENDING_VERIFICATION;
            case PENDING_HSDP_LOGIN:
                return UserLoggedInState.PENDING_HSDP_LOGIN;
            case PENDING_TERM_CONDITION:
                return UserLoggedInState.PENDING_TERM_CONDITION;
        }
        return UserLoggedInState.USER_NOT_LOGGED_IN;
    }


    @Override
    public void authorizeHsdp(com.philips.platform.pif.DataInterface.USR.listeners.HSDPAuthenticationListener hsdpAuthenticationListener) {
        authorizeHSDP(getHsdpAuthenticationHandler(hsdpAuthenticationListener));
    }


    @NonNull
    private HSDPAuthenticationListener getHsdpAuthenticationHandler(com.philips.platform.pif.DataInterface.USR.listeners.HSDPAuthenticationListener hsdpAuthenticationListener) {
        RLog.d(TAG, "getHsdpAuthenticationHandler");
        return new HSDPAuthenticationListener() {
            @Override
            public void onHSDPLoginSuccess() {
                RLog.d(TAG, "onHSDPLoginSuccess");
                hsdpAuthenticationListener.onHSDPLoginSuccess();
            }

            @Override
            public void onHSDPLoginFailure(int errorCode, String msg) {
                RLog.d(TAG, "onHSDPLoginFailure");
                hsdpAuthenticationListener.onHSDPLoginFailure(errorCode, msg);
            }
        };
    }

    @Override
    public void refreshSession(RefreshSessionListener refreshSessionListener) {
        RLog.d(TAG, "refreshLoginSession");
        refreshLoginSession(getRefreshHandler(refreshSessionListener));
    }

    @Override
    public void refreshHSDPSession(RefreshSessionListener refreshSessionListener) {
        RLog.d(TAG, "refreshLoginSession");
        refreshHSDPLoginSession(getRefreshHandler(refreshSessionListener));
    }


    @Override
    public boolean isOIDCToken() {
        return false;
    }

    @Override
    public void migrateUserToPIM(UserMigrationListener userMigrationListener) {

    }

    @NonNull
    protected RefreshLoginSessionHandler getRefreshHandler(final RefreshSessionListener refreshListener) {
        RLog.d(TAG, "getRefreshHandler");
        return new RefreshLoginSessionHandler() {
            @Override
            public void onRefreshLoginSessionSuccess() {
                RLog.d(TAG, "onRefreshLoginSessionSuccess");
                refreshListener.refreshSessionSuccess();
                notifyRefreshSessionSuccess();
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(int error) {
                RLog.e(TAG, "getRefreshHandler: onRefreshLoginSessionFailedWithError: " + error);
                refreshListener.refreshSessionFailed(new Error(error,null));
                notifyRefetchSessionFailure(error);
            }

            @Override
            public void forcedLogout() {
                RLog.e(TAG, "getRefreshHandler: onRefreshLoginSessionFailedAndLoggedout : ");
                refreshListener.forcedLogout();
            }
        };
    }

    @Override
    public void refetchUserDetails(RefetchUserDetailsListener userDetailsListener) {
        RLog.d(TAG, "refetchUserDetails");
        refreshUser(getRefetchHandler(userDetailsListener));
    }

    @Override
    public void updateReceiveMarketingEmail(UpdateUserDetailsHandler updateUserDetailsHandler, boolean receiveMarketingEmail) {
        if (getUserLoginState().ordinal() < UserLoginState.PENDING_VERIFICATION.ordinal()) {
            updateUserDetailsHandler.onUpdateFailedWithError(new Error(ErrorCodes.UPDATE_USER_DETAILS_ERROR, JanrainErrorEnum.getLocalizedError(context, ErrorCodes.UPDATE_USER_DETAILS_ERROR)));
            return;
        }
        UpdateReceiveMarketingEmail updateReceiveMarketingEmailHandler = new
                UpdateReceiveMarketingEmail(
                context);
        RLog.d(TAG, "updateReceiveMarketingEmail called : " + receiveMarketingEmail);
        updateReceiveMarketingEmailHandler.
                updateMarketingEmailStatus(updateUserDetailsHandler, receiveMarketingEmail);
    }

    @NonNull
    protected RefreshUserHandler getRefetchHandler(final RefetchUserDetailsListener userDetailsListener) {
        RLog.d(TAG, "getRefetchHandler");
        return new RefreshUserHandler() {
            @Override
            public void onRefreshUserSuccess() {
                RLog.d(TAG, "onRefreshUserSuccess");
                userDetailsListener.onRefetchSuccess();
                notifyRefreshUserDetailsSuccess();
            }

            @Override
            public void onRefreshUserFailed(int error) {
                RLog.d(TAG, "onRefreshUserFailed : " + error);
                userDetailsListener.onRefetchFailure(new Error(error,null));
                notifyRefetchUserDetailsFailure(error);
            }
        };
    }

    @Override
    public void logoutSession(LogoutSessionListener logoutSessionListener) {
        RLog.d(TAG, "logOut");
        logout(getLogoutHandler(logoutSessionListener));
    }

    @NonNull
    LogoutHandler getLogoutHandler(final LogoutSessionListener logoutListener) {
        RLog.d(TAG, "getLogoutHandler");
        return new LogoutHandler() {
            @Override
            public void onLogoutSuccess() {
                RLog.d(TAG, "onLogoutSuccess");
                logoutListener.logoutSessionSuccess();
                notifyLogOutSuccess();
            }

            @Override
            public void onLogoutFailure(int responseCode, String message) {
                RLog.d(TAG, "onLogoutFailure : responseCode = " + responseCode + " message = " + message);
                logoutListener.logoutSessionFailed(new Error(responseCode,message));
                notifyLogoutFailure(responseCode,message);
            }
        };
    }

    private void notifyLogOutSuccess(){
        synchronized (userDataListeners) {
            for (LogoutSessionListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.logoutSessionSuccess();
                }
            }
        }
    }

    private void notifyLogoutFailure(int errorCode, String errorMessage){
        synchronized (userDataListeners) {
            for (LogoutSessionListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.logoutSessionFailed(new Error(errorCode,errorMessage));
                }
            }
        }
    }

    private void notifyRefreshSessionSuccess(){
        synchronized (userDataListeners) {
            for (RefreshSessionListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.refreshSessionSuccess();
                }
            }
        }
    }

    private void notifyRefetchSessionFailure(int errorCode){
        synchronized (userDataListeners) {
            for (RefreshSessionListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.refreshSessionFailed(new Error(errorCode,null));
                }
            }
        }
    }

    private void notifyRefreshUserDetailsSuccess(){
        synchronized (userDataListeners) {
            for (RefetchUserDetailsListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.onRefetchSuccess();
                }
            }
        }
    }

    private void notifyRefetchUserDetailsFailure(int errorCode){
        synchronized (userDataListeners) {
            for (RefetchUserDetailsListener eventListener : userDataListeners) {
                if (eventListener != null) {
                    eventListener.onRefetchFailure(new Error(errorCode,null));
                }
            }
        }
    }


    @Override
    public void addUserDataInterfaceListener(UserDataListener userDataListener) {
        userDataListeners.add(userDataListener);
    }

    @Override
    public void removeUserDataInterfaceListener(UserDataListener userDataListener) {
        userDataListeners.remove(userDataListener);
    }
}
