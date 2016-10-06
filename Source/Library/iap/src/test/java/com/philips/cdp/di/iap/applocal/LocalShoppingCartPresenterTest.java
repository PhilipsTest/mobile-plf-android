/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.applocal;

import com.philips.cdp.di.iap.cart.LocalShoppingCartPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LocalShoppingCartPresenterTest {
    LocalShoppingCartPresenter mLocalShoppingCartPresenter;

    @Before
    public void setUp() throws Exception {
        mLocalShoppingCartPresenter = new LocalShoppingCartPresenter(null, null);
    }

    @Test
    public void getCurrentCartDetails() throws Exception {
        mLocalShoppingCartPresenter.getCurrentCartDetails();
    }

    @Test
    public void deleteProduct() throws Exception {
        mLocalShoppingCartPresenter.deleteProduct(null);
    }

    @Test
    public void updateProductQuantity() throws Exception {
        mLocalShoppingCartPresenter.updateProductQuantity(null, 0, 20);
    }

    @Test
    public void addProductToCart() throws Exception {
        mLocalShoppingCartPresenter.addProductToCart(null, null, null, false);
    }

    @Test
    public void getProductCartCount() throws Exception {
        mLocalShoppingCartPresenter.getProductCartCount(null, null);
    }

    @Test
    public void buyProduct() throws Exception {
        mLocalShoppingCartPresenter.buyProduct(null, null, null);
    }

}