/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import java.io.Serializable;

public class AppliedOrderPromotions implements Serializable {
    private static final long serialVersionUID = -7373359584619590504L;
    private String description;
    private Promotion promotion;

    public String getDescription() {
        return description;
    }

    public Promotion getPromotion() {
        return promotion;
    }
}
