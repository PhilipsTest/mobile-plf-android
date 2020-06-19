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

package com.philips.platform.ecs.microService.request

import org.mockito.Mockito

fun <T> any(type : Class<T>): T {
    Mockito.any(type)
    return uninitialized()
}

private fun <T> uninitialized(): T = null as T