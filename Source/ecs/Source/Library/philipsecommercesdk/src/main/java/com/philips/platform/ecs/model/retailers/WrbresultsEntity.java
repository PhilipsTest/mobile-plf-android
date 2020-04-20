/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.retailers;

import java.io.Serializable;

public class WrbresultsEntity implements Serializable {
    private String Ctn;
    private String storeLocatorUrl;
    private String EloquaSiteURL;
    private String ShowBuyButton;
    private int EloquaSiteId;
    private boolean RetailStoreAvailableFlag;

    private TextsEntity Texts;
    private OnlineStoresForProductEntity OnlineStoresForProduct;

    public String getStoreLocatorUrl() {
        return storeLocatorUrl;
    }

    public String getEloquaSiteURL() {
        return EloquaSiteURL;
    }

    public String getShowBuyButton() {
        return ShowBuyButton;
    }

    public String getCtn() {
        return Ctn;
    }

    public int getEloquaSiteId() {
        return EloquaSiteId;
    }

    public boolean isRetailStoreAvailableFlag() {
        return RetailStoreAvailableFlag;
    }

    public TextsEntity getTexts() {
        return Texts;
    }

    public OnlineStoresForProductEntity getOnlineStoresForProduct() {
        return OnlineStoresForProduct;
    }

}
