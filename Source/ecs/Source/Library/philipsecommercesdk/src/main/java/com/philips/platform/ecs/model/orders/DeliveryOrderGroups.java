/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import com.philips.platform.ecs.model.cart.ECSEntries;

import java.io.Serializable;
import java.util.List;

public class DeliveryOrderGroups implements Serializable {

    private static final long serialVersionUID = -9099016767693123719L;
    private Cost totalPriceWithTax;

    private List<ECSEntries> entries;

    public Cost getTotalPriceWithTax() {
        return totalPriceWithTax;
    }

    public List<ECSEntries> getEntries() {
        return entries;
    }

}
