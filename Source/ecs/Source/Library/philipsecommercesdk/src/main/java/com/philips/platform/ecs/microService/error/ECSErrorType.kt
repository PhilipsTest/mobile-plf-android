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


    /*
  case ECSPIL_MISSING_PARAMETER_siteId            = 6000

    case ECSPIL_INVALID_PARAMETER_VALUE_siteId      = 6001

    case ECSPIL_MISSING_PARAMETER_country           = 6002

    case ECSPIL_MISSING_PARAMETER_language          = 6003

    case ECSPIL_INVALID_PARAMETER_VALUE_locale      = 6004

    case ECSPIL_NOT_FOUND_productId                 = 6005

    case ECSPIL_MISSING_API_VERSION                 = 6006

    case ECSPIL_INVALID_API_VERSION                 = 6007

    case ECSPIL_MISSING_API_KEY                     = 6008

    case ECSPIL_INVALID_API_KEY                     = 6009

    case ECSPIL_INVALID_PRODUCT_SEARCH_LIMIT        = 6010

    case ECSPIL_NOT_ACCEPTABLE                      = 6011

    case ECSPIL_INTEGRATION_TIMEOUT                 = 6012

    case ECSPIL_BAD_REQUEST                         = 6013

    case ECSPIL_UNSUPPORTED_MEDIA_TYPE              = 6014

    case ECSPIL_NOT_ACCEPTABLE_mimeType             = 6015

    case ECSPIL_INVALID_QUANTITY                    = 6016

    case ECSPIL_INVALID_PARAMETER_VALUE_quantity    = 6017

    case ECSPIL_NEGATIVE_QUANTITY                   = 6018

    case ECSPIL_MISSING_PARAMETER_productId         = 6019

    case ECSPIL_INVALID_PARAMETER_VALUE_productId   = 6020

    case ECSPIL_STOCK_EXCEPTION                     = 6021

    case ECSPIL_INVALID_PARAMETER_VALUE_itemId      = 6022

    case ECSPIL_NOT_FOUND_cartId                    = 6023

    case ECSPIL_BAD_REQUEST_cartId                  = 6024

    case ECSPIL_INVALID_AUTHORIZATION_accessToken   = 6025

    "ECSPIL_INVALID_PARAMETER_VALUE_quantity" = "Please provide a valid quantity";

"ECSPIL_NOT_FOUND_cartId" = "Please provide a valid Cart Id";

"ECSPIL_NOT_ACCEPTABLE_mimeType" = "We have encountered technical glitch. Please try after some time";

"ECSPIL_INVALID_PARAMETER_VALUE_productId" = "Please provide valid CTN";

"ECSPIL_INVALID_PARAMETER_VALUE_itemId" = "The Product is not added in Cart";

"ECSPIL_BAD_REQUEST_cartId" = "No cart created yet";

"ECSPIL_INVALID_AUTHORIZATION_accessToken" = "We have encountered technical glitch (Invalid access token). Please do Hybris Re-Auth";
     */


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
    ECSPIL_NOT_ACCEPTABLE_mimeType(R.string.ECSsomethingWentWrong, 6015  ), //   quantity > 0
    ECSPIL_INVALID_QUANTITY(R.string.ECSPIL_INVALID_QUANTITY, 6016  ), //  quantity >= 0
    ECSPIL_INVALID_PARAMETER_VALUE_quantity(R.string.ECSPIL_INVALID_PARAMETER_VALUE_quantity, 6017  ),
    ECSPIL_NEGATIVE_QUANTITY(R.string.ECSCommerceCartModificationError, 6018  ),
    ECSPIL_MISSING_PARAMETER_productId(R.string.ECSCtnNotProvided, 6019 ),//Please provide the CTN
    ECSPIL_INVALID_PARAMETER_VALUE_productId(R.string.ECSPIL_NOT_FOUND_productId, 6020  ),//Please provide valid CTN
    ECSPIL_STOCK_EXCEPTION(R.string.ECSPIL_STOCK_EXCEPTION, 6021  ),
    ECSPIL_INVALID_PARAMETER_VALUE_itemId(R.string.ECSCartEntryError, 6022 ),
    ECSPIL_NOT_FOUND_cartId(R.string.ECSPIL_NOT_FOUND_cartId, 6023  ),
    ECSPIL_BAD_REQUEST_cartId(R.string.ECSCartError, 6024  ),
    ECSPIL_INVALID_AUTHORIZATION_accessToken(R.string.ECSInvalidTokenError, 6025  );

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