
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.listener;

/**
 * It is a callback class for proposition to notify USR  status.
 *
 * @deprecated since 1903
 */
//@Deprecated
public interface UserRegistrationListener {
    /**
     * Callback when user is logged out
     *
     * @since 1.0.0
     */
    void onUserLogoutSuccess();

    /**
     * Callback when user is failed to log out
     *
     * @since 1.0.0
     */
    void onUserLogoutFailure();

    /**
     * Callback when logout happened due to invalid access token.
     *
     * @since 1.0.0
     */
    void onUserLogoutSuccessWithInvalidAccessToken();
}
