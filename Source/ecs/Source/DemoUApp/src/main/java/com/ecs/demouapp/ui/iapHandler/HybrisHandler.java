/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.iapHandler;

import com.ecs.demouapp.ui.integration.ECSInterface;
import com.ecs.demouapp.ui.integration.ECSListener;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.ecs.model.products.ECSProducts;

import java.util.ArrayList;


public class HybrisHandler extends ECSInterface implements ECSExposedAPI {

    @Override
    public void getProductCartCount(final ECSListener iapListener) {

        ECSUtility.getInstance().getEcsServices().fetchShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart carts) {

                if (carts != null) {
                    int quantity = ECSUtility.getInstance().getQuantity(carts);
                    iapListener.onGetCartCount(quantity);
                } else {
                    iapListener.onFailure(5999);
                }
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                iapListener.onFailure(ecsError.getErrorcode());
            }
        });
    }

    @Override
    public void getCompleteProductList(final ECSListener iapListener) {

            ECSUtility.getInstance().getEcsServices().fetchProducts(0, 20, new ECSCallback<ECSProducts, Exception>() {
                @Override
                public void onResponse(ECSProducts result) {

                    ArrayList<String> ctns = new ArrayList<>();

                    for(ECSProduct product:result.getProducts()){
                        ctns.add(product.getCode());
                    }
                    iapListener.onGetCompleteProductList(ctns);
                }

                @Override
                public void onFailure(Exception error, ECSError ecsError) {

                }
            });
    }

}
