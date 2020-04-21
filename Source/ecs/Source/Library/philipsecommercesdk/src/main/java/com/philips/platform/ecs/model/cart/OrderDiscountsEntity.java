/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.cart;

import java.io.Serializable;

/**
 * The type Order discounts entity which contains discount data
 */
public class OrderDiscountsEntity implements Serializable {
    private String currencyIso;
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
