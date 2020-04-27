/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ecs.model.cart;

import java.io.Serializable;


/**
 * The type Delivery cost entity which contains price related data
 */
public class DeliveryCostEntity implements Serializable {


    private static final long serialVersionUID = -294891733157681685L;
    private String currencyIso;

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    private String formattedValue;
    private String priceType;
    private double value;

    public String getCurrencyIso() {
        return currencyIso;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public String getPriceType() {
        return priceType;
    }

    public double getValue() {
        return value;
    }
}