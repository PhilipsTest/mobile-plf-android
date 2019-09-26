/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs.model.region;

import java.io.Serializable;
import java.util.List;

public class RegionsList implements Serializable {

    private List<ECSRegion> regions;

    public List<ECSRegion> getRegions() {
        return regions;
    }
}
