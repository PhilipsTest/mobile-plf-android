/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.cart;

import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.ecs.model.summary.Price;

import java.io.Serializable;

/**
 * Created by 310228564 on 2/9/2016.
 */
public class Entry implements Serializable {
    private int entryNumber;
    private ECSProduct product;
    private int quantity;
    private Price totalPrice;

    public int getEntryNumber() {
        return entryNumber;
    }

    public ECSProduct getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Price getTotalPrice() {
        return totalPrice;
    }
}
