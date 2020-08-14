/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.rest

interface CCBRequestInterface {

    fun getUrl(): String

    fun getHeader(): Map<String, String>

    fun getBody(): String?

    fun getMethodType(): Int
}