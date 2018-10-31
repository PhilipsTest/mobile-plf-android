/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.prodreg.register;

import android.content.Context;
import android.support.annotation.NonNull;

import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.constants.RegistrationState;
import com.philips.cdp.prodreg.error.ErrorHandler;
import com.philips.cdp.prodreg.listener.MetadataListener;
import com.philips.cdp.prodreg.listener.ProdRegListener;
import com.philips.cdp.prodreg.listener.RegisteredProductsListener;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponse;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponseData;
import com.philips.cdp.prodreg.model.registerproduct.RegistrationResponse;
import com.philips.cdp.prodreg.model.registerproduct.RegistrationResponseData;
import com.philips.cdp.prodreg.prxrequest.RegistrationRequest;
import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.response.ResponseListener;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.UserLoginState;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserWithProductsTest {

    UserWithProducts userWithProducts;
    private Context context;
    private LocalRegisteredProducts localRegisteredProducts;
    private UserWithProducts userWithProductsMock;
    private ErrorHandler errorHandlerMock;
    private ProdRegListener prodRegListener;
    private User userMock;

    @Before
    public void setUp() throws Exception {
        context = mock(Context.class);
        userWithProductsMock = mock(UserWithProducts.class);
        userMock = mock(User.class);
        when(userMock.getJanrainUUID()).thenReturn("abcd1234");
        localRegisteredProducts = mock(LocalRegisteredProducts.class);
        prodRegListener = mock(ProdRegListener.class);
        errorHandlerMock = mock(ErrorHandler.class);
        when(userMock.getUserLoginState()).thenReturn(UserLoginState.USER_LOGGED_IN);
        userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {
            @NonNull
            @Override
            UserWithProducts getUserProduct() {
                return userWithProductsMock;
            }

            @NonNull
            @Override
            protected LocalRegisteredProducts getLocalRegisteredProductsInstance() {
                return localRegisteredProducts;
            }

            @Override
            protected ErrorHandler getErrorHandler() {
                return errorHandlerMock;
            }

            @NonNull
            @Override
            protected User getUser() {
                return userMock;
            }
        };
    }

    @Test
    public void testIsUserSignedIn() {
        final User userMock = mock(User.class);
        when(userMock.getUserLoginState()).thenReturn(UserLoginState.USER_LOGGED_IN);
        UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener);
        assertFalse(userWithProducts.isUserSignedIn(context));
        when(userMock.getUserLoginState()).thenReturn(UserLoginState.USER_NOT_LOGGED_IN);
        assertFalse(userWithProducts.isUserSignedIn(context));
    }

    @Test
    public void testSetUUID() {
        final User userMock = mock(User.class);
        when(userMock.getUserLoginState()).thenReturn(UserLoginState.USER_LOGGED_IN);
      //  when(userMock.getEmailOrMobileVerificationStatus()).thenReturn(true);
        when(userMock.getJanrainUUID()).thenReturn("Janrain_id");
        UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {
            @NonNull
            @Override
            protected User getUser() {
                return userMock;
            }
        };
        userWithProducts.setUuid();
        assertEquals(userWithProducts.getUuid(), "Janrain_id");
    }

    @Test
    public void testRegisterProductWhenNotSignedIn() {
        UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {
            @Override
            protected boolean isUserSignedIn(final Context context) {
                return false;
            }

            @NonNull
            @Override
            protected LocalRegisteredProducts getLocalRegisteredProductsInstance() {
                return localRegisteredProducts;
            }
        };
        Product productMock = new Product("", PrxConstants.Sector.B2C, PrxConstants.Catalog.CONSUMER);
        userWithProducts.registerProduct(productMock);
        assertEquals(userWithProducts.getRequestType(), (UserWithProducts.PRODUCT_REGISTRATION));
    }

    @Test
    public void testRegisterProductOnUserAlreadyRegistered() {
        final UserWithProducts userWithProductsMock = mock(UserWithProducts.class);
        final RegisteredProductsListener prodRegListenerMock = mock(RegisteredProductsListener.class);
        ArrayList<RegisteredProduct> registeredProducts = new ArrayList<>();
        Set<RegisteredProduct> registeredProductSet = new HashSet<>();
        registeredProducts.add(new RegisteredProduct(null, null, null));
        registeredProducts.add(new RegisteredProduct(null, null, null));
        registeredProductSet.add(new RegisteredProduct("ctn", null, null));
        registeredProductSet.add(new RegisteredProduct("ctn1", null, null));
        when(localRegisteredProducts.getRegisteredProducts()).thenReturn(registeredProducts);
        when(localRegisteredProducts.getUniqueRegisteredProducts()).thenReturn(registeredProductSet);
        final UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {
            @Override
            protected boolean isUserSignedIn(final Context context) {
                return true;
            }

            @NonNull
            @Override
            UserWithProducts getUserProduct() {
                return userWithProductsMock;
            }

            @NonNull
            @Override
            RegisteredProductsListener getRegisteredProductsListener(final RegisteredProduct product) {
                return prodRegListenerMock;
            }

            @NonNull
            @Override
            protected LocalRegisteredProducts getLocalRegisteredProductsInstance() {
                return localRegisteredProducts;
            }
        };

        final Product product = new Product("ctn", null, null);
        RegisteredProduct registeredProduct = mock(RegisteredProduct.class);
        when(userWithProductsMock.createDummyRegisteredProduct(product)).thenReturn(registeredProduct);
        when(registeredProduct.getRegisteredProductIfExists(localRegisteredProducts)).thenReturn(registeredProduct);
        when(registeredProduct.getRegistrationState()).thenReturn(RegistrationState.REGISTERED);
        userWithProducts.registerProduct(product);
        verify(userWithProductsMock).createDummyRegisteredProduct(product);
        verify(registeredProduct).getRegisteredProductIfExists(localRegisteredProducts);
        verify(prodRegListener).onProdRegFailed(registeredProduct, userWithProductsMock);
        testMapProductToRegisteredProduct(product);
        testThrowExceptionWhenListenerNull(userWithProducts, product);
    }

    @Test
    public void testRegisterProductOnValidInput() {
        final UserWithProducts userWithProductsMock = mock(UserWithProducts.class);
        final RegisteredProductsListener prodRegListenerMock = mock(RegisteredProductsListener.class);
        ArrayList<RegisteredProduct> registeredProducts = new ArrayList<>();
        Set<RegisteredProduct> registeredProductSet = new HashSet<>();
        registeredProducts.add(new RegisteredProduct(null, null, null));
        registeredProducts.add(new RegisteredProduct(null, null, null));
        registeredProductSet.add(new RegisteredProduct("ctn", null, null));
        registeredProductSet.add(new RegisteredProduct("ctn1", null, null));
        when(localRegisteredProducts.getRegisteredProducts()).thenReturn(registeredProducts);
        when(localRegisteredProducts.getUniqueRegisteredProducts()).thenReturn(registeredProductSet);
        final UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {
            @Override
            protected boolean isUserSignedIn(final Context context) {
                return true;
            }

            @NonNull
            @Override
            UserWithProducts getUserProduct() {
                return userWithProductsMock;
            }

            @NonNull
            @Override
            RegisteredProductsListener getRegisteredProductsListener(final RegisteredProduct product) {
                return prodRegListenerMock;
            }

            @NonNull
            @Override
            protected LocalRegisteredProducts getLocalRegisteredProductsInstance() {
                return localRegisteredProducts;
            }

            @Override
            public String getUuid() {
                return "1234";
            }
        };

        final Product product = new Product("ctn", null, null);
        RegisteredProduct registeredProduct = mock(RegisteredProduct.class);
        when(userWithProductsMock.isUserSignedIn(context)).thenReturn(true);
        when(userWithProductsMock.createDummyRegisteredProduct(product)).thenReturn(registeredProduct);
        when(registeredProduct.getRegisteredProductIfExists(localRegisteredProducts)).thenReturn(registeredProduct);
        when(registeredProduct.getRegistrationState()).thenReturn(RegistrationState.PENDING);
        when(registeredProduct.getUserUUid()).thenReturn("1234");
        userWithProducts.registerProduct(product);
        verify(userWithProductsMock).updateLocaleCache(registeredProduct, registeredProduct.getProdRegError(), RegistrationState.REGISTERING);
    }

    private void testThrowExceptionWhenListenerNull(final UserWithProducts userWithProducts, final Product product) {
        try {
            userWithProducts.registerProduct(product);
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            assertTrue(e.getMessage().equals("Listener not Set"));
        }
    }

    @Test
    public void testMapProductToRegisteredProduct(Product product) {
        RegisteredProduct registeredProduct = userWithProducts.createDummyRegisteredProduct(product);
        assertEquals(registeredProduct.getCtn(), product.getCtn());
        assertEquals(registeredProduct.getSerialNumber(), product.getSerialNumber());
        assertEquals(registeredProduct.getPurchaseDate(), product.getPurchaseDate());
        product = null;
        assertNull(userWithProducts.createDummyRegisteredProduct(product));
    }

//    @Test
//    public void
//    testGettingRegisteredListener() {
//        RegisteredProductsListener registeredProductsListener = mock(RegisteredProductsListener.class);
//        userWithProducts.getRegisteredProducts(registeredProductsListener);
//        assertEquals(registeredProductsListener, userWithProducts.getRegisteredProductsListener());
//    }

    @Test
    public void testReturnCorrectRequestType() {
        final Product product = new Product("ctn", null, null);
        final RegisteredProduct registeredProduct = new RegisteredProduct("ctn", null, null);
        RegisteredProductsListener registeredProductsListener = mock(RegisteredProductsListener.class);
        when(userWithProductsMock.createDummyRegisteredProduct(product)).thenReturn(registeredProduct);
        userWithProducts.registerProduct(product);
        assertTrue(userWithProducts.getRequestType() == UserWithProducts.PRODUCT_REGISTRATION);
        when(userMock.getUserLoginState()).thenReturn(UserLoginState.USER_NOT_LOGGED_IN);
        userWithProducts.getRegisteredProducts(registeredProductsListener);
        assertTrue(userWithProducts.getRequestType() != (UserWithProducts.FETCH_REGISTERED_PRODUCTS));
    }

    @Test
    public void testValidatingSerialNumber() {
        ProductMetadataResponseData data = mock(ProductMetadataResponseData.class);
        RegisteredProduct productMock = mock(RegisteredProduct.class);
        when(productMock.getSerialNumber()).thenReturn("1234");
        when(data.getRequiresSerialNumber()).thenReturn("true");
        when(data.getSerialNumberFormat()).thenReturn("");
        userWithProducts.isValidSerialNumber(data, productMock);
        when(productMock.getSerialNumber()).thenReturn("1234");
        when(data.getSerialNumberFormat()).thenReturn("^[1]{1}[3-9]{1}[0-5]{1}[0-9]{1}$");
        userWithProducts.isValidSerialNumber(data, productMock);
        when(productMock.getSerialNumber()).thenReturn("1344");
        assertTrue(userWithProducts.isValidSerialNumber(data, productMock));
    }

    @Test
    public void testValidatingPurchaseDate() {
        ProductMetadataResponseData data = mock(ProductMetadataResponseData.class);
        RegisteredProduct productMock = mock(RegisteredProduct.class);
        when(data.getRequiresDateOfPurchase()).thenReturn("true");
        when(productMock.getPurchaseDate()).thenReturn("2016-03-22");
        when(data.getRequiresDateOfPurchase()).thenReturn("false");
        assertTrue(userWithProducts.isValidPurchaseDate("2016-03-22"));
        when(data.getRequiresDateOfPurchase()).thenReturn("true");
        assertTrue(userWithProducts.isValidPurchaseDate("2016-03-22"));
    }

    @Test
    public void testRegistrationRequestTest() {
        RegisteredProduct productMock = mock(RegisteredProduct.class);
        final String ctn = "HC5410/83";
        when(productMock.getCtn()).thenReturn(ctn);
        final String serialNumber = "1344";
        final String purchaseDate = "2016-04-15";
        when(productMock.getSerialNumber()).thenReturn(serialNumber);
        when(productMock.getSector()).thenReturn(PrxConstants.Sector.B2C);
        when(productMock.getCatalog()).thenReturn(PrxConstants.Catalog.CONSUMER);
        when(productMock.getSerialNumber()).thenReturn(serialNumber);
        when(productMock.getPurchaseDate()).thenReturn(purchaseDate);
        when(productMock.getLocale()).thenReturn("en_GB");
        when(userWithProductsMock.getRegistrationChannel()).thenReturn("");
        RegistrationRequest registrationRequest = userWithProducts.getRegistrationRequest(context, productMock);
        assertEquals(registrationRequest.getCatalog(), PrxConstants.Catalog.CONSUMER);
        assertEquals(registrationRequest.getSector(), PrxConstants.Sector.B2C);
        assertEquals(registrationRequest.getCtn(), ctn);
        assertEquals(registrationRequest.getProductSerialNumber(), serialNumber);
        assertEquals(productMock.getPurchaseDate(), registrationRequest.getPurchaseDate());
    }

    @Test
    public void testIsCtnRegistered() {
        RegisteredProduct product = mock(RegisteredProduct.class);
        when(product.getCtn()).thenReturn("HD8967/09");
        when(product.getSerialNumber()).thenReturn("1234");
        ArrayList<RegisteredProduct> registeredProducts = new ArrayList<>();
        final RegisteredProduct registeredProduct = new RegisteredProduct("HD8970/09", null, null);
        registeredProduct.setRegistrationState(RegistrationState.REGISTERED);
        registeredProduct.setSerialNumber("1234");
        final RegisteredProduct registeredProduct1 = new RegisteredProduct("HD8969/09", null, null);
        registeredProduct1.setRegistrationState(RegistrationState.REGISTERED);
        registeredProduct1.setSerialNumber("1234");
        final RegisteredProduct registeredProduct2 = new RegisteredProduct("HD8968/09", null, null);
        registeredProduct2.setRegistrationState(RegistrationState.REGISTERED);
        registeredProduct2.setSerialNumber("1234");
        final RegisteredProduct registeredProduct3 = new RegisteredProduct("HD8967/09", null, null);
        registeredProduct3.setRegistrationState(RegistrationState.REGISTERED);
        registeredProduct3.setSerialNumber("1234");
        registeredProducts.add(registeredProduct);
        registeredProducts.add(registeredProduct1);
        registeredProducts.add(registeredProduct2);
        registeredProducts.add(registeredProduct3);
        assertTrue(userWithProducts.isCtnRegistered(registeredProducts, product).getRegistrationState() == RegistrationState.REGISTERED);
    }

    @Test
    public void testIsCtnNotRegistered() {
        RegisteredProduct product = mock(RegisteredProduct.class);
        when(product.getCtn()).thenReturn("HD8965/09");
        when(product.getSerialNumber()).thenReturn("1234");
        ArrayList<RegisteredProduct> registeredProducts = new ArrayList<>();
        final RegisteredProduct registeredProduct = new RegisteredProduct("HD8970/09", null, null);
        registeredProduct.setRegistrationState(RegistrationState.REGISTERED);
        final RegisteredProduct registeredProduct1 = new RegisteredProduct("HD8969/09", null, null);
        registeredProduct1.setRegistrationState(RegistrationState.REGISTERED);
        final RegisteredProduct registeredProduct2 = new RegisteredProduct("HD8968/09", null, null);
        registeredProduct2.setRegistrationState(RegistrationState.REGISTERED);
        final RegisteredProduct registeredProduct3 = new RegisteredProduct("HD8967/09", null, null);
        registeredProduct3.setRegistrationState(RegistrationState.REGISTERED);
        registeredProducts.add(registeredProduct);
        registeredProducts.add(registeredProduct1);
        registeredProducts.add(registeredProduct2);
        registeredProducts.add(registeredProduct3);
        assertFalse(userWithProducts.isCtnRegistered(registeredProducts, product).getRegistrationState() == RegistrationState.REGISTERED);
    }

//    @Test
//    public void testGetPrxResponseListenerForRegisteringProducts() {
//        final UserWithProducts userWithProductsMock = mock(UserWithProducts.class);
//        RegisteredProduct product = mock(RegisteredProduct.class);
//
//        ProdRegCache prodRegCacheMock = mock(ProdRegCache.class);
//        SecureStorageInterface ssInterface = mock(SecureStorageInterface.class);
//        SecureStorageInterface.SecureStorageError ssError = mock(SecureStorageInterface.SecureStorageError.class);
//        String key = ProdRegConstants.PRODUCT_REGISTRATION_KEY;
//        when(prodRegCacheMock.getStringData(key)).thenReturn("data");
//
//        when(prodRegCacheMock.getAppInfraSecureStorageInterface()).thenReturn(ssInterface);
//        when(prodRegCacheMock.getSecureStorageError()).thenReturn(ssError);
//        when(localRegisteredProducts.getProdRegCache()).thenReturn(prodRegCacheMock);
//
//        UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {
//            @NonNull
//            @Override
//            UserWithProducts getUserProduct() {
//                return userWithProductsMock;
//            }
//
//            @NonNull
//            @Override
//            protected LocalRegisteredProducts getLocalRegisteredProductsInstance() {
//                return localRegisteredProducts;
//            }
//
//            @Override
//            protected ErrorHandler getErrorHandler() {
//                return errorHandlerMock;
//            }
//        };
//        ResponseListener responseListener = userWithProducts.getPrxResponseListener(product);
//        RegistrationResponse responseData = mock(RegistrationResponse.class);
//        final RegistrationResponseData data = mock(RegistrationResponseData.class);
//        when(responseData.getData()).thenReturn(data);
//
//        when(data.getWarrantyEndDate()).thenReturn("2016-03-22");
//        userWithProducts.setCurrentRegisteredProduct(product);
//        responseListener.onResponseSuccess(responseData);
//        verify(product).setRegistrationState(RegistrationState.REGISTERED);
//        verify(localRegisteredProducts).updateRegisteredProducts(product);
//        verify(prodRegListener).onProdRegSuccess(product, userWithProductsMock);
//        verify(userWithProductsMock).mapRegistrationResponse(responseData, product);
//        responseListener.onResponseError(new PrxError("test", 10));
//        verify(errorHandlerMock).handleError(userWithProductsMock, product, 10);
//    }

    @Test
    public void testMapRegistrationResponse() {
        RegistrationResponse responseData = mock(RegistrationResponse.class);
        final RegistrationResponseData data = mock(RegistrationResponseData.class);
        when(responseData.getData()).thenReturn(data);
        when(data.getWarrantyEndDate()).thenReturn("2016-03-22");
        final RegisteredProduct registeredProduct = new RegisteredProduct("ctn", null, null);
        userWithProducts.mapRegistrationResponse(responseData, registeredProduct);
        assertEquals(registeredProduct.getEndWarrantyDate(), data.getWarrantyEndDate());
    }

    @Test
    public void testInvokingAccessTokenWhenExpired() {
        final UserWithProducts userWithProductsMock = mock(UserWithProducts.class);
        ProdRegListener prodRegListener = mock(ProdRegListener.class);

        RegisteredProduct product = new RegisteredProduct("ctn", null, null);
        final User userMock = mock(User.class);
        UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {
            @NonNull
            @Override
            UserWithProducts getUserProduct() {
                return userWithProductsMock;
            }

            @NonNull
            @Override
            protected User getUser() {
                return userMock;
            }
        };
        errorHandlerMock.handleError(userWithProductsMock, product, 403);
        RefreshLoginSessionHandler refreshLoginSessionHandler = mock(RefreshLoginSessionHandler.class);
        when(userWithProductsMock.getRefreshLoginSessionHandler(product, context)).thenReturn(refreshLoginSessionHandler);
        userWithProducts.onAccessTokenExpire(product);
        verify(userMock).refreshLoginSession(refreshLoginSessionHandler);
    }

    @Test
    public void testGetUserRefreshedLoginSession() {
        final UserWithProducts userWithProductsMock = mock(UserWithProducts.class);
        RegisteredProduct product = mock(RegisteredProduct.class);
        final LocalRegisteredProducts localRegisteredProductsMock = mock(LocalRegisteredProducts.class);
        UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {
            @NonNull
            @Override
            UserWithProducts getUserProduct() {
                return userWithProductsMock;
            }

            @NonNull
            @Override
            protected LocalRegisteredProducts getLocalRegisteredProductsInstance() {
                return localRegisteredProductsMock;
            }
        };
        userWithProducts.setRequestType(UserWithProducts.PRODUCT_REGISTRATION);
        RefreshLoginSessionHandler refreshLoginSessionHandler = userWithProducts.getRefreshLoginSessionHandler(product, context);
        refreshLoginSessionHandler.onRefreshLoginSessionFailedWithError(50);
        verify(userWithProductsMock).updateLocaleCache(product, ProdRegError.ACCESS_TOKEN_INVALID, RegistrationState.FAILED);
        verify(localRegisteredProductsMock).updateRegisteredProducts(product);
        refreshLoginSessionHandler.onRefreshLoginSessionSuccess();
        verify(userWithProductsMock).retryRequests(context, product);
    }

    String mCTN = "HD8967/01", mSerialNumber = "1344", mAccessToken = "abq21238xbshs";
    PrxConstants.Sector sector;
    PrxConstants.Catalog catalog;

    @Test
    public void testRegistrationRequest() {
        final RegistrationRequest registrationRequest = new RegistrationRequest(mCTN, mSerialNumber, sector, catalog);
        final RegisteredProduct productMock = mock(RegisteredProduct.class);
        final RequestManager requestManagerMock = mock(RequestManager.class);
        final ResponseListener responseListenerMock = mock(ResponseListener.class);
        final String locale = "en_GB";
        when(productMock.getLocale()).thenReturn(locale);
        final String purchase_Date = "2016-03-22";
        when(productMock.getPurchaseDate()).thenReturn(purchase_Date);
        final String serialNumber = "1344";
        when(productMock.getSerialNumber()).thenReturn(serialNumber);

        UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {

            @NonNull
            @Override
            protected RegistrationRequest getRegistrationRequest(final Context context, final RegisteredProduct registeredProduct) {
                return registrationRequest;
            }

            @NonNull
            @Override
            protected RequestManager getRequestManager(final Context context) {
                return requestManagerMock;
            }

            @NonNull
            @Override
            ResponseListener getPrxResponseListener(final RegisteredProduct product) {
                return responseListenerMock;
            }

            @NonNull
            @Override
            UserWithProducts getUserProduct() {
                return userWithProductsMock;
            }
        };
        userWithProducts.makeRegistrationRequest(context, productMock);
        verify(requestManagerMock).executeRequest(registrationRequest, responseListenerMock);
    }

//    @Test
//    public void testRetryMethod() {
//        final UserWithProducts userWithProductsMock = mock(UserWithProducts.class);
//        final Product product = new Product("ctn", null, null);
//        final RegisteredProduct registeredProduct = new RegisteredProduct("ctn", null, null);
//        ProdRegListener prodRegListenerMock = mock(ProdRegListener.class);
//        RegisteredProductsListener registeredProductsListenerMock = mock(RegisteredProductsListener.class);
//        UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {
//            @NonNull
//            @Override
//            UserWithProducts getUserProduct() {
//                return userWithProductsMock;
//            }
//
//            @NonNull
//            @Override
//            protected LocalRegisteredProducts getLocalRegisteredProductsInstance() {
//                return localRegisteredProducts;
//            }
//
//            @NonNull
//            @Override
//            protected User getUser() {
//                return userMock;
//            }
//        };
//        when(userWithProductsMock.createDummyRegisteredProduct(product)).thenReturn(registeredProduct);
//        userWithProducts.registerProduct(product);
//        userWithProducts.retryRequests(context, registeredProduct);
//        verify(userWithProductsMock).makeRegistrationRequest(context, registeredProduct);
//        userWithProducts.getRegisteredProducts(registeredProductsListenerMock);
//        userWithProducts.retryRequests(context, registeredProduct);
//        userWithProducts.setRequestType(-1);
//        userWithProducts.retryRequests(context, registeredProduct);
//        verify(prodRegListenerMock, never()).onProdRegFailed(registeredProduct, userWithProductsMock);
//    }

    @Test
    public void testUpdateLocaleCacheOnError() {
        RegisteredProduct registeredProduct = new RegisteredProduct("ctn", null, null);
        userWithProducts.updateLocaleCache(registeredProduct, ProdRegError.PRODUCT_ALREADY_REGISTERED, RegistrationState.FAILED);
        assertEquals(registeredProduct.getRegistrationState(), RegistrationState.FAILED);
        assertEquals(registeredProduct.getProdRegError(), ProdRegError.PRODUCT_ALREADY_REGISTERED);
        verify(localRegisteredProducts).updateRegisteredProducts(registeredProduct);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testCachedRegisterProducts() {
        RegisteredProduct registeredProduct = new RegisteredProduct("ctn", null, null);
        registeredProduct.setRegistrationState(RegistrationState.PENDING);
        registeredProduct.setUserUUid("abcd1234");
        registeredProduct.setSerialNumber("Serial");
        registeredProduct.setPurchaseDate("2098-03-22");
        RegisteredProduct registeredProduct1 = new RegisteredProduct("ctn1", null, null);
        registeredProduct1.setRegistrationState(RegistrationState.FAILED);
        registeredProduct1.setUserUUid("abcd1234");
        registeredProduct1.setSerialNumber("Serial1");
        registeredProduct1.setPurchaseDate("2098-04-22");
        ArrayList<RegisteredProduct> registeredProducts = new ArrayList<>();
        registeredProducts.add(registeredProduct);
        registeredProducts.add(registeredProduct1);
        when(userWithProductsMock.isUserSignedIn(context)).thenReturn(false);
        userWithProducts.setCurrentRegisteredProduct(registeredProduct);
        userWithProducts.registerCachedProducts(registeredProducts);

        verify(userWithProductsMock).updateLocaleCache(registeredProduct1, ProdRegError.USER_NOT_SIGNED_IN, RegistrationState.FAILED);
        verify(prodRegListener, Mockito.atLeastOnce()).onProdRegFailed(registeredProduct, userWithProductsMock);
        when(userWithProductsMock.isUserSignedIn(context)).thenReturn(true);
    }

    @Test
    public void testGetRegisteredProductsListener() {
        RegisteredProduct registeredProductMock = mock(RegisteredProduct.class);
        when(registeredProductMock.getCtn()).thenReturn("ctn");
        when(registeredProductMock.getSerialNumber()).thenReturn("serial");
        when(registeredProductMock.getRegistrationState()).thenReturn(RegistrationState.REGISTERED);
        RegisteredProductsListener registeredProductsListener = userWithProducts.getRegisteredProductsListener(registeredProductMock);
        final ArrayList<RegisteredProduct> registeredProducts = new ArrayList<>();
        registeredProducts.add(registeredProductMock);
        userWithProducts.setCurrentRegisteredProduct(registeredProductMock);
        registeredProductsListener.getRegisteredProducts(registeredProducts, 0);
        verify(prodRegListener).onProdRegFailed(registeredProductMock, userWithProductsMock);
    }

    @Test
    public void testGetMetadataListener() {
        RegisteredProduct productMock = new RegisteredProduct("ctn", null, null);
        final UserWithProducts userWithProductsMock = mock(UserWithProducts.class);
        final UserWithProducts userWithProducts = new UserWithProducts(context, userMock, prodRegListener) {
            @Override
            protected boolean isValidSerialNumber(final ProductMetadataResponseData data, final RegisteredProduct product) {
                return true;
            }

            @Override
            protected boolean isValidPurchaseDate(String date) {
                return true;
            }

            @NonNull
            @Override
            UserWithProducts getUserProduct() {
                return userWithProductsMock;
            }

            @Override
            protected ErrorHandler getErrorHandler() {
                return errorHandlerMock;
            }
        };
        MetadataListener metadataListener = userWithProducts.getMetadataListener(productMock);
        ProductMetadataResponse responseDataMock = mock(ProductMetadataResponse.class);
        metadataListener.onMetadataResponse(responseDataMock);
        verify(userWithProductsMock).makeRegistrationRequest(context, productMock);
        metadataListener.onErrorResponse("test", 8);
        verify(errorHandlerMock).handleError(userWithProductsMock, productMock, 8);
    }
}
