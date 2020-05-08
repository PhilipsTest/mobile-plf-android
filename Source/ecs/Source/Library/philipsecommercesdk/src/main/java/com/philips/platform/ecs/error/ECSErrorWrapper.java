/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.error;

public class ECSErrorWrapper  {

    Exception exception;
    ECSError ecsError;

    public ECSErrorWrapper(Exception exception, ECSError ecsError) {
        this.exception = exception;
        this.ecsError = ecsError;
    }

    public Exception getException() {
        return exception;
    }

    public ECSError getEcsError() {
        return ecsError;
    }

}
