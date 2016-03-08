package com.philips.cdp.di.iap.response.carts;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class BasePriceEntity {
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

    public void setValue(int value) {
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

