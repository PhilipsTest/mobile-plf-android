package com.philips.cdp.di.iap.response.products;

import java.util.List;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class Products {

    private String type;
    private CurrentQueryEntity currentQuery;
    private String freeTextSearch;
    private PaginationEntity pagination;
    private List<ProductsEntity> products;
    private List<SortsEntity> sorts;

    public String getType() {
        return type;
    }

    public CurrentQueryEntity getCurrentQuery() {
        return currentQuery;
    }

    public String getFreeTextSearch() {
        return freeTextSearch;
    }

    public PaginationEntity getPagination() {
        return pagination;
    }

    public List<ProductsEntity> getProducts() {
        return products;
    }

    public List<SortsEntity> getSorts() {
        return sorts;
    }
}
