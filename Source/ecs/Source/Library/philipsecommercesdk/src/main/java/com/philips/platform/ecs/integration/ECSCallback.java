/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.integration;

import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.error.ECSError;

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
     * @param error     the error object
     * @param ecsError the error code
     */
    public void onFailure(E error, ECSError ecsError);
}
