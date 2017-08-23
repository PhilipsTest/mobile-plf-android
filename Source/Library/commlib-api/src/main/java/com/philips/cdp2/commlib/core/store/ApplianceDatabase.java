/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

/*
 * © Koninklijke Philips N.V., 2015.
 *   All rights reserved.
 */

package com.philips.cdp2.commlib.core.store;

import com.philips.cdp2.commlib.core.appliance.Appliance;

public interface ApplianceDatabase {

    long save(Appliance appliance);

    void loadDataForAppliance(Appliance appliance);

    int delete(Appliance appliance);

}
