/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.prodreg.register;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.constants.RegistrationState;
import com.philips.cdp.prodreg.error.ErrorHandler;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.listener.MetadataListener;
import com.philips.cdp.prodreg.listener.ProdRegListener;
import com.philips.cdp.prodreg.listener.RegisteredProductsListener;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponse;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponseData;
import com.philips.cdp.prodreg.model.registeredproducts.RegisteredResponseData;
import com.philips.cdp.prodreg.model.registerproduct.RegistrationResponse;
import com.philips.cdp.prodreg.model.registerproduct.RegistrationResponseData;
import com.philips.cdp.prodreg.model.registerproduct.RegistrationResponseNewData;
import com.philips.cdp.prodreg.prxrequest.RegistrationRequest;
import com.philips.cdp.prodreg.util.ProdRegUtil;
import com.philips.cdp.prxclient.PRXDependencies;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_APIVERSION_VALUE;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_CONTENTTYYPE_VALUE;

/**
 * Responsible to register and fetch products
 */
public class UserWithProducts {

    public static final int PRODUCT_REGISTRATION = 0;
    public static final int FETCH_REGISTERED_PRODUCTS = 1;
    private static final String TAG = UserWithProducts.class.getSimpleName();
    private int requestType = -1;
    private RegisteredProductsListener registeredProductsListener;
    private Context mContext;
    private LocalRegisteredProducts localRegisteredProducts;
    private ErrorHandler errorHandler;
    private String uuid = "";
    private RegisteredProduct currentRegisteredProduct;
    private ProdRegListener appListener;
    private int processCacheProductsCount;
    private UserDataInterface mUserDataInterface;

    public UserWithProducts(final Context context, final ProdRegListener appListener,UserDataInterface userDataInterface) {
        this.mContext = context;
        mUserDataInterface = userDataInterface;
        this.appListener = appListener;
        setUuid();
        localRegisteredProducts = new LocalRegisteredProducts(mUserDataInterface);
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
        if(mUserDataInterface != null) {
            ArrayList<String> detailsKey = new ArrayList<>();
            detailsKey.add(UserDetailConstants.UUID);
            try {
                HashMap<String,Object> userDetailsMap = mUserDataInterface.getUserDetails(detailsKey);
                String uuid = userDetailsMap.get(UserDetailConstants.UUID).toString();
                this.uuid = uuid != null ? uuid : "";
            } catch (Exception e) {
                ProdRegLogger.d(TAG, "setUuid failed : "+e.getMessage());
            }
        }else {
            ProdRegLogger.d(TAG, "setUuid failed, userDataInterface null");
        }
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
            if (null != registeredProduct) {
                initRegistration(registeredProduct);
            }
        }
    }

    private void initRegistration(final RegisteredProduct registeredProduct) {
        final RegistrationState registrationState = registeredProduct.getRegistrationState();
        final boolean failedOnInvalidInput = isFailedOnInvalidInput(registeredProduct);
        if (!failedOnInvalidInput && (registrationState == RegistrationState.PENDING || registrationState == RegistrationState.FAILED) && getUuid().equals(registeredProduct.getUserUUid())) {
            if (!getUserProduct().isUserSignedIn(mContext)) {
                getUserProduct().updateLocaleCache(registeredProduct, ProdRegError.USER_NOT_SIGNED_IN, RegistrationState.FAILED);
                sendErrorCallBack(registeredProduct);
            } else if (registeredProduct.getPurchaseDate() != null && registeredProduct.getPurchaseDate().length() != 0 && !new ProdRegUtil().isValidDate(registeredProduct.getPurchaseDate())) {
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
     * @param registeredProductsListener - callback listener to get list of products
     */
    public void getRegisteredProducts(final RegisteredProductsListener registeredProductsListener) {
        if (mUserDataInterface != null && mUserDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
            setRequestType(FETCH_REGISTERED_PRODUCTS);
            this.registeredProductsListener = registeredProductsListener;
            final RemoteRegisteredProducts remoteRegisteredProducts = new RemoteRegisteredProducts();
            remoteRegisteredProducts.getRegisteredProducts(mContext, getUserProduct(), mUserDataInterface, registeredProductsListener);
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
        AppInfraInterface appInfra = PRUiHelper.getInstance().getAppInfraInstance();
        PRXDependencies prxDependencies = new PRXDependencies(context, appInfra, ProdRegConstants.PRG_SUFFIX); // use existing appinfra instance
        RequestManager mRequestManager = new RequestManager();
        mRequestManager.init(prxDependencies); // pass prxdependency

        return mRequestManager;
    }

    protected boolean isUserSignedIn(final Context context) {
        return (mUserDataInterface != null && (mUserDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN));
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
                RegisteredProduct ctnRegistered = isCtnRegistered(registeredProducts, registeredProduct);
                if (ctnRegistered.getRegistrationState() != RegistrationState.REGISTERED) {
                    registeredProduct.getProductMetadata(mContext, getUserProduct().getMetadataListener(registeredProduct));
                } else {
                    updateWithCallBack(ctnRegistered, ProdRegError.PRODUCT_ALREADY_REGISTERED, RegistrationState.REGISTERED);
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

    @NonNull
    MetadataListener getMetadataListener(final RegisteredProduct registeredProduct) {
        return new MetadataListener() {
            @Override
            public void onMetadataResponse(final ProductMetadataResponse productMetadataResponse) {
                ProductMetadataResponseData productData = productMetadataResponse.getData();
                boolean isValidSerialNumber = isValidSerialNumber(productData, registeredProduct);
                boolean isValidDate = true;
                if (productData != null && productData.getRequiresDateOfPurchase().equalsIgnoreCase("true")) {
                    isValidDate = isValidPurchaseDate(registeredProduct.getPurchaseDate());
                }
                if (!isValidDate && !isValidSerialNumber) {
                    updateWithCallBack(registeredProduct, ProdRegError.INVALID_SERIAL_NUMBER_AND_PURCHASE_DATE, RegistrationState.FAILED);
                } else if (!isValidDate) {
                    updateWithCallBack(registeredProduct, ProdRegError.MISSING_DATE, RegistrationState.FAILED);
                } else if (!isValidSerialNumber) {
                    updateWithCallBack(registeredProduct, ProdRegError.INVALID_SERIALNUMBER, RegistrationState.FAILED);
                } else {
                    getUserProduct().makeRegistrationRequest(mContext, registeredProduct);
                }
            }

            @Override
            public void onErrorResponse(final String errorMessage, final int responseCode) {
                getErrorHandler().handleError(getUserProduct(), registeredProduct, responseCode);
            }
        };
    }


    private void updateWithCallBack(final RegisteredProduct registeredProduct, final ProdRegError prodRegError, final RegistrationState registrationState) {
        updateLocaleCache(registeredProduct, prodRegError, registrationState);
        sendErrorCallBack(registeredProduct);
    }

    protected boolean isValidPurchaseDate(String purchaseDate) {
        return purchaseDate != null && purchaseDate.length() > 0;
    }

    public RegisteredProduct isCtnRegistered(final List<RegisteredProduct> registeredProducts, final RegisteredProduct registeredProduct) {
        if (null != registeredProduct) {
            for (RegisteredProduct result : registeredProducts) {
                if (registeredProduct.getCtn().equalsIgnoreCase(result.getCtn()) && registeredProduct.getSerialNumber().equals(result.getSerialNumber()) && result.getRegistrationState() == RegistrationState.REGISTERED) {
                    return result;
                }
            }
        }
        return registeredProduct;
    }

    protected boolean isValidSerialNumber(final ProductMetadataResponseData data, final RegisteredProduct registeredProduct) {
        final boolean requiredSerialNumber = data != null && data.getRequiresSerialNumber().equalsIgnoreCase("true");
        final boolean isValidSerialNumber = new ProdRegUtil().isValidSerialNumber(requiredSerialNumber, data.getSerialNumberFormat(), registeredProduct.getSerialNumber());
        return isValidSerialNumber;
    }

    @NonNull
    protected RegistrationRequest getRegistrationRequest(final Context context, final RegisteredProduct registeredProduct) {
        RegistrationRequest registrationRequest = new RegistrationRequest(registeredProduct.getCtn(), ProdRegConstants.REGISTRATIONREQUEST_SERVICE_ID, registeredProduct.getSector(),
                registeredProduct.getCatalog(), mUserDataInterface.isOIDCToken());
        registrationRequest.setSector(registeredProduct.getSector());
        registrationRequest.setCatalog(registeredProduct.getCatalog());
        registrationRequest.setRegistrationChannel(getUserProduct().getRegistrationChannel());
        registrationRequest.setPurchaseDate(registeredProduct.getPurchaseDate());
        registrationRequest.setProductSerialNumber(registeredProduct.getSerialNumber());
        registrationRequest.setShouldSendEmailAfterRegistration(String.valueOf(registeredProduct.getEmail()));

        try {
            ArrayList<String> detailskey = new ArrayList<>();
            detailskey.add(UserDetailConstants.RECEIVE_MARKETING_EMAIL);
            detailskey.add(UserDetailConstants.ACCESS_TOKEN);
            HashMap<String,Object> userDetailsMap = mUserDataInterface.getUserDetails(detailskey);
            boolean isRcvMrktEmail = false;
            if(userDetailsMap.get(UserDetailConstants.RECEIVE_MARKETING_EMAIL)!=null){
                 isRcvMrktEmail = (boolean) userDetailsMap.get(UserDetailConstants.RECEIVE_MARKETING_EMAIL);
            }
            String accessToken = userDetailsMap.get(UserDetailConstants.ACCESS_TOKEN).toString();
            registrationRequest.setAccessToken(accessToken);
            registrationRequest.setReceiveMarketEmail(isRcvMrktEmail);
            registrationRequest.setApiKey(new ProdRegUtil().getAPIKey());
            registrationRequest.setApiVersion(PROD_REG_APIVERSION_VALUE);
            registrationRequest.setContentType(PROD_REG_CONTENTTYYPE_VALUE);

        } catch (Exception e) {
            ProdRegLogger.e(TAG,"Error in fetching user details.");
        }
       return registrationRequest;
    }

    @NonNull
    protected String getRegistrationChannel() {
        final String MICRO_SITE_ID = "MS";
        return MICRO_SITE_ID + PRUiHelper.getInstance().getAppInfraInstance().getAppIdentity().getMicrositeId();
    }

    /**
     * API refresh the access token
     *
     * @param registeredProduct - List of products to be registered
     */
    public void onAccessTokenExpire(final RegisteredProduct registeredProduct) {
        if(mUserDataInterface != null)
            mUserDataInterface.refreshSession(getRefreshListener(registeredProduct,mContext));
        else
            ProdRegLogger.i(TAG,"onAccessTokenExpire :: mUserDataInterface null");
    }


    protected RefreshSessionListener getRefreshListener(final RegisteredProduct registeredProduct, final Context mContext){
        return new RefreshSessionListener() {
            @Override
            public void refreshSessionSuccess() {
                getUserProduct().retryRequests(mContext, registeredProduct);
            }

            @Override
            public void refreshSessionFailed(Error error) {
                if (requestType == PRODUCT_REGISTRATION && registeredProduct != null) {
                    getLocalRegisteredProductsInstance().updateRegisteredProducts(registeredProduct);
                    getUserProduct().updateWithCallBack(registeredProduct, ProdRegError.ACCESS_TOKEN_INVALID, RegistrationState.FAILED);
                } else if (requestType == FETCH_REGISTERED_PRODUCTS && registeredProductsListener != null) {
                    registeredProductsListener.getRegisteredProducts(getLocalRegisteredProductsInstance().getRegisteredProducts(), -1);
                }
            }

            @Override
            public void forcedLogout() {
                //TODO: Shashi, Check what action need to perform on forced logout.
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
                RegistrationResponseNewData registrationResponse = (RegistrationResponseNewData) responseData;
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

    protected void mapRegistrationResponse(final RegistrationResponseNewData registrationResponse, final RegisteredProduct registeredProduct) {
        final RegistrationResponseNewData data = registrationResponse;
        registeredProduct.setEndWarrantyDate(data.getData().getAttributes().getExtendedWarrantyExpires()+"");
        registeredProduct.setContractNumber(data.getData().getAttributes().getSerialNumber());
    }

    protected void makeRegistrationRequest(final Context mContext, final RegisteredProduct registeredProduct) {
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
