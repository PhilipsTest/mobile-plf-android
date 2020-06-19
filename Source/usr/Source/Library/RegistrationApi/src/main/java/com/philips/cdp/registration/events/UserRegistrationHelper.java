
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.events;

import com.philips.cdp.registration.listener.HSDPAuthenticationListener;
import com.philips.cdp.registration.listener.UserRegistrationListener;
import com.philips.cdp.registration.ui.utils.RLog;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class User Registration helper
 */
public class UserRegistrationHelper {

    private final String TAG = "UserRegistrationHelper";

    /* User registration helper*/
    private static volatile UserRegistrationHelper eventHelper;

    /* User registration listeners */
    private final CopyOnWriteArrayList<UserRegistrationListener> userRegistrationListeners;
    /* User registration listeners */
    private final CopyOnWriteArrayList<HSDPAuthenticationListener> hsdpAuthenticationListeners;

    private ArrayList<CopyOnWriteArrayList> listner = new ArrayList<CopyOnWriteArrayList>();

    /**
     * Class constructor
     */
    private UserRegistrationHelper() {
        userRegistrationListeners = new CopyOnWriteArrayList<>();
        hsdpAuthenticationListeners = new CopyOnWriteArrayList<>();
        listner.add(userRegistrationListeners);
        listner.add(hsdpAuthenticationListeners);
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
     * {@code registerEventNotification} method to registration event notification
     *
     * @param observer UserRegistrationListener object
     */
    public synchronized void registerHSDPAuthenticationEventNotification(HSDPAuthenticationListener observer) {
        RLog.d(TAG, "registerEventNotification");
        synchronized (hsdpAuthenticationListeners) {
            if (observer != null) {
                for (int i = 0; i < hsdpAuthenticationListeners.size(); i++) {
                    HSDPAuthenticationListener tmp = hsdpAuthenticationListeners.get(i);
                    if (tmp.getClass() == observer.getClass()) {
                        hsdpAuthenticationListeners.remove(tmp);
                    }
                }
                hsdpAuthenticationListeners.add(observer);
            }
        }
    }

    /**
     * {@code unregisterEventNotification} method to unregister event notification
     *
     * @param observer UserRegistrationListener object
     */
    public synchronized void unregisterHSDPAuthenticationEventNotification(HSDPAuthenticationListener observer) {
        RLog.d(TAG, "unregisterEventNotification");
        synchronized (hsdpAuthenticationListeners) {
            if (observer != null) {
                for (int i = 0; i < hsdpAuthenticationListeners.size(); i++) {
                    HSDPAuthenticationListener tmp = hsdpAuthenticationListeners.get(i);
                    if (tmp.getClass() == observer.getClass()) {
                        hsdpAuthenticationListeners.remove(tmp);
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
            for (HSDPAuthenticationListener eventListener : hsdpAuthenticationListeners) {
                if (eventListener != null) {
                    eventListener.onHSDPLoginSuccess();
                }
            }
        }
    }

    /**
     * {@code notifyOnLogoutSuccessWithInvalidAccessToken} method to notify on logout success with invalid access token
     */
    public synchronized void notifyOnHSDPLoginFailure(int errorCode, String errorMsg) {
        RLog.d(TAG, "notifyOnHSDPLoginFailure");
        synchronized (userRegistrationListeners) {
            for (HSDPAuthenticationListener eventListener : hsdpAuthenticationListeners) {
                if (eventListener != null) {
                    eventListener.onHSDPLoginFailure(errorCode, errorMsg);
                }
            }
        }
    }
}
