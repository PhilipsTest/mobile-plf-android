/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.prodreg.register;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.listener.ProdRegListener;
import com.philips.cdp.prodreg.localcache.ProdRegCache;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponseData;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ProdRegRegistrationControllerTest {

    private ProdRegRegistrationController prodRegRegistrationController;
    private FragmentActivity fragmentActivity;
    private ProdRegRegistrationController.RegisterControllerCallBacks registerControllerCallBacksMock;
    private RegisteredProduct registeredProductMock;
    private LocalRegisteredProducts localRegisteredProductsMock;
    private Bundle bundle;
    private ProductMetadataResponseData productMetadataResponseData;
    private Data summaryDataMock;
    private ProdRegCache prodRegCacheMock;

    @Before
    public void setUp() throws Exception {
        fragmentActivity = mock(FragmentActivity.class);
        prodRegCacheMock = mock(ProdRegCache.class);
        registerControllerCallBacksMock = mock(ProdRegRegistrationController.RegisterControllerCallBacks.class);
        registeredProductMock = mock(RegisteredProduct.class);
        localRegisteredProductsMock = mock(LocalRegisteredProducts.class);
        bundle = mock(Bundle.class);
        productMetadataResponseData = mock(ProductMetadataResponseData.class);
        summaryDataMock = mock(Data.class);
        UserDataInterface userDataInterface = mock(UserDataInterface.class);
        prodRegRegistrationController = new ProdRegRegistrationController(registerControllerCallBacksMock,fragmentActivity) {
            @Override
            public RegisteredProduct getRegisteredProduct() {
                return registeredProductMock;
            }

            @NonNull
            @Override
            protected LocalRegisteredProducts getLocalRegisteredProducts() {
                return localRegisteredProductsMock;
            }
        };
        when(fragmentActivity.isFinishing()).thenReturn(false);
        when(productMetadataResponseData.getRequiresDateOfPurchase()).thenReturn("true");
        when(productMetadataResponseData.getRequiresSerialNumber()).thenReturn("true");
        when(productMetadataResponseData.getSerialNumberFormat()).thenReturn("[0-9]-[0-9]-[0-9]");
        when(registeredProductMock.getSerialNumber()).thenReturn("1234");
        when(bundle.getSerializable(ProdRegConstants.PROD_REG_PRODUCT)).thenReturn(registeredProductMock);
        when(bundle.getSerializable(ProdRegConstants.PROD_REG_PRODUCT_METADATA)).thenReturn(productMetadataResponseData);
        when(bundle.getSerializable(ProdRegConstants.PROD_REG_PRODUCT_SUMMARY)).thenReturn(summaryDataMock);
    }

    @Test
    public void testHandleState() {
        when(registeredProductMock.isProductAlreadyRegistered(localRegisteredProductsMock)).thenReturn(true);
        prodRegRegistrationController.handleState();
        verify(registeredProductMock).getRegisteredProductIfExists(localRegisteredProductsMock);
        verify(registerControllerCallBacksMock).showAlreadyRegisteredDialog(registeredProductMock);
    }

    @Test
    public void testInit() {
        prodRegRegistrationController.init(null);
        verify(registerControllerCallBacksMock).exitProductRegistration();
        when(registeredProductMock.getSerialNumber()).thenReturn("1-2-3");
        prodRegRegistrationController.init(bundle);
        verify(registerControllerCallBacksMock).requireFields(true, false);
        verify(registerControllerCallBacksMock).setSummaryView(summaryDataMock);
        verify(registerControllerCallBacksMock).setProductView(registeredProductMock);
    }

    @Test
    public void testIsValidSerialNumber() {
        prodRegRegistrationController.init(bundle);
        assertFalse(prodRegRegistrationController.isValidSerialNumber("1234"));
//        verify(registerControllerCallBacksMock).isValidSerialNumber(false, "[0-9]-[0-9]-[0-9]", null);
    }

    @Test
    public void testIsValidDate() {
        assertTrue(prodRegRegistrationController.isValidDate("2016-01-22"));
        verify(registerControllerCallBacksMock).isValidDate(true);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testRegisterEvent() {
        UserWithProducts userWithProductsMock = mock(UserWithProducts.class);
        //when(prodRegHelperMock.getSignedInUserWithProducts(fragmentActivity)).thenReturn(userWithProductsMock);
        prodRegRegistrationController.init(bundle);
        prodRegRegistrationController.registerProduct("x2016-04-28", "1-2-3");
//        verify(registerControllerCallBacksMock).tagEvents("RegistrationEvent", AnalyticsConstants.SPECIAL_EVENTS, "extendWarrantyOption");
//        verify(registerControllerCallBacksMock).showLoadingDialog();
        verify(registerControllerCallBacksMock).logEvents(ProdRegRegistrationController.TAG, "Registering product with product details as CTN::" + registeredProductMock.getCtn());
        verify(userWithProductsMock).registerProduct(registeredProductMock);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGetProdRegListener() {
        ProdRegListener prodRegListener = prodRegRegistrationController.getProdRegListener();
        UserWithProducts userWithProductsMock = mock(UserWithProducts.class);
        prodRegListener.onProdRegSuccess(registeredProductMock, userWithProductsMock);
        verify(registerControllerCallBacksMock).logEvents(ProdRegRegistrationController.TAG, "Product registered successfully");
//        verify(registerControllerCallBacksMock).dismissLoadingDialog();
        verify(registerControllerCallBacksMock).showSuccessLayout();

        when(registeredProductMock.getProdRegError()).thenReturn(ProdRegError.PRODUCT_ALREADY_REGISTERED);
        prodRegListener.onProdRegFailed(registeredProductMock, userWithProductsMock);
        verify(registerControllerCallBacksMock).logEvents(ProdRegRegistrationController.TAG, "Product registration failed");
        verify(registerControllerCallBacksMock, atLeastOnce()).showAlreadyRegisteredDialog(registeredProductMock);

        when(registeredProductMock.getProdRegError()).thenReturn(ProdRegError.INVALID_CTN);
        prodRegListener.onProdRegFailed(registeredProductMock, userWithProductsMock);
        verify(registerControllerCallBacksMock).showAlertOnError(registeredProductMock.getProdRegError().getCode());
    }

    @Test
    public void testGetMethods() {
        assertTrue(prodRegRegistrationController.getLocalRegisteredProducts() != null);
        assertTrue(prodRegRegistrationController.getRegisteredProduct() != null);
    }
}
