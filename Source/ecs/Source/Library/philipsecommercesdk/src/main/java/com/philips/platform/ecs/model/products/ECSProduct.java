/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ecs.model.products;


import com.philips.platform.ecs.model.asset.Assets;
import com.philips.platform.ecs.model.disclaimer.Disclaimers;
import com.philips.platform.ecs.model.summary.Data;

import java.io.Serializable;

/**
 * The type Ecs product contains summary data fetched from prx. This object is returned for fetchProduct and fetchProductSummaries.
 * It is passed as input parameter to get the product details including assets and disclaimer.
 * It is passed as input parameter for adding product to cart and to get list of retailers.
 */
public class ECSProduct implements Serializable {

    private static final long serialVersionUID = 8891698781011215830L;
    private boolean availableForPickup;
    private String code;
    private DiscountPriceEntity discountPrice;
    private String name;
    private PriceEntity price;
    private PriceRangeEntity priceRange;
    private StockEntity stock;
    private String url;

    private Data summary;

    public Assets getAssets() {
        return assets;
    }

    public void setAssets(Assets assets) {
        this.assets = assets;
    }

    public com.philips.platform.ecs.model.disclaimer.Disclaimers getDisclaimers() {
        return Disclaimers;
    }

    public void setDisclaimers(com.philips.platform.ecs.model.disclaimer.Disclaimers disclaimers) {
        Disclaimers = disclaimers;
    }

    private Assets assets;
    private Disclaimers Disclaimers;

    public boolean isAvailableForPickup() {
        return availableForPickup;
    }

    /**
     * Gets code.
     *
     * @return the product unique code (CTN)
     */
    public String getCode() {
        return code;
    }

    public String getCodeForBazaarVoice(){
        return this.code.replace("/","_");
    }

    public DiscountPriceEntity getDiscountPrice() {
        return discountPrice;
    }

    public String getName() {
        return name;
    }

    public void setPrice(PriceEntity price) {
        this.price = price;
    }

    public PriceEntity getPrice() {
        return price;
    }

    public PriceRangeEntity getPriceRange() {
        return priceRange;
    }

    public StockEntity getStock() {
        return stock;
    }

    public String getUrl() {
        return url;
    }

    public Data getSummary() {
        return summary;
    }

    public void setSummary(Data summary) {
        this.summary = summary;
    }

    public void setCode(String productCode) {
        this.code = productCode;
    }
}
