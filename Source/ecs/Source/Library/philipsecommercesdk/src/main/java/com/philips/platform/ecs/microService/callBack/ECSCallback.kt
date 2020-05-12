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
package com.philips.platform.ecs.microService.callBack

/**
 * The is a callback interface for ECS.
 */
interface ECSCallback<R, E> {
    /**
     * On response.
     *
     * @param result the result
     */
    fun onResponse(result: R)

    /**
     * On failure.
     * @param ecsError the error with exception ,error code and message
     */
    fun onFailure(ecsError: E)
}