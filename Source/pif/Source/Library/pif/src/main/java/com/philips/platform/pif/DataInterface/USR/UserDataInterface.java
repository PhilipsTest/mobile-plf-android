/*
 * Copyright (c) Koninklijke Philips N.V. 2019
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */
package com.philips.platform.pif.DataInterface.USR;

import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.HSDPAuthenticationListener;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefetchUserDetailsListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;
import com.philips.platform.pif.DataInterface.USR.listeners.UserDataListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UserMigrationListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data interface for getting the user data
 *
 * @since 2018.1.0
 */
public interface UserDataInterface extends Serializable {


    /**
     * Get the user details hashmap on sending ArrayList of respective valid keys. All details HashMap returned on sending empty ArrayList. Throws exception on sending invalid key.
     *
     * @param detailKeys the list of the keys for which user data is needed
     * @return hashmap of user's details
     * @since 1903
     */
    HashMap<String, Object> getUserDetails(ArrayList<String> detailKeys) throws UserDataInterfaceException;


    /**
     * Get the HSDP access token
     *
     * @return returns the HSDP access token
     * @since 2018.1.0
     * @deprecated since 1903
     */
    @Deprecated
    String getHSDPAccessToken();

    /**
     * Get the HSDP UUID
     *
     * @return returns the HSDP UUID
     * @since 2018.1.0
     * @deprecated since 1903
     */
    @Deprecated
    String getHSDPUUID();

    /**
     * {@code getUserSignInState} method checks a user is logged in state
     *
     * @return UserLoginState
     * @since 1804.0
     */
    UserLoggedInState getUserLoggedInState();

    /**
     * {@code authorizeHSDP} method authorize a user is log-in in HSDP Backend
     *
     * @param hsdpAuthenticationListener
     * @since 1804.0
     * @deprecated since 1903
     */
    @Deprecated
    void authorizeHsdp(HSDPAuthenticationListener hsdpAuthenticationListener);

    /**
     * To refesh the user
     *
     * @param refreshSessionListener listener for user refresh
     * @since 1903
     */
    void refreshSession(RefreshSessionListener refreshSessionListener);

    /**
     * To refesh the user
     *
     * @param refreshSessionListener listener for user refresh
     * @since 1903
     */
    void refreshHSDPSession(RefreshSessionListener refreshSessionListener);


    /**
     * To check whether current access token is OIDC token or not
     *
     * @return true if the current access token is from OIDC else return false
     */
    boolean isOIDCToken();

    /**
     * To migrate user from USR to PIM
     *
     * @param userMigrationListener listener for migration
     */
    void migrateUserToPIM(UserMigrationListener userMigrationListener);

    /**
     * log out the user
     *
     * @param logoutSessionListener listener for logout
     * @since 1903
     */
    void logoutSession(LogoutSessionListener logoutSessionListener);

    /**
     * Refetch the details of the user from server
     *
     * @param userDetailsListener listener for refetching the details
     * @since 1903
     */
    void refetchUserDetails(RefetchUserDetailsListener userDetailsListener);

    /**
     * Update the receive marketing email.
     *
     * @param updateUserDetailsHandler instance of UpdateUserDetailsHandler callback
     * @param receiveMarketingEmail    does User want to receive marketing email or not.
     *                                 Pass true if User wants to receive or else false .
     * @since 1903
     */
    public void updateReceiveMarketingEmail(
            final UpdateUserDetailsHandler updateUserDetailsHandler,
            final boolean receiveMarketingEmail);

    /**
     * Register UserDataListener for logout,refreshSession and refechUserDeatils
     *
     * @param userDataListener listener
     * @since 1903
     */
    void addUserDataInterfaceListener(UserDataListener userDataListener);

    /**
     * Remove UserDataListener
     *
     * @param userDataListener
     * @since 1903
     */
    void removeUserDataInterfaceListener(UserDataListener userDataListener);


}
