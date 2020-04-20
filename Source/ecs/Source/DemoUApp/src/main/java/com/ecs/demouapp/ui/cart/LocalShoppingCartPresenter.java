/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.cart;

import android.content.Context;

import com.philips.platform.ecs.model.cart.ECSEntries;
import com.philips.platform.ecs.model.products.ECSProduct;

/**
 * For local store, we just need retailers url and details.
 * Retailer request doesn't depend on store and implemented in abstract class
 */
public class LocalShoppingCartPresenter extends AbstractShoppingCartPresenter{

    @SuppressWarnings("rawtypes")
    public LocalShoppingCartPresenter(Context context, ShoppingCartListener listener) {
        super(context, listener);
    }

    @Override
    public void getCurrentCartDetails() {
    //Do nothing
    }

    @Override
    public void deleteProduct(final ShoppingCartData summary) {
    //Do nothing
    }

    @Override
    public void deleteProduct(ECSEntries entriesEntity) {

    }

    @Override
    public void updateProductQuantity(ECSEntries entriesEntity, int count) {

    }

    @Override
    public void updateProductQuantity(final ShoppingCartData data, final int count, final int quantityStatus) {
    //Do nothing
    }

    @Override
    public void addProductToCart(Context context, String productCTN, ECSCartListener iapHandlerListener, boolean isFromBuyNow) {
    //Do nothing
    }

    @Override
    public void getProductCartCount(Context context, ECSCartListener iapHandlerListener) {
    //Do nothing
    }

    @Override
    public void addProductToCart(ECSProduct product, ECSCartListener iapHandlerListener) {
    //Do nothing
    }

}
