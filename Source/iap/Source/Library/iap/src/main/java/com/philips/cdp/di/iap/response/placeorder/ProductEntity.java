package com.philips.cdp.di.iap.response.placeorder;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ProductEntity {
    private boolean availableForPickup;
    private String code;
    private boolean purchasable;
    private StockEntity stock;
    private String url;

    public boolean isAvailableForPickup() {
        return availableForPickup;
    }

    public String getCode() {
        return code;
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
