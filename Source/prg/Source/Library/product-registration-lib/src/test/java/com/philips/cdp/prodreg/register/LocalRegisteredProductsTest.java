package com.philips.cdp.prodreg.register;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.constants.RegistrationState;
import com.philips.cdp.prodreg.localcache.ProdRegCache;
import com.philips.cdp.prodreg.model.registerproduct.Attributes;
import com.philips.cdp.prxclient.PrxConstants;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
public class LocalRegisteredProductsTest extends TestCase {


    private LocalRegisteredProducts localRegisteredProducts;
    private ProdRegCache prodRegCache;
    private HashSet<RegisteredProduct> registeredProducts = new HashSet<>();
    private Gson gson;
    private UserDataInterface userDataInterface;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        prodRegCache = mock(ProdRegCache.class);
        gson = new Gson();
        addDummyProjects();
        userDataInterface = mock(UserDataInterface.class);
        HashMap<String,Object> userdetailsMap = new HashMap<>();
        userdetailsMap.put(UserDetailConstants.UUID,"uuid");
        ArrayList<String> detailsKey = new ArrayList<>();
        detailsKey.add(UserDetailConstants.UUID);
        when(userDataInterface.getUserDetails(detailsKey)).thenReturn(userdetailsMap);

        localRegisteredProducts = new LocalRegisteredProducts(userDataInterface) {
            @Override
            protected Set<RegisteredProduct> getUniqueRegisteredProducts() {
                return registeredProducts;
            }

            @NonNull
            @Override
            protected Gson getGSon() {
                return gson;
            }

            @Override
            public ProdRegCache getProdRegCache() {
                return prodRegCache;
            }
        };
    }

    private void addDummyProjects() {
        registeredProducts.add(new RegisteredProduct("ctn", PrxConstants.Sector.B2C, PrxConstants.Catalog.CONSUMER));
        registeredProducts.add(new RegisteredProduct("ctn1", PrxConstants.Sector.B2C, PrxConstants.Catalog.CONSUMER));
        registeredProducts.add(new RegisteredProduct("ctn2", PrxConstants.Sector.B2C, PrxConstants.Catalog.CONSUMER));
    }

    @Test
    public void testStore() {
        RegisteredProduct product = new RegisteredProduct("ABC", PrxConstants.Sector.B2C, PrxConstants.Catalog.CONSUMER);
        product.setRegistrationState(RegistrationState.REGISTERED);
        localRegisteredProducts.store(product);
        assertEquals(registeredProducts.size(), 4);
        verify(prodRegCache).storeStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY, gson.toJson(registeredProducts));
    }

    @Test
    public void testUpdateRegisteredProducts() {
        RegisteredProduct product = new RegisteredProduct("ABC", PrxConstants.Sector.B2C, PrxConstants.Catalog.CONSUMER);
        product.setRegistrationState(RegistrationState.REGISTERED);
        localRegisteredProducts.updateRegisteredProducts(product);
        assertEquals(registeredProducts.size(), 4);
        verify(prodRegCache).storeStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY, gson.toJson(registeredProducts));
    }

    @Test
    public void testGetRegisteredProducts() {
        when(userDataInterface.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN.USER_LOGGED_IN);
        final RegisteredProduct[] registeredProducts = {new RegisteredProduct(null, null, null), new RegisteredProduct(null, null, null), new RegisteredProduct(null, null, null)};

        localRegisteredProducts = new LocalRegisteredProducts(userDataInterface) {
            @Override
            protected RegisteredProduct[] getRegisteredProducts(final Gson gson, final String data) {
                return registeredProducts;
            }

            @Override
            protected ProdRegCache getProdRegCache() {
                return prodRegCache;
            }
        };
        when(prodRegCache.getStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY)).thenReturn("");
        assertTrue(localRegisteredProducts.getRegisteredProducts().size() == 3);
//        when(userMock.isUserSignIn()).thenReturn(false);
        when(userDataInterface.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_NOT_LOGGED_IN);
        assertTrue(localRegisteredProducts.getRegisteredProducts().size() == 0);
    }

    @Test
    public void testGettingUniqueRegisteredProducts() {
        when(userDataInterface.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        final RegisteredProduct[] registeredProducts = {new RegisteredProduct("ctn", null, null), new RegisteredProduct("ctn", null, null), new RegisteredProduct("ctn", null, null)};

        localRegisteredProducts = new LocalRegisteredProducts(userDataInterface) {
            @Override
            protected RegisteredProduct[] getRegisteredProducts(final Gson gson, final String data) {
                return registeredProducts;
            }

            @Override
            public ProdRegCache getProdRegCache() {
                return prodRegCache;
            }
        };
        when(prodRegCache.getStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY)).thenReturn("");
        assertTrue(localRegisteredProducts.getUniqueRegisteredProducts().size() == 1);
    }

    @Test
    public void testRemoveProductFromCache() {
        RegisteredProduct registeredProductMock = new RegisteredProduct("ctn", null, null);
        when(userDataInterface.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        final ProdRegCache prodRegCacheMock = mock(ProdRegCache.class);
        localRegisteredProducts = new LocalRegisteredProducts(userDataInterface) {
            @Override
            protected Set<RegisteredProduct> getUniqueRegisteredProducts() {
                return registeredProducts;
            }

            @Override
            protected ProdRegCache getProdRegCache() {
                return prodRegCacheMock;
            }

            @NonNull
            @Override
            protected Gson getGSon() {
                return gson;
            }
        };
        localRegisteredProducts.removeProductFromCache(registeredProductMock);
        assertEquals(registeredProducts.size(), 2);
        verify(prodRegCacheMock).storeStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY, gson.toJson(registeredProducts));
    }
}
