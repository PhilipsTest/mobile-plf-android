/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */
package com.philips.platform.mec.integration

/**
 * MECException is thrown from MEC public apis while accessing their public API
 * @param message : Localized message of the exception
 * @param errorCode : error code attached to the exception
 * @since 1.0.0
 */
class MECException(message: String?, val errorCode: Int) : Exception(message) {

    companion object {
        var USER_NOT_LOGGED_IN = 2000
        var NO_INTERNET = 2001
        var HYBRIS_NOT_AVAILABLE = 2002
    }

}