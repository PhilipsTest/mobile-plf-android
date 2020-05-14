package com.philips.cdp.registration.handlers;

/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */


import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;

import org.json.JSONObject;

/**
 * It is a callback class to proposition for handling social provider login
 *
 * @since 1.0.0
 * @deprecated since 1903
 */
//@Deprecated
public interface SocialLoginProviderHandler extends LoginHandler {

    /**
     * {@code onLoginFailedWithTwoStepError} method is invoked on login failed with tow step error
     *
     * @param prefilledRecord         Json object of login detail record
     * @param socialRegistrationToken social registration token
     * @since 1.0.0
     */
    void onLoginFailedWithTwoStepError(JSONObject prefilledRecord,
                                       String socialRegistrationToken);

    /**
     * {@code onLoginFailedWithMergeFlowError} method is invoked on login failed with merge flow error
     *
     * @param mergeToken                  merge token
     * @param existingProvider            used social login provider
     * @param conflictingIdentityProvider conflicting identity provider
     * @param conflictingIdpNameLocalized conflicting idp name localized
     * @param existingIdpNameLocalized    existing idp name localized
     * @param emailId                     email id
     * @since 1.0.0
     */
    void onLoginFailedWithMergeFlowError(String mergeToken, String existingProvider,
                                         String conflictingIdentityProvider, String conflictingIdpNameLocalized,
                                         String existingIdpNameLocalized, String emailId);

    /**
     * {@code onContinueSocialProviderLoginSuccess} method is invoked on continue social provider login success
     *
     * @since 1.0.0
     */
    void onContinueSocialProviderLoginSuccess();

    /**
     * {@code userRegistrationFailureInfo} method is invoked on continue social provider login failure
     *
     * @param userRegistrationFailureInfo it gives the registration failure information when social provider login fails to continue
     * @since 1.0.0
     */
    void onContinueSocialProviderLoginFailure(UserRegistrationFailureInfo userRegistrationFailureInfo);
}
