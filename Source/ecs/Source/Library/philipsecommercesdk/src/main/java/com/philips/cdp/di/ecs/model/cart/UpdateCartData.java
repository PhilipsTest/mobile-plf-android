/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs.model.cart;

import java.io.Serializable;

/**
 * Created by 310228564 on 2/9/2016.
 */
public class UpdateCartData implements Serializable {
    private Entry entry;
    private int quantity;
    private int quantityAdded;
    private String statusCode;


    public Entry getEntry() {
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
