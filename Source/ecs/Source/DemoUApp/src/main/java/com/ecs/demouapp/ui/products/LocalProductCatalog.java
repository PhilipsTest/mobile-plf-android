/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.products;

import com.ecs.demouapp.ui.integration.ECSListener;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.ecs.model.products.ECSProducts;

import java.util.ArrayList;
import java.util.List;

public class LocalProductCatalog implements ProductCatalogAPI {


    @Override
    public void getProductCatalog(int currentPage, int pageSize, ECSCallback<ECSProducts, Exception> ecsCallback) {

    }

    @Override
    public void getCategorizedProductList(final ArrayList<String> productList , ECSCallback<ECSProducts, Exception> ecsCallback) {


        ECSUtility.getInstance().getEcsServices().fetchProductSummaries(productList, new ECSCallback<List<ECSProduct>, Exception>() {
            @Override
            public void onResponse(List<ECSProduct> result) {

                ECSProducts products = new ECSProducts();
                products.setProducts(result);
                ecsCallback.onResponse(products);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        });
    }

    @Override
    public void getCompleteProductList(ECSListener iapListener) {

    }

    @Override
    public void getCatalogCount(ECSListener listener) {
    }

}
