package com.philips.cdp.di.iap.response.cart;

/**
 * Created by 310228564 on 2/9/2016.
 */
public class Entry {
    private int entryNumber;
    private Product product;
    private int quantity;
    private Price totalPrice;

    public int getEntryNumber() {
        return entryNumber;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Price getTotalPrice() {
        return totalPrice;
    }
}
