/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.address;

import java.io.Serializable;
import java.util.List;

/**
 * The type Get delivery modes contains the list of delivery modes.
 */
public class GetDeliveryModes implements Serializable {

    private List<ECSDeliveryMode> deliveryModes;

    public void setDeliveryModes(List<ECSDeliveryMode> deliveryModes) {
        this.deliveryModes = deliveryModes;
    }

    public List<ECSDeliveryMode> getDeliveryModes() {
        return deliveryModes;
    }
}
