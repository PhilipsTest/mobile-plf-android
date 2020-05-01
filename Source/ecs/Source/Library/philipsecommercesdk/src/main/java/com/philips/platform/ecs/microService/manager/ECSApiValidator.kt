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

    fun getECSException(apiName: APIName): ECSException? {

        when (apiName) {
            APIName.CONFIG -> return validateLocaleAndBaseURL()
        }

        return null
    }


    private fun validateLocale(): ECSException? {
        return if (ECSDataHolder.locale == null) ECSException(ECSErrorEnum.ECSLocaleNotFound.localizedErrorString, ECSErrorEnum.ECSLocaleNotFound.errorCode) else null
    }

    private fun validateBaseURL(): ECSException? {
        return if (ECSDataHolder.baseURL == null) ECSException(ECSErrorEnum.ECSBaseURLNotFound.localizedErrorString, ECSErrorEnum.ECSBaseURLNotFound.errorCode) else null
    }

    private fun validateLocaleAndBaseURL(): ECSException? {
        if(validateLocale()!=null) return validateLocale()
        if(validateBaseURL()!=null) return validateBaseURL()
        return null
    }

    private fun validateLocaleBaseURLAndHybris(): ECSException? {

        if(validateLocale()!=null) return validateLocale()
        if(validateBaseURL()!=null) return validateBaseURL()
        if (!ECSDataHolder.config.isHybris) return ECSException(ECSErrorEnum.ECSSiteIdNotFound.localizedErrorString, ECSErrorEnum.ECSBaseURLNotFound.errorCode)
        return null
    }

    private fun validateLocaleBaseURLHybrisAndAuth():ECSException?{

        if(validateLocale()!=null) return validateLocale()
        if(validateBaseURL()!=null) return validateBaseURL()
        if(validateLocaleBaseURLAndHybris()!=null) return validateLocaleBaseURLAndHybris()
        if(ECSDataHolder.eCSOAuthData==null) return ECSException(ECSErrorEnum.ECSOAuthDetailError.localizedErrorString, ECSErrorEnum.ECSOAuthDetailError.errorCode)
        return null
    }

    /*ECSErrorEnum.ECSLocaleNotFound
   ECSErrorEnum.ECSBaseURLNotFound
   ECSErrorEnum.ECSSiteIdNotFound
   ECSErrorEnum.ECSOAuthDetailError
   ECSErrorEnum.ECSCommerceCartModificationError*/
}