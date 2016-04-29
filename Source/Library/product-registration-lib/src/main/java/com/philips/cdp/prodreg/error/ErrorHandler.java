package com.philips.cdp.prodreg.error;

import com.philips.cdp.prodreg.RegistrationState;
import com.philips.cdp.prodreg.backend.RegisteredProduct;
import com.philips.cdp.prodreg.backend.UserWithProducts;
import com.philips.cdp.prodreg.listener.ProdRegListener;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ErrorHandler {

    public void handleError(final UserWithProducts userProduct, RegisteredProduct registeredProduct, int statusCode, ProdRegListener appListener) {

        if (statusCode == ProdRegError.INVALID_CTN.getCode()) {
            userProduct.updateLocaleCacheOnError(registeredProduct, ProdRegError.INVALID_CTN, RegistrationState.FAILED);
            appListener.onProdRegFailed(registeredProduct);
        } else if (statusCode == ProdRegError.ACCESS_TOKEN_INVALID.getCode()) {
            userProduct.onAccessTokenExpire(registeredProduct, appListener);
        } else if (statusCode == ProdRegError.INVALID_VALIDATION.getCode()) {
            userProduct.updateLocaleCacheOnError(registeredProduct, ProdRegError.INVALID_VALIDATION, RegistrationState.FAILED);
            appListener.onProdRegFailed(registeredProduct);
        } else if (statusCode == ProdRegError.INVALID_SERIALNUMBER.getCode()) {
            userProduct.updateLocaleCacheOnError(registeredProduct, ProdRegError.INVALID_SERIALNUMBER, RegistrationState.FAILED);
            appListener.onProdRegFailed(registeredProduct);
        } else if (statusCode == ProdRegError.NO_INTERNET_AVAILABLE.getCode()) {
            userProduct.updateLocaleCacheOnError(registeredProduct, ProdRegError.NO_INTERNET_AVAILABLE, RegistrationState.FAILED);
            appListener.onProdRegFailed(registeredProduct);
        } else if (statusCode == ProdRegError.INTERNAL_SERVER_ERROR.getCode()) {
            userProduct.updateLocaleCacheOnError(registeredProduct, ProdRegError.INTERNAL_SERVER_ERROR, RegistrationState.FAILED);
            appListener.onProdRegFailed(registeredProduct);
        } else if (statusCode == ProdRegError.METADATA_FAILED.getCode()) {
            userProduct.updateLocaleCacheOnError(registeredProduct, ProdRegError.METADATA_FAILED, RegistrationState.FAILED);
            appListener.onProdRegFailed(registeredProduct);
        } else if (statusCode == ProdRegError.TIME_OUT.getCode()) {
            userProduct.updateLocaleCacheOnError(registeredProduct, ProdRegError.TIME_OUT, RegistrationState.FAILED);
            appListener.onProdRegFailed(registeredProduct);
        } else {
            userProduct.updateLocaleCacheOnError(registeredProduct, ProdRegError.UNKNOWN, RegistrationState.FAILED);
            appListener.onProdRegFailed(registeredProduct);
        }
    }
}
