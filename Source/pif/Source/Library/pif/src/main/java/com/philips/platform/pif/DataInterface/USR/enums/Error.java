/*
 * Copyright (c) Koninklijke Philips N.V. 2019
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */
package com.philips.platform.pif.DataInterface.USR.enums;

/**
 * Describe error code and error message
 *
 * @since 1903
 */
public class Error {
    private int errCode;
    private String errDesc;

    public enum UserDetailError {
        InvalidFields(1000, "INVALID FIELDS"),
        NotLoggedIn(1001, "USER NOT LOGGED IN"),
        InvalidUserDetailsKeys(1002, "User details keys are invalid");
        private int errorCode;
        private String errorMsg;

        UserDetailError(int errorCode, String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }
    }


    public Error(int errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
    }

    public Error(UserDetailError userDetailError){
        errCode = userDetailError.errorCode;
        errDesc = userDetailError.errorMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getErrDesc() {
        return errDesc;
    }
}
