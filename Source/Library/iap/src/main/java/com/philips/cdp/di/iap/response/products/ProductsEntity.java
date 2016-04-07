/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.response.products;

public class ProductsEntity {
    private boolean availableForPickup;
    private String code;
    private String name;
    private PriceEntity price;
    private PriceRangeEntity priceRange;
    private boolean purchasable;
    private StockEntity stock;
    private String url;

    public boolean isAvailableForPickup() {
        return availableForPickup;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public PriceEntity getPrice() {
        return price;
    }

    public PriceRangeEntity getPriceRange() {
        return priceRange;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public StockEntity getStock() {
        return stock;
    }

    public String getUrl() {
        return url;
    }

}
