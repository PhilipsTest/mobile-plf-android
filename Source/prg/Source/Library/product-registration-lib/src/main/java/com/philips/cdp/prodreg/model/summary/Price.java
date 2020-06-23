/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.prodreg.model.summary;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Price extends  com.philips.cdp.prxclient.datamodels.summary.Data implements Serializable {

    private static final long serialVersionUID = 5055423992827404506L;
    private String productPrice;
    private String displayPriceType;
    private String displayPrice;
    private String currencyCode;
    private String formattedPrice;
    private String formattedDisplayPrice;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The productPrice
     */
    public String getProductPrice() {
        return productPrice;
    }

    /**
     * @param productPrice The productPrice
     */
    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    /**
     * @return The displayPriceType
     */
    public String getDisplayPriceType() {
        return displayPriceType;
    }

    /**
     * @param displayPriceType The displayPriceType
     */
    public void setDisplayPriceType(String displayPriceType) {
        this.displayPriceType = displayPriceType;
    }

    /**
     * @return The displayPrice
     */
    public String getDisplayPrice() {
        return displayPrice;
    }

    /**
     * @param displayPrice The displayPrice
     */
    public void setDisplayPrice(String displayPrice) {
        this.displayPrice = displayPrice;
    }

    /**
     * @return The currencyCode
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * @param currencyCode The currencyCode
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * @return The formattedPrice
     */
    public String getFormattedPrice() {
        return formattedPrice;
    }

    /**
     * @param formattedPrice The formattedPrice
     */
    public void setFormattedPrice(String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }

    /**
     * @return The formattedDisplayPrice
     */
    public String getFormattedDisplayPrice() {
        return formattedDisplayPrice;
    }

    /**
     * @param formattedDisplayPrice The formattedDisplayPrice
     */
    public void setFormattedDisplayPrice(String formattedDisplayPrice) {
        this.formattedDisplayPrice = formattedDisplayPrice;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
