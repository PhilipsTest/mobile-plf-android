
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.events;

import com.philips.cdp.registration.listener.UserRegistrationListener;
import com.philips.cdp.registration.ui.utils.RLog;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class User Registration helper
 */
public class UserRegistrationHelper {

    private final String TAG = UserRegistrationHelper.class.getSimpleName();

    /* User registration helper*/
    private static volatile UserRegistrationHelper eventHelper;

    /* User registration listeners */
    private final CopyOnWriteArrayList<UserRegistrationListener> userRegistrationListeners;

    /**
     * Class constructor
     */
    private UserRegistrationHelper() {
        userRegistrationListeners = new CopyOnWriteArrayList<UserRegistrationListener>();
    }

    /**
     * {@code UserRegistrationHelper} method for User registration helper get instance
     *
     * @return eventHelper UserRegistratinHelper object
     */
    public static synchronized UserRegistrationHelper getInstance() {
        if (eventHelper == null) {
            synchronized (UserRegistrationHelper.class) {
                if (eventHelper == null) {
                    eventHelper = new UserRegistrationHelper();
                }
            }

        }
        return eventHelper;
    }

    /**
     * {@code registerEventNotification} method to registration event notification
     *
     * @param observer UserRegistrationListener object
     */
    public synchronized void registerEventNotification(UserRegistrationListener observer) {
        RLog.d(TAG, "registerEventNotification");
        synchronized (userRegistrationListeners) {
            if (observer != null) {
                for (int i = 0; i < userRegistrationListeners.size(); i++) {
                    UserRegistrationListener tmp = userRegistrationListeners.get(i);
                    if (tmp.getClass() == observer.getClass()) {
                        userRegistrationListeners.remove(tmp);
                    }
                }
                userRegistrationListeners.add(observer);
            }
        }
    }

    /**
     * {@code unregisterEventNotification} method to unregister event notification
     *
     * @param observer UserRegistrationListener object
     */
    public synchronized void unregisterEventNotification(UserRegistrationListener observer) {
        RLog.d(TAG, "unregisterEventNotification");
        synchronized (userRegistrationListeners) {
            if (observer != null) {
                for (int i = 0; i < userRegistrationListeners.size(); i++) {
                    UserRegistrationListener tmp = userRegistrationListeners.get(i);
                    if (tmp.getClass() == observer.getClass()) {
                        userRegistrationListeners.remove(tmp);
                    }
                }
            }
        }
    }

    /**
     * {@code notifyOnUserLogoutSuccess} method to notify on user logout success
     */
    public synchronized void notifyOnUserLogoutSuccess() {
        RLog.d(TAG, "notifyOnUserLogoutSuccess");
        synchronized (userRegistrationListeners) {
            for (UserRegistrationListener eventListener : userRegistrationListeners) {
                if (eventListener != null) {
                    eventListener.onUserLogoutSuccess();
                }
            }
        }
    }

    /**
     * {@code notifyOnUserLogoutFailure} method to notify on user logout failure
     */
    public synchronized void notifyOnUserLogoutFailure() {
        RLog.d(TAG, "notifyOnUserLogoutFailure");
        synchronized (userRegistrationListeners) {
            for (UserRegistrationListener eventListener : userRegistrationListeners) {
                if (eventListener != null) {
                    eventListener.onUserLogoutFailure();
                }
            }
        }
    }

    /**
     * {@code notifyOnLogoutSuccessWithInvalidAccessToken} method to notify on logout success with invalid access token
     */
    public synchronized void notifyOnLogoutSuccessWithInvalidAccessToken() {
        RLog.d(TAG, "notifyOnLogoutSuccessWithInvalidAccessToken");
        synchronized (userRegistrationListeners) {
            for (UserRegistrationListener eventListener : userRegistrationListeners) {
                if (eventListener != null) {
                    eventListener.onUserLogoutSuccessWithInvalidAccessToken();
                }
            }
        }
    }

    /**
     * {@code notifyOnLogoutSuccessWithInvalidAccessToken} method to notify on logout success with invalid access token
     */
    public synchronized void notifyOnHSDPLoginSuccess() {
        RLog.d(TAG, "notifyOnHSDPLoginSuccess");
        synchronized (userRegistrationListeners) {
            for (UserRegistrationListener eventListener : userRegistrationListeners) {
                if (eventListener != null) {
                    eventListener.onHSDPLoginSuccess();
                }
            }
        }
    }

    /**
     * {@code notifyOnLogoutSuccessWithInvalidAccessToken} method to notify on logout success with invalid access token
     */
    public synchronized void notifyOnHSDPLoginFailure(int errorCode) {
        RLog.d(TAG, "notifyOnHSDPLoginFailure");
        synchronized (userRegistrationListeners) {
            for (UserRegistrationListener eventListener : userRegistrationListeners) {
                if (eventListener != null) {
                    eventListener.onHSDPLoginFailure(errorCode);
                }
            }
        }
    }
}
