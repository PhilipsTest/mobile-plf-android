/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.error;

public class ECSError {
    private  String errorTrype;
    private  int errorcode;


    public ECSError( int errorcode ,String errorType) {
        this.errorcode = errorcode;
        this.errorTrype = errorType;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public String getErrorType() {
        return errorTrype;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }
}
