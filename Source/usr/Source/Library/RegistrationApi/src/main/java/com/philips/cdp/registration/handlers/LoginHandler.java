
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.handlers;

import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;

/**
 * It is a callback class to proposition for handling traditional login
 * @since 1.0.0
 */
// Deprecated  since 1903
public interface LoginHandler {

    /**
     * {@code onLoginSuccess}method is invoked on traditional(philips) login success
     * @since 1.0.0
     */
    void onLoginSuccess();

    /**
     * {@code onLoginFailedWithError }method is invoked on traditional(philips) login fails with error
     * @param userRegistrationFailureInfo gives user registration failure information when philips login fails
     * @since 1.0.0
     */
    void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo);

}
