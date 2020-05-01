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
package com.philips.platform.ecs.microService.callBack;

/**
 * The interface Iapsdk callback.
 */
public interface ECSCallback<R,E> {
    /**
     * On response.
     *
     * @param result the result
     */
    public void onResponse(R result);

    /**
     * On failure.
     * @param ecsError the error with exception ,error code and message
     */
    public void onFailure(E ecsError);
}
