/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.products;

import java.io.Serializable;

public class StockBeanEntity implements Serializable {
    private static final long serialVersionUID = -7507740046541258095L;
    private int stockLevel;
    private String stockLevelStatus;

    public int getStockLevel() {
        return stockLevel;
    }

    public String getStockLevelStatus() {
        return stockLevelStatus;
    }
}
