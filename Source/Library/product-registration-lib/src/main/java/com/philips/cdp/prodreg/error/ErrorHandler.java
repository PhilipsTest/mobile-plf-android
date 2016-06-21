package com.philips.cdp.prodreg.error;

import android.content.Context;

import com.philips.cdp.prodreg.RegistrationState;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.cdp.prodreg.register.UserWithProducts;
import com.philips.cdp.product_registration_lib.R;

/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
public class ErrorHandler {

    public void handleError(final UserWithProducts userProduct, RegisteredProduct registeredProduct, int statusCode) {

        if (statusCode == ProdRegError.INVALID_CTN.getCode()) {
            userProduct.updateLocaleCache(registeredProduct, ProdRegError.INVALID_CTN, RegistrationState.FAILED);
            userProduct.sendErrorCallBack(registeredProduct);
        } else if (statusCode == ProdRegError.ACCESS_TOKEN_INVALID.getCode()) {
            userProduct.onAccessTokenExpire(registeredProduct);
        } else if (statusCode == ProdRegError.INVALID_VALIDATION.getCode()) {
            userProduct.updateLocaleCache(registeredProduct, ProdRegError.INVALID_VALIDATION, RegistrationState.FAILED);
            userProduct.sendErrorCallBack(registeredProduct);
        } else if (statusCode == ProdRegError.INVALID_SERIALNUMBER.getCode()) {
            userProduct.updateLocaleCache(registeredProduct, ProdRegError.INVALID_SERIALNUMBER, RegistrationState.FAILED);
            userProduct.sendErrorCallBack(registeredProduct);
        } else if (statusCode == ProdRegError.NO_INTERNET_AVAILABLE.getCode()) {
            userProduct.updateLocaleCache(registeredProduct, ProdRegError.NO_INTERNET_AVAILABLE, RegistrationState.FAILED);
            userProduct.sendErrorCallBack(registeredProduct);
        } else if (statusCode == ProdRegError.INTERNAL_SERVER_ERROR.getCode()) {
            userProduct.updateLocaleCache(registeredProduct, ProdRegError.INTERNAL_SERVER_ERROR, RegistrationState.FAILED);
            userProduct.sendErrorCallBack(registeredProduct);
        } else if (statusCode == ProdRegError.TIME_OUT.getCode()) {
            userProduct.updateLocaleCache(registeredProduct, ProdRegError.TIME_OUT, RegistrationState.FAILED);
            userProduct.sendErrorCallBack(registeredProduct);
        } else if (statusCode == ProdRegError.NETWORK_ERROR.getCode()) {
            userProduct.updateLocaleCache(registeredProduct, ProdRegError.NETWORK_ERROR, RegistrationState.FAILED);
            userProduct.sendErrorCallBack(registeredProduct);
        } else if (statusCode == ProdRegError.PARSE_ERROR.getCode()) {
            userProduct.updateLocaleCache(registeredProduct, ProdRegError.PARSE_ERROR, RegistrationState.FAILED);
            userProduct.sendErrorCallBack(registeredProduct);
        } else if (statusCode == ProdRegError.INVALID_SERIAL_NUMBER_AND_PURCHASE_DATE.getCode()) {
            userProduct.updateLocaleCache(registeredProduct, ProdRegError.INVALID_SERIAL_NUMBER_AND_PURCHASE_DATE, RegistrationState.FAILED);
            userProduct.sendErrorCallBack(registeredProduct);
        } else {
            userProduct.updateLocaleCache(registeredProduct, ProdRegError.UNKNOWN, RegistrationState.FAILED);
            userProduct.sendErrorCallBack(registeredProduct);
        }
    }

    public ProdRegErrorMap getError(Context context, int statusCode) {
        if (statusCode == ProdRegError.INVALID_CTN.getCode()) {
            return new ProdRegErrorMap(context.getString(R.string.PPR_Product_Not_Found_Title), context.getString(R.string.PPR_Product_Not_Found_ErrMsg));
        } else if (statusCode == ProdRegError.PRODUCT_ALREADY_REGISTERED.getCode()) {
            return new ProdRegErrorMap(context.getString(R.string.PPR_Already_Registered_title), context.getString(R.string.PPR_Already_Registered_ErrMsg));
        } else if (statusCode == ProdRegError.ACCESS_TOKEN_INVALID.getCode()) {
            return new ProdRegErrorMap(context.getString(R.string.PPR_Authentication_Fail_Title), context.getString(R.string.PPR_Authentication_ErrMsg));
        } else if (statusCode == ProdRegError.INVALID_SERIALNUMBER.getCode()) {
            return new ProdRegErrorMap(context.getString(R.string.PPR_Invalid_SerialNum_Title), context.getString(R.string.PPR_Invalid_SerialNum_ErrMsg));
        } else if (statusCode == ProdRegError.INVALID_DATE.getCode()) {
            return new ProdRegErrorMap(context.getString(R.string.PPR_Invalid_SerialNum_Title), context.getString(R.string.PPR_Invalid_SerialNum_ErrMsg));
        } else if (statusCode == ProdRegError.NO_INTERNET_AVAILABLE.getCode()) {
            return new ProdRegErrorMap(context.getString(R.string.PPR_No_Internet_Title), context.getString(R.string.PPR_No_Internet_ErrMsg));
        } else if (statusCode == ProdRegError.INTERNAL_SERVER_ERROR.getCode()) {
            return new ProdRegErrorMap(context.getString(R.string.PPR_Communication_Err_Title), context.getString(R.string.PPR_Unable_Connect_Server_ErrMsg));
        } else if (statusCode == ProdRegError.TIME_OUT.getCode()) {
            return new ProdRegErrorMap(context.getString(R.string.PPR_Communication_Err_Title), context.getString(R.string.PPR_Unable_Connect_Server_ErrMsg));
        } else if (statusCode == ProdRegError.NETWORK_ERROR.getCode()) {
            return new ProdRegErrorMap(context.getString(R.string.PPR_Network_Err_Title), context.getString(R.string.PPR_Network_ErrMsg));
        } else if (statusCode == ProdRegError.INVALID_SERIAL_NUMBER_AND_PURCHASE_DATE.getCode()) {
            return new ProdRegErrorMap(context.getString(R.string.PPR_Invalid_Date_And_serial_title), context.getString(R.string.PPR_Invalid_Date_And_serial_ErrMsg));
        } else {
            return new ProdRegErrorMap(context.getString(R.string.PPR_Unknown_title), context.getString(R.string.PPR_Unknown_ErrMsg));
        }
    }
}
