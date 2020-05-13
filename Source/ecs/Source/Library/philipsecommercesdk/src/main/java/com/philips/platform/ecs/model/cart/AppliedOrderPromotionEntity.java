/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */


package com.philips.platform.ecs.model.cart;

import java.io.Serializable;

public class AppliedOrderPromotionEntity  implements Serializable {


    private static final long serialVersionUID = 4162683403373900131L;
    private String description;
    private PromotionEntity promotion;

    public String getDescription() {
        return description;
    }

    public PromotionEntity getPromotion() {
        return promotion;
    }

}
