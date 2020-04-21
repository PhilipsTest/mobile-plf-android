/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.products;



import com.ecs.demouapp.ui.integration.ECSListener;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.products.ECSProducts;

import java.util.ArrayList;

public interface ProductCatalogAPI {
    void getProductCatalog(int currentPage, int pageSize, ECSCallback<ECSProducts,Exception> ecsCallback);

    void getCategorizedProductList(ArrayList<String> productList,ECSCallback<ECSProducts, Exception> ecsCallback);

    void getCompleteProductList(ECSListener iapListener);

    void getCatalogCount(ECSListener listener);
}
