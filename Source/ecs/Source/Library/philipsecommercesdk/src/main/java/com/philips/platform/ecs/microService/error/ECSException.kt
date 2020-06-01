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
package com.philips.platform.ecs.microService.error

/**
 * @param message : Localized message of the exception
 * @param errorCode : error code attached to the exception
 * @since 2003.0
 */
class ECSException(message: String?, val errorCode: Int) : Exception(message)