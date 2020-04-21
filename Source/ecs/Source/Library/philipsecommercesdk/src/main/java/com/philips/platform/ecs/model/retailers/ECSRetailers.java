/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.retailers;

import java.io.Serializable;
import java.util.List;

public class ECSRetailers implements Serializable {
    private List<ECSRetailer> Store;

    public List<ECSRetailer> getRetailerList() {
        return Store;
    }
}
