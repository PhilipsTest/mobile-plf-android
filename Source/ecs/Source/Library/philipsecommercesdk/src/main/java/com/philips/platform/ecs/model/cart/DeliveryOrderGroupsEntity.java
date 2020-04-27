/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.cart;

import java.io.Serializable;
import java.util.List;

public class DeliveryOrderGroupsEntity   implements Serializable {

    private TotalPriceWithTaxEntity totalPriceWithTax;

    private List<ECSEntries> entries;

    public TotalPriceWithTaxEntity getTotalPriceWithTax() {
        return totalPriceWithTax;
    }

    public List<ECSEntries> getEntries() {
        return entries;
    }
}

