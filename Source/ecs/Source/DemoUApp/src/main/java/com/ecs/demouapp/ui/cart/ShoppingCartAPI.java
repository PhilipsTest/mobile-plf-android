/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.cart;

import android.content.Context;

import com.philips.platform.ecs.model.cart.ECSEntries;
import com.philips.platform.ecs.model.products.ECSProduct;

public interface ShoppingCartAPI {
    void getCurrentCartDetails();

    void deleteProduct(ShoppingCartData summary);

    void deleteProduct(ECSEntries entriesEntity);
    void updateProductQuantity(ECSEntries entriesEntity, int count);

    void updateProductQuantity(ShoppingCartData data, int count, int quantityStatus);

    void addProductToCart(Context context, String productCTN, ECSCartListener iapHandlerListener,

                          boolean isFromBuyNow);

    void getProductCartCount(Context context, ECSCartListener iapHandlerListener);

    void addProductToCart(ECSProduct product, ECSCartListener iapHandlerListener);

    void getRetailersInformation(String ctn);

}
