
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
 * It is a callback class to proposition for handling resend verification
 * @since 1.0.0
 */
// Deprecated  since 1903
public interface ResendVerificationEmailHandler {
    /**
     *{@code onResendVerificationEmailSuccess} method is invoked on resend verification email success
     * @since 1.0.0
     */
    void onResendVerificationEmailSuccess();

    /**
     * {@code onResendVerificationEmailFailedWithError} method is invoked on resend verification email fails with error
     * @param userRegistrationFailureInfo   gives the failure information when resend verification email fails
     * @since 1.0.0
     */
    void onResendVerificationEmailFailedWithError(
            UserRegistrationFailureInfo userRegistrationFailureInfo);
}
