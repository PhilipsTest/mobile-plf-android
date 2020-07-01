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

import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSErrorType
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
        return if (ECSDataHolder.locale == null) ECSException(ECSErrorType.ECSLocaleNotFound.getLocalizedErrorString(), ECSErrorType.ECSLocaleNotFound.errorCode) else null
    }

    //We also check api key is available or not here too
    private fun validateLocaleAndHybris(): ECSException? {
        if (validateLocale() != null) return validateLocale()
        if (!ECSDataHolder.config.isHybris) return ECSException(ECSErrorType.ECSSiteIdNotFound.getLocalizedErrorString(), ECSErrorType.ECSSiteIdNotFound.errorCode)
        if (ECSDataHolder.getAPIKey() == null) return ECSException(ECSErrorType.ECSPIL_INVALID_API_KEY.getLocalizedErrorString(), ECSErrorType.ECSPIL_INVALID_API_KEY.errorCode)
        return null
    }

    private fun validateAuth(): ECSException? {
        if(ECSDataHolder.authToken == null) return ECSException(ECSErrorType.ECSOAuthNotCalled.getLocalizedErrorString(), ECSErrorType.ECSOAuthNotCalled.errorCode)
        return null
    }


    private fun validateLocaleHybrisAndAuth(): ECSException? {

        if (validateLocale() != null) return validateLocale()
        if (validateLocaleAndHybris() != null) return validateLocaleAndHybris()
        if (validateAuth()!=null) return validateAuth()
        return null
    }

     fun validatePageLimit(limitSize:Int): ECSException? {
        return if(limitSize>50) ECSException(ECSErrorType.ECSPIL_INVALID_PRODUCT_SEARCH_LIMIT.getLocalizedErrorString(), ECSErrorType.ECSPIL_INVALID_PRODUCT_SEARCH_LIMIT.errorCode) else null
     }

    fun validateCTN(ctn: String): ECSException?{

        if(ctn.trim().isEmpty() || ctn.trim().contains(" ")){
          return ECSException(ECSErrorType.ECSUnknownIdentifierError.getLocalizedErrorString(), ECSErrorType.ECSUnknownIdentifierError.errorCode)
         }
        return null
    }

}