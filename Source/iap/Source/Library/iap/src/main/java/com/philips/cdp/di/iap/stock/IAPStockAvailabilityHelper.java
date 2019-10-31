/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.cdp.di.iap.stock;

import com.philips.cdp.di.iap.controller.ControllerFactory;

public class IAPStockAvailabilityHelper {

    final String IN_STOCK = "inStock";
    final String LOW_STOCK = "lowStock";

    public boolean isStockAvailable(String stockLevelStatus, int stockLevel) {

        if(stockLevelStatus == null && ControllerFactory.getInstance().isPlanB()){
            return true;
        }

        if(stockLevelStatus == null){
            return false;
        }

        if ((stockLevelStatus.equalsIgnoreCase(IN_STOCK) || stockLevelStatus.equalsIgnoreCase(LOW_STOCK)) && stockLevel > 0) {
            return true;
        }

        return false;
    }

    public boolean checkIfRequestedQuantityAvailable(String stockLevelStatus, int stockLevel, int quantity) {
        final boolean stockAvailable = isStockAvailable(stockLevelStatus, stockLevel);
        if(stockAvailable){

           return true;
        }
        return false;
    }
}
