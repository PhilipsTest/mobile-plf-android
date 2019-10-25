/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */
package com.philips.cdp.registration.listener;

import android.app.Activity;

/**
 * It is a callback class for proposition to notify UI event in USR
 * @since 1.0.0
 *
 * @deprecated since 1903
 */
@Deprecated
public interface UserRegistrationUIEventListener {
    /**
     * When user complete the registration process completed
     *
     * @param activity launching activity as callback
     *                 @since 1.0.0
     */
    void onUserRegistrationComplete(Activity activity);

    /**
     * Privacy policy link clicked callback
     *
     * @param activity launching activity as callback
     *                 @since 1.0.0
     */
    void onPrivacyPolicyClick(Activity activity);

    /**
     * On terms and condition clicked callback.
     *
     * @param activity launching activity as callback
     *                 @since 1.0.0
     */
    void onTermsAndConditionClick(Activity activity);

    /**
     * On terms and condition clicked callback.
     *
     * @param activity launching activity as callback
     *                 @since 1.0.0
     */
    void onPersonalConsentClick(Activity activity);


}
