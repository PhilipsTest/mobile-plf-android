/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.retailers;

import androidx.annotation.VisibleForTesting;

import java.io.Serializable;

public class ECSRetailer implements Serializable{

    private static final long serialVersionUID = -4549397314749988036L;
    private String name;
    private String availability;

    @VisibleForTesting
    public void setIsPhilipsStore(String isPhilipsStore) {
        this.isPhilipsStore = isPhilipsStore;
    }

    private String isPhilipsStore;
    private String philipsOnlinePrice;
    private int logoHeight;
    private int logoWidth;
    private String buyURL;
    private String logoURL;
    private String xactparam;

    public String getName() {
        return name;
    }

    public String getAvailability() {
        return availability;
    }

    public String getIsPhilipsStore() {
        return isPhilipsStore;
    }

    public String getPhilipsOnlinePrice() {
        return philipsOnlinePrice;
    }

    public int getLogoHeight() {
        return logoHeight;
    }

    public int getLogoWidth() {
        return logoWidth;
    }

    public String getBuyURL() {
        return buyURL;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public String getXactparam() {
        return xactparam;
    }
}
