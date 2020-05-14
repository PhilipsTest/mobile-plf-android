
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
 * It is a callback class to proposition for  for handling forgot password
 * @since 1.0.0
 * @deprecated since 1903
 */
//@Deprecated
public interface ForgotPasswordHandler {

    /**
     * {@code onSendForgotPasswordSuccess} method is invoked on send forgot password success
     * @since 1.0.0
     */
    void onSendForgotPasswordSuccess();

    /**
     *{@code onSendForgotPasswordFailedWithError} method is invoked  on send forgot password failed with error
     * @param userRegistrationFailureInfo  instance of UserRegistrationFailureInfo which contains failure information
     * @since 1.0.0
     */
    void onSendForgotPasswordFailedWithError(
            UserRegistrationFailureInfo userRegistrationFailureInfo);
}
