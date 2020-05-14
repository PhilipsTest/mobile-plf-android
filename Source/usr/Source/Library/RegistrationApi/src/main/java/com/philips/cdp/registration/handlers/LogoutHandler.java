/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.handlers;

/**
 * It is a callback class to proposition for  for handling USR logout
 * @since 1.0.0
 * @deprecated since 1903
 */
//@Deprecated
public interface LogoutHandler {

    /**
     * {@code onLogoutSuccess} method is invoked on Logout Success
     * @since 1.0.0
     */
    void onLogoutSuccess();

    /**
     * {@code onLogoutFailure} method is invoked on Logout Failure
     * @param responseCode  failure response code in integer
     * @param message  failure message
     * @since 1.0.0
     */
    void onLogoutFailure(int responseCode, String message);
}
