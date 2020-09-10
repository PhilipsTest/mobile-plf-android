/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ecs.model.cart;

import java.io.Serializable;


/**
 * The type Delivery mode entity which contains delivery mode description, name, unique code
 */
public class DeliveryModeEntity implements Serializable{


    private static final long serialVersionUID = 183875197064381903L;
    private String code;

    private DeliveryCostEntity deliveryCost;
    private String description;
    private String name;
    private boolean pickupPoint;

    public String getCode() {
        return code;
    }

    public DeliveryCostEntity getDeliveryCost() {
        return deliveryCost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(boolean pickupPoint) {
        this.pickupPoint = pickupPoint;
    }
}
