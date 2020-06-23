/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import com.philips.platform.ecs.model.products.ECSProduct;

import java.io.Serializable;

public class Entries implements Serializable {
    private static final long serialVersionUID = -2922644423462768633L;
    private int entryNumber;

    private ECSProduct product;
    private int quantity;

    private Cost totalPrice;



    public int getEntryNumber() {
        return entryNumber;
    }

    public ECSProduct getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Cost getTotalPrice() {
        return totalPrice;
    }

}
