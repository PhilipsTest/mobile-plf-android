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

package com.philips.platform.ecs.microService.manager

import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.microService.error.ECSException
import com.philips.platform.ecs.microService.util.ECSDataHolder

class ECSApiValidator {

    fun getECSException(apiType: APIType): ECSException? {

        return when (apiType) {
            APIType.Locale -> validateLocale()
            APIType.LocaleAndHybris -> validateLocaleAndHybris()
            APIType.LocaleHybrisAndAuth -> validateLocaleHybrisAndAuth()
        }

    }

    private fun validateLocale(): ECSException? {
        return if (ECSDataHolder.locale == null) ECSException(ECSErrorEnum.ECSLocaleNotFound.localizedErrorString, ECSErrorEnum.ECSLocaleNotFound.errorCode) else null
    }

    private fun validateLocaleAndHybris(): ECSException? {
        if (validateLocale() != null) return validateLocale()
        if (!ECSDataHolder.config.isHybris) return ECSException(ECSErrorEnum.ECSSiteIdNotFound.localizedErrorString, ECSErrorEnum.ECSBaseURLNotFound.errorCode)
        return null
    }

    private fun validateLocaleHybrisAndAuth(): ECSException? {

        if (validateLocale() != null) return validateLocale()
        if (validateLocaleAndHybris() != null) return validateLocaleAndHybris()
        if (ECSDataHolder.eCSOAuthData == null) return ECSException(ECSErrorEnum.ECSOAuthDetailError.localizedErrorString, ECSErrorEnum.ECSOAuthDetailError.errorCode)
        return null
    }
}