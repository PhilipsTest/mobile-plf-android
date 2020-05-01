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
package com.philips.platform.ecs.microService.model.config.oauth

import java.io.Serializable

/**
 * The type Ecs oauth data which contains OAuth data ad returns Janrain id for hybrisOAthAuthentication and returns refresh token for hybrisRefreshOAuth.
 */
class ECSOAuthData : Serializable {
    val accessToken: String? = null
    val tokenType: String? = null
    val refreshToken: String? = null
    val expiresIn = 0
    val scope: String? = null

}