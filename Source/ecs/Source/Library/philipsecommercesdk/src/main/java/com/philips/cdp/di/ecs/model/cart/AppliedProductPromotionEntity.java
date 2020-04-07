/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */


package com.philips.cdp.di.ecs.model.cart;

import java.io.Serializable;

public class AppliedProductPromotionEntity implements Serializable {


    private String description;
    private PromotionEntity promotion;

    public String getDescription() {
        return description;
    }

    public PromotionEntity getPromotion() {
        return promotion;
    }

}
