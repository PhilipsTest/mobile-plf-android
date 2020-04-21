/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.ecs.demouapp.ui.products;

import com.ecs.demouapp.ui.integration.ECSListener;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.products.ECSProducts;

import java.util.ArrayList;

public class ProductCatalogPresenter implements ProductCatalogAPI {


    @Override
    public void getCatalogCount(final ECSListener iapListener) {

    }


    @Override
    public void getProductCatalog(int currentPage, int pageSize, ECSCallback<ECSProducts, Exception> ecsCallback) {
        ECSUtility.getInstance().getEcsServices().fetchProducts(currentPage, pageSize, ecsCallback);
    }

    @Override
    public void getCategorizedProductList(ArrayList<String> ctnList, ECSCallback<ECSProducts, Exception> ecsCallback) {

    }

    @Override
    public void getCompleteProductList(ECSListener iapListener) {

    }

}
