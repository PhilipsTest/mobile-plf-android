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
package com.philips.platform.ecs.microService.error

/**
 * @param message : Localized message of the exception
 * @param errorCode : error code attached to the exception
 * @since 1.0.0
 */
class ECSException(message: String?, val errorCode: Int) : Exception(message){


    companion object {
        var USER_NOT_LOGGED_IN = 2000
        var NO_INTERNET = 2001
        var HYBRIS_NOT_AVAILABLE = 2002
    }

    /*ECSErrorEnum.ECSLocaleNotFound
    ECSErrorEnum.ECSBaseURLNotFound
    ECSErrorEnum.ECSSiteIdNotFound
    ECSErrorEnum.ECSOAuthDetailError
    ECSErrorEnum.ECSCommerceCartModificationError*/

}