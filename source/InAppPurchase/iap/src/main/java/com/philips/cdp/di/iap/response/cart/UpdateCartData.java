package com.philips.cdp.di.iap.response.cart;

/**
 * Created by 310228564 on 2/9/2016.
 */
public class UpdateCartData {
    private Entries entry;
    private int quantity;
    private int quantityAdded;
    private String statusCode;


    public Entries getEntry() {
        return entry;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getQuantityAdded() {
        return quantityAdded;
    }

    public String getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
