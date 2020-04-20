/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.error;

import android.util.Log;

import com.philips.platform.ecs.R;
import com.philips.platform.ecs.util.ECSConfiguration;

public enum ECSErrorEnum {

    //volley Errors
    ECS_volley_error( R.string.ECS_volley_error, 11000),




    ECSinvalid_grant( R.string.ECSinvalid_grant, 5000),
    ECSinvalid_client(R.string.ECSinvalid_client, 5001),
    ECSunsupported_grant_type( R.string.ECSunsupported_grant_type, 5002),

    ECSNoSuchElementError( R.string.ECSNoSuchElementError, 5003),
    ECSCartError( R.string.ECSCartError, 5004),
    InsufficientStockError( R.string.ECSInsufficientStockError, 5005),
    ECSUnknownIdentifierError( R.string.ECSUnknownIdentifierError, 5006),
    ECSCommerceCartModificationError( R.string.ECSCommerceCartModificationError, 5007),
    ECSCartEntryError( R.string.ECSCartEntryError, 5008),
    ECSInvalidTokenError( R.string.ECSInvalidTokenError, 5009),
    ECSUnsupportedVoucherError( R.string.ECSUnsupportedVoucherError, 5010),
    ECSVoucherOperationError( R.string.ECSVoucherOperationError, 5011),
    ECSValidationError( R.string.ECSValidationError, 5012),
    ECSUnsupportedDeliveryModeError( R.string.ECSUnsupportedDeliveryModeError, 5013),
    ECSregionisocode( R.string.ECSregionisocode, 5014),
    ECScountryisocode( R.string.ECScountryisocode, 5015),
    ECSpostalCode( R.string.ECSpostalCode, 5016),
    ECSfirstName(R.string.ECSfirstName, 5017),
    ECSlastName( R.string.ECSlastName, 5018),
    ECSphone1( R.string.ECSphone1, 5019),
    ECSphone2( R.string.ECSphone2, 5020),
    ECSaddressId( R.string.ECSaddressId, 5021),
    ECSsessionCart( R.string.ECSsessionCart, 5022),
    postUrl( R.string.ECSpostUrl, 5023),
    ECSIllegalArgumentError( R.string.ECSIllegalArgumentError, 5024),
    ECSInvalidPaymentInfoError( R.string.ECSInvalidPaymentInfoError, 5025),



    //client error
    ECSBaseURLNotFound( R.string.ECSBaseURLNotFound, 5050),
    ECSAppInfraNotFound( R.string.ECSAppInfraNotFound, 5051),
    ECSLocaleNotFound( R.string.ECSLocaleNotFound, 5052),
    ECSPropositionIdNotFound( R.string.ECSPropositionIdNotFound, 5053),
    ECSSiteIdNotFound( R.string.ECSSiteIdNotFound, 5054),
    ECSHybrisNotAvailable( R.string.ECSHybrisNotAvailable, 5055),
    ECSCtnNotProvided( R.string.ECSCtnNotProvided, 5056),
    ECSOAuthNotCalled( R.string.ECSOAuthNotCalled, 5057),
    ECSOAuthDetailError( R.string.ECSOAuthDetailError, 5058),
    ECScountryCodeNotGiven( R.string.ECScountryCodeNotGiven, 5059),
    ECSorderIdNil( R.string.ECSorderIdNil, 5060),




    ECSsomethingWentWrong( R.string.ECSsomethingWentWrong, 5999);



    int resourceID;
    int errorCode;

    public int getResourceID() {
        return resourceID;
    }


    public int getErrorCode() {
        return errorCode;
    }

    ECSErrorEnum( int resourceID, int errorCode) {
        this.resourceID = resourceID;
        this.errorCode = errorCode;
    }

    public static ECSErrorEnum getErrorEnumFromType(String errorType) {
        return ECSErrorEnum.valueOf(errorType);
    }

    public  String getLocalizedErrorString(){
        String localizedError= ECSConfiguration.INSTANCE.getAppInfra().getAppInfraContext().getString(R.string.ECSsomethingWentWrong);
        try{
            localizedError =   ECSConfiguration.INSTANCE.getAppInfra().getAppInfraContext().getResources().getString(getResourceID());
        } catch(Exception e){
            Log.e("RES_NOT_FOUND", e.getMessage());
        }
        return localizedError;
    }
}
