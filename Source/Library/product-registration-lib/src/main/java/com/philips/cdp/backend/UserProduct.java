package com.philips.cdp.backend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.philips.cdp.handler.ErrorType;
import com.philips.cdp.handler.ProdRegConstants;
import com.philips.cdp.handler.ProdRegListener;
import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.model.ProdRegMetaData;
import com.philips.cdp.model.ProdRegMetaDataResponse;
import com.philips.cdp.model.ProdRegRegisteredDataResponse;
import com.philips.cdp.model.ProdRegRegisteredResults;
import com.philips.cdp.product_registration_lib.R;
import com.philips.cdp.prxclient.Logger.PrxLogger;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;
import com.philips.cdp.prxrequest.RegisteredProductsRequest;
import com.philips.cdp.prxrequest.RegistrationRequest;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class UserProduct {

    private final String TAG = getClass() + "";
    private Context mContext;
    private String requestType;
    private Sector sector;
    private Catalog catalog;
    private String locale;
    private Product product;

    public UserProduct(final Sector sector, final Catalog catalog) {
        this.sector = sector;
        this.catalog = catalog;
    }

    public void getRegisteredProducts(final Context context, final ProdRegListener listener) {
        this.mContext = context;
        this.requestType = ProdRegConstants.FETCH_REGISTERED_PRODUCTS;
        RegisteredProductsRequest registeredProductsRequest = getRegisteredProductsRequest(context);
        final RequestManager mRequestManager = getRequestManager(context);
        mRequestManager.executeRequest(registeredProductsRequest, getLocalResponseListener(listener));
    }

    @NonNull
    RegisteredProductsRequest getRegisteredProductsRequest(final Context context) {
        RegisteredProductsRequest registeredProductsRequest = new RegisteredProductsRequest();
        registeredProductsRequest.setSector(getSector());
        registeredProductsRequest.setCatalog(getCatalog());
        registeredProductsRequest.setAccessToken(new User(context).getAccessToken());
        registeredProductsRequest.setmLocale(getLocale());
        return registeredProductsRequest;
    }

    @NonNull
    RequestManager getRequestManager(final Context context) {
        RequestManager mRequestManager = new RequestManager();
        mRequestManager.init(context);
        return mRequestManager;
    }

    public void registerProduct(final Context context, final Product product, final ProdRegListener appListener) {
        this.mContext = context;
        this.product = product;
        this.requestType = ProdRegConstants.PRODUCT_REGISTRATION;

        if (!isUserSignedIn(new User(context), context)) {
            appListener.onProdRegFailed(ErrorType.USER_NOT_SIGNED_IN);
        } else {
            if (!isValidaDate(product.getPurchaseDate())) {
                appListener.onProdRegFailed(ErrorType.INVALID_DATE);
            } else {
                UserProduct userProduct = getUserProduct(product);
                userProduct.setLocale(this.locale);
                userProduct.getRegisteredProducts(context, getRegisteredProductsListener(context, product, appListener));
            }
        }
    }

    @NonNull
    private RegistrationRequest getRegistrationRequest(final Context context, final Product product) {
        RegistrationRequest registrationRequest = new RegistrationRequest(product.getCtn(), product.getSerialNumber(), new User(context).getAccessToken());
        registrationRequest.setSector(product.getSector());
        registrationRequest.setCatalog(product.getCatalog());
        registrationRequest.setmLocale(product.getLocale());
        registrationRequest.setProductSerialNumber(product.getSerialNumber());
        registrationRequest.setAccessToken(new User(context).getAccessToken());
        registrationRequest.setRegistrationChannel(ProdRegConstants.MICRO_SITE_ID + RegistrationConfiguration.getInstance().getPilConfiguration().getMicrositeId());
        registrationRequest.setPurchaseDate(product.getPurchaseDate());
        return registrationRequest;
    }

    void handleError(final int statusCode, final ProdRegListener listener) {
        if (statusCode == ErrorType.INVALID_CTN.getCode()) {
            listener.onProdRegFailed(ErrorType.INVALID_CTN);
        } else if (statusCode == ErrorType.USER_TOKEN_EXPIRED.getCode()) {
            onAccessTokenExpire(listener);
        } else if (statusCode == ErrorType.ACCESS_TOKEN_INVALID.getCode()) {
            onAccessTokenExpire(listener);
        } else if (statusCode == ErrorType.INVALID_VALIDATION.getCode()) {
            listener.onProdRegFailed(ErrorType.INVALID_VALIDATION);
        } else if (statusCode == ErrorType.INVALID_SERIALNUMBER.getCode()) {
            listener.onProdRegFailed(ErrorType.INVALID_SERIALNUMBER);
        } else if (statusCode == ErrorType.NO_INTERNET_AVAILABLE.getCode()) {
            listener.onProdRegFailed(ErrorType.NO_INTERNET_AVAILABLE);
        } else if (statusCode == ErrorType.INTERNAL_SERVER_ERROR.getCode()) {
            listener.onProdRegFailed(ErrorType.INTERNAL_SERVER_ERROR);
        } else {
            listener.onProdRegFailed(ErrorType.UNKNOWN);
        }
    }

    private void onAccessTokenExpire(final ProdRegListener listener) {
        final User user = new User(mContext);
        user.refreshLoginSession(new RefreshLoginSessionHandler() {
            @Override
            public void onRefreshLoginSessionSuccess() {
                retryRequests(mContext, listener);
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(final int error) {
                Log.d(TAG, "error in refreshing session");
                listener.onProdRegFailed(ErrorType.REFRESH_ACCESS_TOKEN_FAILED);
            }
        }, mContext);
    }

    private void retryRequests(final Context mContext, final ProdRegListener listener) {
        switch (requestType) {
            case ProdRegConstants.PRODUCT_REGISTRATION:
                registerProduct(mContext, getProduct(), listener);
                break;
            case ProdRegConstants.FETCH_REGISTERED_PRODUCTS:
                getRegisteredProducts(mContext, listener);
                break;
            default:
                break;
        }
    }

    @NonNull
    private ResponseListener getLocalResponseListener(final ProdRegListener listener) {
        return new ResponseListener() {
            @Override
            public void onResponseSuccess(final ResponseData responseData) {
                listener.onProdRegSuccess(responseData);
            }

            @Override
            public void onResponseError(final String errorMessage, final int responseCode) {
                try {
                    handleError(responseCode, listener);
                } catch (Exception e) {
                    PrxLogger.e(TAG, mContext.getString(R.string.volley_error) + e.toString());
                }
            }
        };
    }

    protected boolean isUserSignedIn(final User mUser, final Context context) {
        return mUser.isUserSignIn(context) && mUser.getEmailVerificationStatus(context);
    }

    protected boolean isValidaDate(final String date) {
        if (date != null && date.length() != 0) {
            String[] dates = date.split("-");
            return dates.length > 1 && Integer.parseInt(dates[0]) > 1999;
        }
        return true;
    }

    @NonNull
    UserProduct getUserProduct(final Product product) {
        return new UserProduct(product.getSector(), product.getCatalog());
    }

    String getRequestType() {
        return requestType;
    }

    @NonNull
    ProdRegListener getRegisteredProductsListener(final Context context, final Product product, final ProdRegListener listener) {
        return new ProdRegListener() {
            @Override
            public void onProdRegSuccess(ResponseData responseData) {
                ProdRegRegisteredDataResponse registeredDataResponse = (ProdRegRegisteredDataResponse) responseData;
                ProdRegRegisteredResults[] results = registeredDataResponse.getResults();
                if (!isCtnRegistered(results, product, listener)) ;
                product.getProductMetadata(context, getMetadataListener(product, listener));
            }

            @Override
            public void onProdRegFailed(final ErrorType errorType) {
                listener.onProdRegFailed(errorType);
            }
        };
    }

    @NonNull
    ProdRegListener getMetadataListener(final Product product, final ProdRegListener listener) {
        return new ProdRegListener() {
            @Override
            public void onProdRegSuccess(final ResponseData responseData) {
                ProdRegMetaData productMetaData = (ProdRegMetaData) responseData;
                ProdRegMetaDataResponse productData = productMetaData.getData();
                if (validateSerialNumberFromMetadata(productData, product, listener)
                        && validatePurchaseDateFromMetadata(productData, product, listener))

                    makeRegistrationRequest(mContext, product, listener);
            }

            @Override
            public void onProdRegFailed(ErrorType errorType) {
                listener.onProdRegFailed(errorType);
            }
        };
    }

    private void makeRegistrationRequest(final Context mContext, final Product product, final ProdRegListener listener) {
        RegistrationRequest registrationRequest = getRegistrationRequest(mContext, product);
        registrationRequest.setRegistrationChannel(ProdRegConstants.MICRO_SITE_ID + RegistrationConfiguration.getInstance().getPilConfiguration().getMicrositeId());
        registrationRequest.setmLocale(product.getLocale());
        registrationRequest.setPurchaseDate(product.getPurchaseDate());
        registrationRequest.setProductSerialNumber(product.getSerialNumber());
        RequestManager mRequestManager = getRequestManager(mContext);
        mRequestManager.executeRequest(registrationRequest, getLocalResponseListener(listener));
    }

    protected boolean isCtnRegistered(final ProdRegRegisteredResults[] results, final Product product, final ProdRegListener listener) {
        for (ProdRegRegisteredResults result : results) {
            if (product.getCtn().equalsIgnoreCase(result.getProductModelNumber())) {
                listener.onProdRegFailed(ErrorType.PRODUCT_ALREADY_REGISTERED);
                return true;
            }
        }
        return false;
    }

    protected boolean validateSerialNumberFromMetadata(final ProdRegMetaDataResponse data, final Product product, final ProdRegListener listener) {
        if (data.getRequiresSerialNumber().equalsIgnoreCase("true")) {
            if (processSerialNumber(data, product, listener)) return false;
        } else {
            product.setSerialNumber(null);
        }
        return true;
    }

    private boolean processSerialNumber(final ProdRegMetaDataResponse data, final Product product, final ProdRegListener listener) {
        if (product.getSerialNumber() == null || product.getSerialNumber().length() < 1) {
            listener.onProdRegFailed(ErrorType.MISSING_SERIALNUMBER);
            return true;
        } else if (!product.getSerialNumber().matches(data.getSerialNumberFormat())) {
            listener.onProdRegFailed(ErrorType.INVALID_SERIALNUMBER);
            return true;
        }
        return false;
    }

    protected boolean validatePurchaseDateFromMetadata(final ProdRegMetaDataResponse data, final Product product, final ProdRegListener listener) {
        final String purchaseDate = product.getPurchaseDate();
        if (data.getRequiresDateOfPurchase().equalsIgnoreCase("true")) {
            if (purchaseDate != null && purchaseDate.length() > 0) {
                return true;
            } else {
                listener.onProdRegFailed(ErrorType.MISSING_DATE);
                return false;
            }
        } else
            product.setPurchaseDate(null);
        return true;
    }

    public Sector getSector() {
        return sector;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    public Product getProduct() {
        return product;
    }
}
