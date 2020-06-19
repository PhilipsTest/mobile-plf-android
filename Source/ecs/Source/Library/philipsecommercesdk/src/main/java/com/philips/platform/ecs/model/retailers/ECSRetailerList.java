/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.retailers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Ecs retailer list which contains the list of retailers selling the product
 * This object is returned when fetchRetailers
 */
public class ECSRetailerList implements Serializable {

    private static final long serialVersionUID = 2403127424945928917L;
    private WrbresultsEntity wrbresults;

    public WrbresultsEntity getWrbresults() {
        return wrbresults;
    }

    public List<ECSRetailer> getRetailers() {
        if (getWrbresults() != null && getWrbresults().getOnlineStoresForProduct() != null
                && getWrbresults().getOnlineStoresForProduct().getStores() != null && getWrbresults().getOnlineStoresForProduct().getStores().getRetailerList() != null) {

            return getWrbresults().getOnlineStoresForProduct().getStores().getRetailerList();
        }
        return new ArrayList<ECSRetailer>();
    }
}
