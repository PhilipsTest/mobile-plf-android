package com.philips.cdp.prodreg.register;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.constants.RegistrationState;
import com.philips.cdp.prodreg.error.ErrorHandler;
import com.philips.cdp.prodreg.listener.ProdRegListener;
import com.philips.cdp.prodreg.listener.RegisteredProductsListener;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponseData;
import com.philips.cdp.prodreg.model.registeredproducts.RegisteredResponseData;
import com.philips.cdp.prodreg.model.registerproduct.RegistrationResponse;
import com.philips.cdp.prodreg.model.registerproduct.RegistrationResponseData;
import com.philips.cdp.prodreg.prxrequest.RegistrationRequest;
import com.philips.cdp.prodreg.util.ProdRegUtil;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;

import java.util.List;

/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
public class UserWithProducts {

    public static final int PRODUCT_REGISTRATION = 0;
    public static final int FETCH_REGISTERED_PRODUCTS = 1;
    private static final String TAG = UserWithProducts.class.getSimpleName();
    private int requestType = -1;
    private RegisteredProductsListener registeredProductsListener;
    private Context mContext;
    private User user;
    private LocalRegisteredProducts localRegisteredProducts;
    private ErrorHandler errorHandler;
    private String uuid = "";
    private RegisteredProduct currentRegisteredProduct;
    private ProdRegListener appListener;
    private int processCacheProductsCount;

    UserWithProducts(final Context context, final User user, final ProdRegListener appListener) {
        this.mContext = context;
        this.user = user;
        this.appListener = appListener;
        setUuid();
        localRegisteredProducts = new LocalRegisteredProducts(context, this.user);
        errorHandler = new ErrorHandler();
    }

    /**
     * API get User UUID
     *
     * @return return user UUID
     */
    public String getUuid() {
        return uuid;
    }

    protected void setUuid() {
        this.uuid = getUser().getJanrainUUID() != null ? getUser().getJanrainUUID() : "";
    }

    /**
     * API to register product
     *
     * @param product - instance of product which should include CTN, Serial, Sector and Catalog of product
     */
    public void registerProduct(final Product product) {
        if (appListener == null) {
            throw new RuntimeException("Listener not Set");
        }
        setRequestType(PRODUCT_REGISTRATION);
        currentRegisteredProduct = getUserProduct().createDummyRegisteredProduct(product);
        LocalRegisteredProducts localRegisteredProducts = getLocalRegisteredProductsInstance();
        final RegisteredProduct registeredProductIfExists = currentRegisteredProduct.getRegisteredProductIfExists(localRegisteredProducts);
        currentRegisteredProduct = registeredProductIfExists != null ? registeredProductIfExists : currentRegisteredProduct;
        if (currentRegisteredProduct.getRegistrationState() == RegistrationState.REGISTERED) {
            currentRegisteredProduct.setProdRegError(ProdRegError.PRODUCT_ALREADY_REGISTERED);
            sendErrorCallBack(currentRegisteredProduct);
        } else if (currentRegisteredProduct.getRegistrationState() != RegistrationState.REGISTERING) {
            localRegisteredProducts.store(currentRegisteredProduct);
            initRegistration(currentRegisteredProduct);
        }
    }

    /**
     * API to register products which are cached
     *
     * @param registeredProducts - List of products to be registered
     */
    public void registerCachedProducts(final List<RegisteredProduct> registeredProducts) {
        for (RegisteredProduct registeredProduct : registeredProducts) {
            initRegistration(registeredProduct);
        }
    }

    private void initRegistration(final RegisteredProduct registeredProduct) {
        final RegistrationState registrationState = registeredProduct.getRegistrationState();
        final boolean failedOnInvalidInput = isFailedOnInvalidInput(registeredProduct);
        if (!failedOnInvalidInput && (registrationState == RegistrationState.PENDING || registrationState == RegistrationState.FAILED) && getUuid().equals(registeredProduct.getUserUUid())) {
            ProdRegLogger.e(TAG, registeredProduct.getCtn() + "___" + registeredProduct.getSerialNumber() + "________" + registeredProduct.getUserUUid() + "_________" + getUuid());
            if (!getUserProduct().isUserSignedIn(mContext)) {
                getUserProduct().updateLocaleCache(registeredProduct, ProdRegError.USER_NOT_SIGNED_IN, RegistrationState.FAILED);
                sendErrorCallBack(registeredProduct);
            } else if (registeredProduct.getPurchaseDate() != null && registeredProduct.getPurchaseDate().length() != 0 && !ProdRegUtil.isValidDate(registeredProduct.getPurchaseDate())) {
                updateWithCallBack(registeredProduct, ProdRegError.INVALID_DATE, RegistrationState.FAILED);
            } else {
                UserWithProducts userWithProducts = getUserProduct();
                userWithProducts.updateLocaleCache(registeredProduct, registeredProduct.getProdRegError(), RegistrationState.REGISTERING);
                userWithProducts.getRegisteredProducts(userWithProducts.getRegisteredProductsListener(registeredProduct));
            }
        } else if (currentRegisteredProduct != null && currentRegisteredProduct.equals(registeredProduct) && failedOnInvalidInput) {
            appListener.onProdRegFailed(registeredProduct, getUserProduct());
        }
    }

    public void sendErrorCallBack(final RegisteredProduct registeredProduct) {
        if (currentRegisteredProduct != null && currentRegisteredProduct.equals(registeredProduct))
            appListener.onProdRegFailed(registeredProduct, getUserProduct());

    }

    protected boolean isFailedOnInvalidInput(final RegisteredProduct registeredProduct) {
        final ProdRegError prodRegError = registeredProduct.getProdRegError();
        return prodRegError != null && (prodRegError == ProdRegError.INVALID_CTN || prodRegError == ProdRegError.INVALID_SERIALNUMBER);
    }

    /**
     * API to fetch list of products which are registered locally and remote
     *
     * @param registeredProductsListener - call back listener to get list of products
     */
    public void getRegisteredProducts(final RegisteredProductsListener registeredProductsListener) {
        if (getUser().isUserSignIn()) {
            setRequestType(FETCH_REGISTERED_PRODUCTS);
            this.registeredProductsListener = registeredProductsListener;
            final RemoteRegisteredProducts remoteRegisteredProducts = new RemoteRegisteredProducts();
            remoteRegisteredProducts.getRegisteredProducts(mContext, getUserProduct(), getUser(), registeredProductsListener);
        } else {
            registeredProductsListener.getRegisteredProducts(getLocalRegisteredProductsInstance().getRegisteredProducts(), -1);
        }
    }

    /**
     * API will update to Error scenario to Locale cache
     *
     * @param registeredProduct - instance registeredProduct which include ctn,Sector and Catalog
     * @param prodRegError      - to get Error code and description
     * @param registrationState - registrationState as Enum's
     */
    public void updateLocaleCache(final RegisteredProduct registeredProduct, final ProdRegError prodRegError, final RegistrationState registrationState) {
        registeredProduct.setRegistrationState(registrationState);
        registeredProduct.setProdRegError(prodRegError);
        if (prodRegError == ProdRegError.INVALID_DATE || prodRegError == ProdRegError.MISSING_DATE) {
            getLocalRegisteredProductsInstance().removeProductFromCache(registeredProduct);
        } else
            getLocalRegisteredProductsInstance().updateRegisteredProducts(registeredProduct);
    }

    @NonNull
    protected RequestManager getRequestManager(final Context context) {
        RequestManager mRequestManager = new RequestManager();
        mRequestManager.init(context);
        return mRequestManager;
    }

    protected boolean isUserSignedIn(final Context context) {
        User mUser = getUser();
        return mUser.isUserSignIn() && mUser.getEmailVerificationStatus();
    }

    @NonNull
    UserWithProducts getUserProduct() {
        return this;
    }

    int getRequestType() {
        return requestType;
    }

    protected void setRequestType(final int requestType) {
        this.requestType = requestType;
    }

    @NonNull
    RegisteredProductsListener getRegisteredProductsListener(final RegisteredProduct registeredProduct) {
        return new RegisteredProductsListener() {
            @Override
            public void getRegisteredProducts(final List<RegisteredProduct> registeredProducts, final long timeStamp) {
                if (!isCtnRegistered(registeredProducts, registeredProduct)) {
                    getUserProduct().makeRegistrationRequest(mContext, registeredProduct);
                } else {
                    updateWithCallBack(registeredProduct, ProdRegError.PRODUCT_ALREADY_REGISTERED, RegistrationState.REGISTERED);
                }
            }
        };
    }

    protected ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    protected RegisteredProduct[] getRegisteredProductsFromResponse(final RegisteredResponseData[] results, final Gson gson) {
        return gson.fromJson(gson.toJson(results), RegisteredProduct[].class);
    }

    private void updateWithCallBack(final RegisteredProduct registeredProduct, final ProdRegError prodRegError, final RegistrationState registrationState) {
        getUserProduct().updateLocaleCache(registeredProduct, prodRegError, registrationState);
        sendErrorCallBack(registeredProduct);
    }

    protected boolean isValidPurchaseDate(String purchaseDate) {
        return purchaseDate != null && purchaseDate.length() > 0;
    }

    protected boolean isCtnRegistered(final List<RegisteredProduct> registeredProducts, final RegisteredProduct registeredProduct) {
        for (RegisteredProduct result : registeredProducts) {
            if (registeredProduct.getCtn().equalsIgnoreCase(result.getCtn()) && registeredProduct.getSerialNumber().equals(result.getSerialNumber()) && result.getRegistrationState() == RegistrationState.REGISTERED) {
                return true;
            }
        }
        return false;
    }

    protected boolean isValidSerialNumber(final ProductMetadataResponseData data, final RegisteredProduct registeredProduct) {
        final boolean requiredSerialNumber = data != null && data.getRequiresSerialNumber().equalsIgnoreCase("true");
        final boolean isValidSerialNumber = ProdRegUtil.isValidSerialNumber(requiredSerialNumber, data.getSerialNumberFormat(), registeredProduct.getSerialNumber());
        return isValidSerialNumber;
    }

    @NonNull
    protected RegistrationRequest getRegistrationRequest(final Context context, final RegisteredProduct registeredProduct) {
        RegistrationRequest registrationRequest = new RegistrationRequest(registeredProduct.getCtn(), registeredProduct.getSerialNumber(), getUser().getAccessToken());
        registrationRequest.setSector(registeredProduct.getSector());
        registrationRequest.setCatalog(registeredProduct.getCatalog());
        final String MICRO_SITE_ID = "MS";
        registrationRequest.setRegistrationChannel(MICRO_SITE_ID + RegistrationConfiguration.getInstance().getPilConfiguration().getMicrositeId());
        registrationRequest.setPurchaseDate(registeredProduct.getPurchaseDate());
        registrationRequest.setProductSerialNumber(registeredProduct.getSerialNumber());
        registrationRequest.setShouldSendEmailAfterRegistration(String.valueOf(registeredProduct.getEmail()));
        return registrationRequest;
    }

    /**
     * API refresh the access token
     *
     * @param registeredProduct - List of products to be registered
     */
    public void onAccessTokenExpire(final RegisteredProduct registeredProduct) {
        final User user = getUser();
        user.refreshLoginSession(getUserProduct().getRefreshLoginSessionHandler(registeredProduct, mContext));
    }

    @NonNull
    protected RefreshLoginSessionHandler getRefreshLoginSessionHandler(final RegisteredProduct registeredProduct, final Context mContext) {
        return new RefreshLoginSessionHandler() {
            @Override
            public void onRefreshLoginSessionSuccess() {
                getUserProduct().retryRequests(mContext, registeredProduct);
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(final int error) {
                ProdRegLogger.d(TAG, "error in refreshing session");
                if (requestType == PRODUCT_REGISTRATION && registeredProduct != null) {
                    getLocalRegisteredProductsInstance().updateRegisteredProducts(registeredProduct);
                    getUserProduct().updateWithCallBack(registeredProduct, ProdRegError.ACCESS_TOKEN_INVALID, RegistrationState.FAILED);
                } else if (requestType == FETCH_REGISTERED_PRODUCTS && registeredProductsListener != null) {
                    registeredProductsListener.getRegisteredProducts(getLocalRegisteredProductsInstance().getRegisteredProducts(), -1);
                }
            }

            @Override
            public void onRefreshLoginSessionInProgress(final String s) {

            }
        };
    }

    protected void retryRequests(final Context mContext, final RegisteredProduct registeredProduct) {
        switch (requestType) {
            case PRODUCT_REGISTRATION:
                getUserProduct().makeRegistrationRequest(mContext, registeredProduct);
                break;
            case FETCH_REGISTERED_PRODUCTS:
                getUserProduct().getRegisteredProducts(getRegisteredProductsListener());
                break;
            default:
                break;
        }
    }

    @NonNull
    ResponseListener getPrxResponseListener(final RegisteredProduct registeredProduct) {
        return new ResponseListener() {
            @Override
            public void onResponseSuccess(final ResponseData responseData) {
                registeredProduct.setRegistrationState(RegistrationState.REGISTERED);
                RegistrationResponse registrationResponse = (RegistrationResponse) responseData;
                getUserProduct().mapRegistrationResponse(registrationResponse, registeredProduct);
                registeredProduct.setProdRegError(null);
                sendSuccessFullCallBack(registeredProduct);
                getLocalRegisteredProductsInstance().updateRegisteredProducts(registeredProduct);
                if (currentRegisteredProduct != null && processCacheProductsCount < 1) {
                    processCacheProductsCount++;
                    final List<RegisteredProduct> registeredProducts = localRegisteredProducts.getRegisteredProducts();
                    getUserProduct().registerCachedProducts(registeredProducts);
                }
            }

            @Override
            public void onResponseError(PrxError prxError) {
                try {
                    getErrorHandler().handleError(getUserProduct(), registeredProduct, prxError.getStatusCode());
                    if (currentRegisteredProduct != null && processCacheProductsCount < 1) {
                        processCacheProductsCount++;
                        final List<RegisteredProduct> registeredProducts = localRegisteredProducts.getRegisteredProducts();
                        registeredProducts.remove(registeredProduct);
                        getUserProduct().registerCachedProducts(registeredProducts);
                    }
                } catch (Exception e) {
                    ProdRegLogger.e(TAG, e.getMessage());
                }
            }
        };
    }

    private void sendSuccessFullCallBack(final RegisteredProduct registeredProduct) {
        if (currentRegisteredProduct != null && currentRegisteredProduct.equals(registeredProduct))
            appListener.onProdRegSuccess(registeredProduct, getUserProduct());
    }

    protected void mapRegistrationResponse(final RegistrationResponse registrationResponse, final RegisteredProduct registeredProduct) {
        final RegistrationResponseData data = registrationResponse.getData();
        registeredProduct.setEndWarrantyDate(data.getWarrantyEndDate());
        registeredProduct.setContractNumber(data.getContractNumber());
    }

    public void makeRegistrationRequest(final Context mContext, final RegisteredProduct registeredProduct) {
        setRequestType(PRODUCT_REGISTRATION);
        RegistrationRequest registrationRequest = getRegistrationRequest(mContext, registeredProduct);
        RequestManager mRequestManager = getRequestManager(mContext);
        mRequestManager.executeRequest(registrationRequest, getPrxResponseListener(registeredProduct));
    }

    protected RegisteredProductsListener getRegisteredProductsListener() {
        return registeredProductsListener;
    }

    protected long getTimeStamp() {
        return System.currentTimeMillis();
    }

    @NonNull
    protected LocalRegisteredProducts getLocalRegisteredProductsInstance() {
        return localRegisteredProducts;
    }

    @NonNull
    protected User getUser() {
        return user;
    }

    protected RegisteredProduct createDummyRegisteredProduct(final Product product) {
        if (product != null) {
            RegisteredProduct registeredProduct = new RegisteredProduct(product.getCtn(), product.getSector(), product.getCatalog());
            registeredProduct.setSerialNumber(product.getSerialNumber());
            registeredProduct.setPurchaseDate(product.getPurchaseDate());
            registeredProduct.sendEmail(product.getEmail());
            registeredProduct.setRegistrationState(RegistrationState.PENDING);
            registeredProduct.setUserUUid(getUuid());
            return registeredProduct;
        }
        return null;
    }

    protected void setCurrentRegisteredProduct(final RegisteredProduct currentRegisteredProduct) {
        this.currentRegisteredProduct = currentRegisteredProduct;
    }
}
