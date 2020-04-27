/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.region;

import java.io.Serializable;

/**
 * The type Ecs region contains region name and code.
 * This object is returned for fetchRegions.
 */
public class ECSRegion implements Serializable {
    private String isocode;
    private String name;

    public String getIsocode() {
        return isocode;
    }

    public String getName() {
        return name;
    }
}
