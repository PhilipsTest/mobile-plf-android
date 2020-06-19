/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ecs.model.address;

import java.io.Serializable;
import java.util.List;

/**
 * The type Get shipping address data contains list of addresses.
 */
public class GetShippingAddressData implements Serializable {

    private static final long serialVersionUID = 4938645976384523792L;
    private List<ECSAddress> addresses;

    public List<ECSAddress> getAddresses() {
        return addresses;
    }
}
