/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.pif.DataInterface.MEC;

/**
 * MECException is thrown from MEC public apis while accessing their public API
 * @since 1.0.0
 */
public class MECException extends Exception{

    private int errorCode;

    public static int USER_NOT_LOGGED_IN = 2000;
    public static int NO_INTERNET = 2001;

    /**
     * @param message : Localized message of the exception
     * @param errorCode : error code attached to the exception
     * @since 1.0.0
     */
    public MECException(String message,int errorCode){
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
