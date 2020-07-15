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

import android.util.Log
import com.philips.platform.ecs.R
import com.philips.platform.ecs.microService.util.ECSDataHolder

enum class ECSErrorType(var resourceID: Int, var errorCode: Int) {

    ECS_volley_error(R.string.ECS_volley_error, 11000),
    ECSinvalid_grant(R.string.ECSinvalid_grant, 5000),
    ECSinvalid_client(R.string.ECSinvalid_client, 5001),
    ECSunsupported_grant_type(R.string.ECSunsupported_grant_type, 5002),
    ECSNoSuchElementError(R.string.ECSNoSuchElementError, 5003),
    ECSCartError(R.string.ECSCartError, 5004),
    InsufficientStockError(R.string.ECSInsufficientStockError, 5005),
    ECSUnknownIdentifierError(R.string.ECSUnknownIdentifierError, 5006),
    ECSCommerceCartModificationError(R.string.ECSCommerceCartModificationError, 5007),
    ECSCartEntryError(R.string.ECSCartEntryError, 5008),
    ECSInvalidTokenError(R.string.ECSInvalidTokenError, 5009),
    ECSUnsupportedVoucherError(R.string.ECSUnsupportedVoucherError, 5010),
    ECSVoucherOperationError(R.string.ECSVoucherOperationError, 5011),
    ECSValidationError(R.string.ECSValidationError, 5012),
    ECSUnsupportedDeliveryModeError(R.string.ECSUnsupportedDeliveryModeError, 5013),
    ECSregionisocode(R.string.ECSregionisocode, 5014),
    ECScountryisocode(R.string.ECScountryisocode, 5015),
    ECSpostalCode(R.string.ECSpostalCode, 5016),
    ECSfirstName(R.string.ECSfirstName, 5017),
    ECSlastName(R.string.ECSlastName, 5018),
    ECSphone1(R.string.ECSphone1, 5019),
    ECSphone2(R.string.ECSphone2, 5020),
    ECSaddressId(R.string.ECSaddressId, 5021),
    ECSsessionCart(R.string.ECSsessionCart, 5022),
    postUrl(R.string.ECSpostUrl, 5023),
    ECSIllegalArgumentError(R.string.ECSIllegalArgumentError, 5024),
    ECSInvalidPaymentInfoError(R.string.ECSInvalidPaymentInfoError, 5025),  //client error
    ECSBaseURLNotFound(R.string.ECSBaseURLNotFound, 5050),
    ECSAppInfraNotFound(R.string.ECSAppInfraNotFound, 5051),
    ECSLocaleNotFound(R.string.ECSLocaleNotFound, 5052),
    ECSPropositionIdNotFound(R.string.ECSPropositionIdNotFound, 5053),
    ECSSiteIdNotFound(R.string.ECSSiteIdNotFound, 5054),
    ECSHybrisNotAvailable(R.string.ECSHybrisNotAvailable, 5055),
    ECSCtnNotProvided(R.string.ECSCtnNotProvided, 5056),
    ECSOAuthNotCalled(R.string.ECSOAuthNotCalled, 5057),
    ECSOAuthDetailError(R.string.ECSOAuthDetailError, 5058),
    ECScountryCodeNotGiven(R.string.ECScountryCodeNotGiven, 5059),
    ECSorderIdNil(R.string.ECSorderIdNil, 5060),
    ECSsomethingWentWrong(R.string.ECSsomethingWentWrong, 5999),

    //PIL Service


    ECSPIL_MISSING_PARAMETER_siteId(R.string.ECSPIL_MISSING_PARAMETER_siteId, 6000),
    ECSPIL_INVALID_PARAMETER_VALUE_siteId(R.string.ECSPIL_MISSING_PARAMETER_siteId, 6001),
    ECSPIL_MISSING_PARAMETER_country(R.string.ECSLocaleNotFound, 6002),
    ECSPIL_MISSING_PARAMETER_language(R.string.ECSLocaleNotFound, 6003),
    ECSPIL_INVALID_PARAMETER_VALUE_locale(R.string.ECSLocaleNotFound, 6004),
    ECSPIL_NOT_FOUND_productId(R.string.ECSPIL_NOT_FOUND_productId, 6005),
    ECSPIL_MISSING_API_VERSION(R.string.ECSPIL_MISSING_API_VERSION, 6006),
    ECSPIL_INVALID_API_VERSION(R.string.ECSPIL_INVALID_API_VERSION, 6007),
    ECSPIL_MISSING_API_KEY(R.string.ECSPIL_MISSING_API_KEY, 6008),
    ECSPIL_INVALID_API_KEY(R.string.ECSPIL_INVALID_API_KEY, 6009),
    ECSPIL_INVALID_PRODUCT_SEARCH_LIMIT(R.string.ECSPIL_INVALID_PRODUCT_SEARCH_LIMIT, 6010),
    ECSPIL_NOT_ACCEPTABLE(R.string.ECSsomethingWentWrong, 6011  ),
    ECSPIL_INTEGRATION_TIMEOUT(R.string.ECSsomethingWentWrong, 6012),
    ECSPIL_BAD_REQUEST(R.string.ECSsomethingWentWrong, 6013  ),
    ECSPIL_UNSUPPORTED_MEDIA_TYPE(R.string.ECSsomethingWentWrong, 6014  ),
    ECSPIL_INVALID_QUANTITY(R.string.ECSPIL_INVALID_QUANTITY, 6015  ), //   quantity > 0
    ECSPIL_NEGATIVE_QUANTITY(R.string.ECSCommerceCartModificationError, 6016  ), //  quantity >= 0
    ECSPIL_MISSING_PARAMETER_productId(R.string.ECSCtnNotProvided, 6017  ),
    ECSPIL_STOCK_EXCEPTION(R.string.ECSPIL_STOCK_EXCEPTION, 6018  );

    // todo promotionFilter , invalid offset



    fun getLocalizedErrorString(): String {

        var localizedError = ECSDataHolder.appInfra?.appInfraContext?.getString(R.string.ECSsomethingWentWrong)+""
        try {
            localizedError = ECSDataHolder.appInfra?.appInfraContext?.resources?.getString(resourceID)+""
        } catch (e: Exception) {
            Log.e("RES_NOT_FOUND", e.message)
        }
        return localizedError
    }

}