package com.ecs.demouapp.ui.response.placeorder;

import com.philips.platform.ecs.model.cart.TotalPriceEntity;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class UnconsignedEntriesEntity {
    private int entryNumber;

    private ProductEntity product;
    private int quantity;

    private TotalPriceEntity totalPrice;

    public void setEntryNumber(int entryNumber) {
        this.entryNumber = entryNumber;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(TotalPriceEntity totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getEntryNumber() {
        return entryNumber;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public TotalPriceEntity getTotalPrice() {
        return totalPrice;
    }
}
