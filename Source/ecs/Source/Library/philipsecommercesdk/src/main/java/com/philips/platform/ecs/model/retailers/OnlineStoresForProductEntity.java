/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.retailers;

import java.io.Serializable;

public class OnlineStoresForProductEntity implements Serializable {
    private String excludePhilipsShopInWTB;
    private String showPrice;
    private String ctn;
    private ECSRetailers Stores;

    public String getExcludePhilipsShopInWTB() {
        return excludePhilipsShopInWTB;
    }

    public String getShowPrice() {
        return showPrice;
    }

    public String getCtn() {
        return ctn;
    }

    public ECSRetailers getStores() {
        return Stores;
    }
}
