/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ecs.model.cart;

import androidx.annotation.VisibleForTesting;

import com.philips.platform.ecs.model.products.ECSProduct;

import java.io.Serializable;


/**
 * The type Ecs entries which contains product for which cart has to be updated.
 * This object is passed as input parameter for updateShoppingCart
 */
public class ECSEntries implements Serializable{


    private static final long serialVersionUID = 9115373408948680734L;
    private BasePriceEntity basePrice;
    private int entryNumber;

    private ECSProduct product;
    private int quantity;

    private TotalPriceEntity totalPrice;
    private boolean updateable;

    public BasePriceEntity getBasePrice() {
        return basePrice;
    }

    public int getEntryNumber() {
        return entryNumber;
    }

    @VisibleForTesting
    public void setEntryNumber(int entryNumber) {
        this.entryNumber = entryNumber;
    }

    public ECSProduct getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public TotalPriceEntity getTotalPrice() {
        return totalPrice;
    }

    public boolean isUpdateable() {
        return updateable;
    }

    public void setProduct(ECSProduct product) {
        this.product = product;
    }
}