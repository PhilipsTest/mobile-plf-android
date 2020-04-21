/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.address;

import java.io.Serializable;

public class DeliveryCost implements Serializable {
    private String currencyIso;
    private String formattedValue;
    private String priceType;
    private double value;

    public void setCurrencyIso(String currencyIso) {
        this.currencyIso = currencyIso;
    }

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public void setValue(double value) {
        this.value = value;
    }

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
