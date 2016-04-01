package com.philips.cdp.prodreg.backend;

import android.content.Context;
import android.support.annotation.NonNull;

import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.prodreg.MockitoTestCase;
import com.philips.cdp.prodreg.handler.ErrorType;
import com.philips.cdp.prodreg.handler.GetRegisteredProductsListener;
import com.philips.cdp.prodreg.handler.ProdRegConstants;
import com.philips.cdp.prodreg.handler.ProdRegListener;
import com.philips.cdp.prodreg.model.ProductMetadataResponse;
import com.philips.cdp.prodreg.model.ProductMetadataResponseData;
import com.philips.cdp.prodreg.model.RegisteredResponse;
import com.philips.cdp.prodreg.model.RegisteredResponseData;
import com.philips.cdp.prodreg.prxrequest.RegisteredProductsRequest;
import com.philips.cdp.prodreg.prxrequest.RegistrationRequest;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class UserProductTest extends MockitoTestCase {

    UserProduct userProduct;
    private Context context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER);
        context = getInstrumentation().getContext();
    }

    public void testIsUserSignedIn() {
        User userMock = mock(User.class);
        when(userMock.isUserSignIn(context)).thenReturn(true);
        when(userMock.getEmailVerificationStatus(context)).thenReturn(true);
        assertTrue(userProduct.isUserSignedIn(userMock, context));
        when(userMock.isUserSignIn(context)).thenReturn(false);
        when(userMock.getEmailVerificationStatus(context)).thenReturn(true);
        assertFalse(userProduct.isUserSignedIn(userMock, context));
    }

    public void testReturnTrueForValidDate() throws Exception {
        assertTrue(userProduct.isValidaDate("2016-03-22"));
        assertTrue(userProduct.isValidaDate(null));
    }

    public void testReturnFalseForInValidDate() throws Exception {
        assertFalse(userProduct.isValidaDate("1998-03-22"));
    }

    public void testRegisterProductWhenNotSignedIn() {
        UserProduct userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER) {
            @Override
            protected boolean isUserSignedIn(final User mUser, final Context context) {
                return false;
            }
        };
        Product productMock = mock(Product.class);

        userProduct.registerProduct(context, productMock, new ProdRegListener() {
            @Override
            public void onProdRegSuccess(final ResponseData responseData) {

            }

            @Override
            public void onProdRegFailed(final ErrorType errorType) {
                assertEquals(ErrorType.USER_NOT_SIGNED_IN, errorType);
            }
        });
        assertEquals(userProduct.getRequestType(), (ProdRegConstants.PRODUCT_REGISTRATION));
    }

    public void testRegisterProductWhenInValidDate() {
        UserProduct userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER) {
            @Override
            protected boolean isUserSignedIn(final User mUser, final Context context) {
                return true;
            }

            @Override
            protected boolean isValidaDate(final String date) {
                return false;
            }
        };
        Product productMock = mock(Product.class);
        userProduct.registerProduct(context, productMock, new ProdRegListener() {
            @Override
            public void onProdRegSuccess(final ResponseData responseData) {
            }

            @Override
            public void onProdRegFailed(final ErrorType errorType) {
                assertEquals(ErrorType.INVALID_DATE, errorType);
            }
        });
    }

    public void testRegisterProductOnValidParameters() {
        final UserProduct userProductMock = mock(UserProduct.class);
        final GetRegisteredProductsListener prodRegListenerMock = mock(GetRegisteredProductsListener.class);
        final UserProduct userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER) {
            @Override
            protected boolean isUserSignedIn(final User mUser, final Context context) {
                return true;
            }

            @Override
            protected boolean isValidaDate(final String date) {
                return true;
            }

            @NonNull
            @Override
            UserProduct getUserProduct() {
                return userProductMock;
            }

            @NonNull
            @Override
            GetRegisteredProductsListener getRegisteredProductsListener(final Context context, final Product product, final ProdRegListener appListener) {
                return prodRegListenerMock;
            }
        };
        Product productMock = mock(Product.class);
        userProduct.registerProduct(context, productMock, new ProdRegListener() {
            @Override
            public void onProdRegSuccess(final ResponseData responseData) {
            }

            @Override
            public void onProdRegFailed(final ErrorType errorType) {
            }
        });
        verify(userProductMock).getRegisteredProducts(context, prodRegListenerMock);
    }

    public void testGetRegisteredProductsListener() {
        Product product = mock(Product.class);
        ProdRegListener listener = mock(ProdRegListener.class);
        when(product.getCtn()).thenReturn("HD8967/09");
        GetRegisteredProductsListener getRegisteredProductsListener = userProduct.
                getRegisteredProductsListener(context, product, listener);
        RegisteredResponse responseMock = mock(RegisteredResponse.class);
        final RegisteredResponseData registeredResponseData = new RegisteredResponseData();
        registeredResponseData.setProductModelNumber("HD8967/09");
        final RegisteredResponseData registeredResponseData1 = new RegisteredResponseData();
        registeredResponseData1.setProductModelNumber("HD8968/09");
        final RegisteredResponseData registeredResponseData2 = new RegisteredResponseData();
        registeredResponseData2.setProductModelNumber("HD8969/09");
        RegisteredResponseData[] results = {registeredResponseData, registeredResponseData1, registeredResponseData2};
        when(responseMock.getResults()).thenReturn(results);
        getRegisteredProductsListener.getRegisteredProducts(responseMock);
        verify(listener).onProdRegFailed(ErrorType.PRODUCT_ALREADY_REGISTERED);
    }

    public void testGetRegisteredProductsListenerOnCtnNotRegistered() {
        Product product = mock(Product.class);
        ProdRegListener listener = mock(ProdRegListener.class);
        final ProdRegListener metadataListener = mock(ProdRegListener.class);
        when(product.getCtn()).thenReturn("HD8970/09");
        UserProduct userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER) {
            @NonNull
            @Override
            ProdRegListener getMetadataListener(final Context context, final Product product, final ProdRegListener appListener) {
                return metadataListener;
            }
        };
        GetRegisteredProductsListener getRegisteredProductsListener = userProduct.
                getRegisteredProductsListener(context, product, listener);
        RegisteredResponse responseMock = mock(RegisteredResponse.class);
        final RegisteredResponseData registeredResponseData = new RegisteredResponseData();
        registeredResponseData.setProductModelNumber("HD8967/09");
        final RegisteredResponseData registeredResponseData1 = new RegisteredResponseData();
        registeredResponseData1.setProductModelNumber("HD8968/09");
        final RegisteredResponseData registeredResponseData2 = new RegisteredResponseData();
        registeredResponseData2.setProductModelNumber("HD8969/09");
        RegisteredResponseData[] results = {registeredResponseData, registeredResponseData1, registeredResponseData2};
        when(responseMock.getResults()).thenReturn(results);
        getRegisteredProductsListener.getRegisteredProducts(responseMock);
        verify(product).getProductMetadata(context, metadataListener);
    }

    public void testHandleErrorCases() {
        ProdRegListener prodRegListenerMock = mock(ProdRegListener.class);
        userProduct.handleError(ErrorType.INVALID_CTN.getCode(), prodRegListenerMock);
        verify(prodRegListenerMock).onProdRegFailed(ErrorType.INVALID_CTN);
        userProduct.handleError(ErrorType.INVALID_SERIALNUMBER.getCode(), prodRegListenerMock);
        verify(prodRegListenerMock).onProdRegFailed(ErrorType.INVALID_SERIALNUMBER);
        userProduct.handleError(ErrorType.INVALID_VALIDATION.getCode(), prodRegListenerMock);
        verify(prodRegListenerMock).onProdRegFailed(ErrorType.INVALID_VALIDATION);
        userProduct.handleError(ErrorType.NO_INTERNET_AVAILABLE.getCode(), prodRegListenerMock);
        verify(prodRegListenerMock).onProdRegFailed(ErrorType.NO_INTERNET_AVAILABLE);
        userProduct.handleError(ErrorType.INTERNAL_SERVER_ERROR.getCode(), prodRegListenerMock);
        verify(prodRegListenerMock).onProdRegFailed(ErrorType.INTERNAL_SERVER_ERROR);
    }

    public void testGettingRegisteredListener() {
        GetRegisteredProductsListener getRegisteredProductsListener = mock(GetRegisteredProductsListener.class);
        userProduct.getRegisteredProducts(context, getRegisteredProductsListener);
        assertEquals(getRegisteredProductsListener, userProduct.getGetRegisteredProductsListener());
    }

    public void testReturnCorrectRequestType() {
        Product productMock = mock(Product.class);
        GetRegisteredProductsListener getRegisteredProductsListener = mock(GetRegisteredProductsListener.class);
        ProdRegListener prodRegListener = mock(ProdRegListener.class);
        userProduct.registerProduct(context, productMock, prodRegListener);
        assertTrue(userProduct.getRequestType().equals(ProdRegConstants.PRODUCT_REGISTRATION));

        userProduct.getRegisteredProducts(context, getRegisteredProductsListener);
        assertTrue(userProduct.getRequestType().equals(ProdRegConstants.FETCH_REGISTERED_PRODUCTS));
    }

    public void testGetProduct() {
        Product productMock = mock(Product.class);
        ProdRegListener prodRegListener = mock(ProdRegListener.class);
        userProduct.registerProduct(context, productMock, prodRegListener);
        assertTrue(userProduct.getProduct().equals(productMock));
    }

    public void testValidatingSerialNumber() {
        ProductMetadataResponseData data = mock(ProductMetadataResponseData.class);
        Product productMock = mock(Product.class);
        final ProdRegListener listener = new ProdRegListener() {
            @Override
            public void onProdRegSuccess(final ResponseData responseData) {
            }

            @Override
            public void onProdRegFailed(final ErrorType errorType) {
                assertEquals(ErrorType.MISSING_SERIALNUMBER, errorType);
            }
        };

        final ProdRegListener listener2 = new ProdRegListener() {
            @Override
            public void onProdRegSuccess(final ResponseData responseData) {
            }

            @Override
            public void onProdRegFailed(final ErrorType errorType) {
                assertEquals(ErrorType.INVALID_SERIALNUMBER, errorType);
            }
        };
        when(data.getRequiresSerialNumber()).thenReturn("true");
        userProduct.validateSerialNumberFromMetadata(data, productMock, listener);
        when(productMock.getSerialNumber()).thenReturn("1234");
        when(data.getSerialNumberFormat()).thenReturn("^[1]{1}[3-9]{1}[0-5]{1}[0-9]{1}$");
        userProduct.validateSerialNumberFromMetadata(data, productMock, listener2);
        when(productMock.getSerialNumber()).thenReturn("1344");
        assertTrue(userProduct.validateSerialNumberFromMetadata(data, productMock, listener2));
    }

    public void testValidatingPurchaseDate() {
        ProductMetadataResponseData data = mock(ProductMetadataResponseData.class);
        Product productMock = mock(Product.class);
        when(data.getRequiresDateOfPurchase()).thenReturn("true");
        final ProdRegListener listener = new ProdRegListener() {
            @Override
            public void onProdRegSuccess(final ResponseData responseData) {
            }

            @Override
            public void onProdRegFailed(final ErrorType errorType) {
                assertEquals(ErrorType.MISSING_DATE, errorType);
            }
        };
        assertFalse(userProduct.validatePurchaseDateFromMetadata(data, productMock, listener));

        when(productMock.getPurchaseDate()).thenReturn("2016-03-22");
        when(data.getRequiresDateOfPurchase()).thenReturn("false");
        assertTrue(userProduct.validatePurchaseDateFromMetadata(data, productMock, listener));
        verify(productMock, atLeastOnce()).setPurchaseDate(null);
    }

    public void testRegisteredTest() {
        UserProduct userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER) {
            @Override
            public String getLocale() {
                return "en_GB";
            }
        };
        RegisteredProductsRequest registeredProductsRequest = userProduct.getRegisteredProductsRequest(context);
        assertEquals(registeredProductsRequest.getCatalog(), Catalog.CONSUMER);
        assertEquals(registeredProductsRequest.getSector(), Sector.B2C);
        assertEquals(registeredProductsRequest.getLocale(), "en_GB");
    }

    public void testRegistrationRequestTest() {
        Product productMock = mock(Product.class);
        final String ctn = "HC5410/83";
        when(productMock.getCtn()).thenReturn(ctn);
        final String serialNumber = "1344";
        when(productMock.getSerialNumber()).thenReturn(serialNumber);
        when(productMock.getSector()).thenReturn(Sector.B2C);
        when(productMock.getCatalog()).thenReturn(Catalog.CONSUMER);
        when(productMock.getSerialNumber()).thenReturn(serialNumber);
        when(productMock.getLocale()).thenReturn("en_GB");
        RegistrationRequest registrationRequest = userProduct.getRegistrationRequest(context, productMock);
        assertEquals(registrationRequest.getCatalog(), Catalog.CONSUMER);
        assertEquals(registrationRequest.getSector(), Sector.B2C);
        assertEquals(registrationRequest.getLocale(), "en_GB");
        assertEquals(registrationRequest.getCtn(), ctn);
        assertEquals(registrationRequest.getProductSerialNumber(), serialNumber);
    }

    public void testModelMapping() {
        UserProduct userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER);
        userProduct.setLocale("en_GB");
        assertEquals(userProduct.getSector(), Sector.B2C);
        assertEquals(userProduct.getCatalog(), Catalog.CONSUMER);
        assertEquals(userProduct.getLocale(), "en_GB");
    }

    public void testIsCtnRegistered() {
        Product product = mock(Product.class);
        ProdRegListener prodRegListener = mock(ProdRegListener.class);
        when(product.getCtn()).thenReturn("HD8967/09");
        final RegisteredResponseData registeredResponseData = new RegisteredResponseData();
        registeredResponseData.setProductModelNumber("HD8967/09");
        final RegisteredResponseData registeredResponseData1 = new RegisteredResponseData();
        registeredResponseData1.setProductModelNumber("HD8968/09");
        final RegisteredResponseData registeredResponseData2 = new RegisteredResponseData();
        registeredResponseData2.setProductModelNumber("HD8969/09");
        RegisteredResponseData[] results = {registeredResponseData, registeredResponseData1, registeredResponseData2};
        assertTrue(userProduct.isCtnRegistered(results, product, prodRegListener));
    }

    public void testIsCtnNotRegistered() {
        Product product = mock(Product.class);
        ProdRegListener prodRegListener = mock(ProdRegListener.class);
        when(product.getCtn()).thenReturn("HD8970/09");
        final RegisteredResponseData registeredResponseData = new RegisteredResponseData();
        registeredResponseData.setProductModelNumber("HD8967/09");
        final RegisteredResponseData registeredResponseData1 = new RegisteredResponseData();
        registeredResponseData1.setProductModelNumber("HD8968/09");
        final RegisteredResponseData registeredResponseData2 = new RegisteredResponseData();
        registeredResponseData2.setProductModelNumber("HD8969/09");
        RegisteredResponseData[] results = {registeredResponseData, registeredResponseData1, registeredResponseData2};
        assertFalse(userProduct.isCtnRegistered(results, product, prodRegListener));
    }

    public void testGetPrxResponseListenerForRegisteredProducts() {
        GetRegisteredProductsListener getRegisteredProductsListener = mock(GetRegisteredProductsListener.class);
        ResponseListener responseListener = userProduct.getPrxResponseListenerForRegisteredProducts(getRegisteredProductsListener);
        RegisteredResponse registeredResponse = mock(RegisteredResponse.class);
        responseListener.onResponseSuccess(registeredResponse);
        verify(getRegisteredProductsListener).getRegisteredProducts(registeredResponse);
        responseListener.onResponseError("test", 10);
        verify(getRegisteredProductsListener).onErrorResponse("test", 10);
    }

    public void testGetPrxResponseListenerForRegisteringProducts() {
        final UserProduct userProductMock = mock(UserProduct.class);
        UserProduct userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER) {
            @NonNull
            @Override
            UserProduct getUserProduct() {
                return userProductMock;
            }
        };
        ProdRegListener prodRegListener = mock(ProdRegListener.class);
        ResponseListener responseListener = userProduct.getPrxResponseListener(prodRegListener);
        ResponseData responseData = mock(ResponseData.class);
        responseListener.onResponseSuccess(responseData);
        verify(prodRegListener).onProdRegSuccess(responseData);
        responseListener.onResponseError("test", 10);
        verify(userProductMock).handleError(10, prodRegListener);
    }

    public void testInvokingAccessTokenWhenExpired() {
        final UserProduct userProductMock = mock(UserProduct.class);
        UserProduct userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER) {
            @NonNull
            @Override
            UserProduct getUserProduct() {
                return userProductMock;
            }
        };
        ProdRegListener prodRegListener = mock(ProdRegListener.class);
        userProduct.handleError(500, prodRegListener);
        verify(userProductMock).onAccessTokenExpire(prodRegListener);
    }

    public void testGetUserRefreshedLoginSession() {
        final UserProduct userProductMock = mock(UserProduct.class);
        UserProduct userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER) {
            @NonNull
            @Override
            UserProduct getUserProduct() {
                return userProductMock;
            }
        };
        ProdRegListener prodRegListener = mock(ProdRegListener.class);
        RefreshLoginSessionHandler refreshLoginSessionHandler = userProduct.getRefreshLoginSessionHandler(prodRegListener, context);
        refreshLoginSessionHandler.onRefreshLoginSessionFailedWithError(50);
        verify(prodRegListener).onProdRegFailed(ErrorType.REFRESH_ACCESS_TOKEN_FAILED);
        refreshLoginSessionHandler.onRefreshLoginSessionSuccess();
        verify(userProductMock).retryRequests(context, prodRegListener);
    }

    public void testGetMetadataListener() {
        Product productMock = mock(Product.class);
        ProdRegListener prodRegListenerMock = mock(ProdRegListener.class);
        final UserProduct userProductMock = mock(UserProduct.class);
        final UserProduct userProduct = new UserProduct(Sector.B2C, Catalog.CONSUMER) {
            @Override
            protected boolean validatePurchaseDateFromMetadata(final ProductMetadataResponseData data, final Product product, final ProdRegListener listener) {
                return true;
            }

            @Override
            protected boolean validateSerialNumberFromMetadata(final ProductMetadataResponseData data, final Product product, final ProdRegListener listener) {
                return true;
            }

            @NonNull
            @Override
            UserProduct getUserProduct() {
                return userProductMock;
            }
        };
        ProdRegListener prodRegListener = userProduct.getMetadataListener(context, productMock, prodRegListenerMock);
        ProductMetadataResponse responseDataMock = mock(ProductMetadataResponse.class);
        prodRegListener.onProdRegSuccess(responseDataMock);
        verify(userProductMock).makeRegistrationRequest(context, productMock, prodRegListenerMock);
        prodRegListener.onProdRegFailed(ErrorType.METADATA_FAILED);
        verify(prodRegListenerMock).onProdRegFailed(ErrorType.METADATA_FAILED);
    }
}
