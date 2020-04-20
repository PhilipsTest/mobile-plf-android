/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import com.philips.platform.ecs.model.products.ECSProduct;

import java.io.Serializable;

/**
 * Created by philips on 5/15/19.
 */

public class OrderEntry implements Serializable {

    private ECSProduct product;

    public ECSProduct getProduct() {
        return product;
    }

    public void setProduct(ECSProduct product) {
        this.product = product;
    }
}
