/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.cart;

import java.io.Serializable;

public class StockEntity implements Serializable {
    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    private int stockLevel;
    private String stockLevelStatus;

    public int getStockLevel() {
        return stockLevel;
    }

    public String getStockLevelStatus() {
        return stockLevelStatus;
    }
}

