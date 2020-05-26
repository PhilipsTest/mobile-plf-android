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
 * The is a callback interface to give back result to caller .
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
     * @param ecsError the error with  message (mandatory) , error code (optional) and enum of error type (optional)
     */
    fun onFailure(ecsError: E)
}