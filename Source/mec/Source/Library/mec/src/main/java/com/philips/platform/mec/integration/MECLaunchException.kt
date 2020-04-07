/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */

package com.philips.platform.mec.integration

/**
 * @param message : Localized message of the exception
 * @param errorCode : error code attached to the exception
 * @since 1.0.0
 */

class MECLaunchException(message:String,val errorCode:Int) : Exception(message){

    companion object {
        const val ERROR_CODE_NOT_LOGGED_IN = 2000
        const val ERROR_CODE_NO_INTERNET = 2001
    }
}