/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import java.io.Serializable;

public class Promotion implements Serializable {
    private String code;
    private String description;
    private String endDate;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getEndDate() {
        return endDate;
    }
}
