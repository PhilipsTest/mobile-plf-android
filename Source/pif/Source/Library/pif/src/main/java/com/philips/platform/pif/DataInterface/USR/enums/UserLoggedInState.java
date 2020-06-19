/*
 * Copyright (c) Koninklijke Philips N.V. 2019
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */
package com.philips.platform.pif.DataInterface.USR.enums;

/**
 * API UserLoggedInState is implemented to return the exact status of the user while logging in. The user log in states returned are the following:
 *
 * USER_NOT_LOGGED_IN: Informs the state of the user as not logged in
 * PENDING_VERIFICATION: Informs that the verification is pending for the user
 * PENDING_TERM_CONDITION: Informs that the terms and conditions acceptance is pending for the user.
 * PENDING_HSDP_LOGIN: Informs that HSDP login is pending for the user.
 * USER_LOGGED_IN: Informs that the user is successfully logged in.
 *
 * @since 1804.0
 *
 */
public enum UserLoggedInState {
    USER_NOT_LOGGED_IN,
    //Deprecated
    PENDING_VERIFICATION,
    //Deprecated
    PENDING_TERM_CONDITION,
    //Deprecated
    PENDING_HSDP_LOGIN,
    USER_LOGGED_IN;
}
